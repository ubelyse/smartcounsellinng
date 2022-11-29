package com.example.smartcounsellinng.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcounsellinng.MainActivity;
import com.example.smartcounsellinng.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.scottyab.aescrypt.AESCrypt;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnSwitchRegister, btnLogin;
    EditText txtUserName, txtPassWord;
    FirebaseAuth firebaseAuth;
    TextView btnSwitchForgetPassWord;
    String email,password;
    DatabaseReference databaseReference;
    private FirebaseAnalytics mFirebaseAnalytics;
    FirebaseFirestore fStore;
    boolean valid = true;
    private ProgressDialog mAuthProgressDialog;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        createAuthProgressDialog();

        txtUserName = (EditText) findViewById(R.id.uemail);
        txtPassWord = (EditText) findViewById(R.id.upassword);
        mFirebaseAnalytics=FirebaseAnalytics.getInstance(this);
        btnSwitchRegister = (Button)findViewById(R.id.buttonSwitchRegister);
        btnSwitchRegister.setOnClickListener(this);
        btnSwitchForgetPassWord = (TextView) findViewById(R.id.forgot_password);
        btnSwitchForgetPassWord.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(LoginActivity.this,ReStorePassWordActivity.class);
                startActivity(intent);
                finish();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        btnLogin = (Button)findViewById(R.id.btn_login);

        if(readFile().equals("")) {

        }
        else
        {
            String messageAfterDecrypt="";
            try {
                messageAfterDecrypt = AESCrypt.decrypt("123", readFile());
            }catch (GeneralSecurityException e){
                //handle error - could be due to incorrect password or tampered encryptedMsg
            }
            if(messageAfterDecrypt!="") {
                String[] fulluser = messageAfterDecrypt.split("[ ]");
                email = fulluser[0].trim();
                password = fulluser[1].trim();
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    Toast.makeText(LoginActivity.this, "Your account information has been changed! Please check again!!",
                                            Toast.LENGTH_LONG).show();
                                }

                                else
                                {
                                    final FirebaseUser user = firebaseAuth.getCurrentUser();

//                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    String uid = firebaseAuth.getCurrentUser().getUid();
//                                    Bundle bundle = new Bundle();
//                                    bundle.putString("UID", uid);
//                                    intent.putExtras(bundle);
//                                    startActivity(intent);
//                                    mFirebaseAnalytics.logEvent("login",bundle);
//                                    finish();
//
                                    DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.child("users").child(uid).exists()){
                                                saveFile(email,password);
                                                Intent intent = new Intent(LoginActivity.this, MainPatientActivity.class);
                                                String uid = firebaseAuth.getCurrentUser().getUid();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("UID", uid);
                                                intent.putExtras(bundle);
                                                overridePendingTransition(R.anim.animation_in,R.anim.animation_out);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else if (snapshot.child("doctors").child(uid).exists()){
                                                saveFile(email,password);
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                String uid = firebaseAuth.getCurrentUser().getUid();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("UID", uid);
                                                intent.putExtras(bundle);
                                                overridePendingTransition(R.anim.animation_in,R.anim.animation_out);
                                                startActivity(intent);
                                                finish();
                                            }

                                            else if (snapshot.child("head doctors").child(uid).exists()){
                                                saveFile(email,password);
                                                Intent intent = new Intent(LoginActivity.this, HeadDoctorsAdmin.class);
                                                String uid = firebaseAuth.getCurrentUser().getUid();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("UID", uid);
                                                intent.putExtras(bundle);
                                                overridePendingTransition(R.anim.animation_in,R.anim.animation_out);
                                                startActivity(intent);
                                                finish();
                                            }

                                            else if(snapshot.child("admin").exists()){
                                                Intent intent=new Intent(LoginActivity.this,Admin.class);
                                                startActivity(intent);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                }
                            }

                        });
            }

        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = txtUserName.getText().toString();
                password = txtPassWord.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Please enter your email before logging in!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Please enter your password before logging in!", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuthProgressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                mAuthProgressDialog.dismiss();
                                if (!task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Wrong account information or password. Please check your internet again!!",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    String uid= task.getResult().getUser().getUid();
                                    final FirebaseUser user = firebaseAuth.getCurrentUser();

                                    DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.child("users").child(uid).exists()){
                                                saveFile(email,password);
                                                Intent intent = new Intent(LoginActivity.this, MainPatientActivity.class);
                                                String uid = firebaseAuth.getCurrentUser().getUid();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("UID", uid);
                                                intent.putExtras(bundle);
                                                overridePendingTransition(R.anim.animation_in,R.anim.animation_out);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else if (snapshot.child("doctors").child(uid).exists()){
                                                saveFile(email,password);
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                String uid = firebaseAuth.getCurrentUser().getUid();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("UID", uid);
                                                intent.putExtras(bundle);
                                                overridePendingTransition(R.anim.animation_in,R.anim.animation_out);
                                                startActivity(intent);
                                                finish();
                                            }

                                            else if (snapshot.child("head doctors").child(uid).exists()){
                                                saveFile(email,password);
                                                Intent intent = new Intent(LoginActivity.this, HeadDoctorsAdmin.class);
                                                String uid = firebaseAuth.getCurrentUser().getUid();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("UID", uid);
                                                intent.putExtras(bundle);
                                                overridePendingTransition(R.anim.animation_in,R.anim.animation_out);
                                                startActivity(intent);
                                                finish();
                                            }

                                            else if(snapshot.child("admin").exists()){
                                                Intent intent=new Intent(LoginActivity.this,Admin.class);
                                                startActivity(intent);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }
                            }
                        });
            }
        });
    }

    private void createAuthProgressDialog() {
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading...");
        mAuthProgressDialog.setMessage("Authenticating with Firebase...");
        mAuthProgressDialog.setCancelable(false);
    }

    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("Error");
            valid = false;
        }else {
            valid = true;
        }

        return valid;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonSwitchRegister:
                Intent iRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(iRegister);
        }
    }
    public  void saveFile(String email, String passWord)
    {
        try {

            // Opens a file recording stream.
            FileOutputStream out = this.openFileOutput("session.txt", MODE_PRIVATE);
            // Record data.
            String fulluser = email +" " + passWord;
            String encryptedMsg ="";
            try {
                encryptedMsg = AESCrypt.encrypt("123",fulluser);
                System.out.println("what is it: "+encryptedMsg);
            }catch (GeneralSecurityException e){
                //handle error
            }
            out.write(encryptedMsg.getBytes());
            out.close();
        } catch (Exception e) {
            Toast.makeText(this,"Error:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    private String readFile() {
        try {
            //Opens a file reading stream.
            FileInputStream in = this.openFileInput("session.txt");
            if(in==null)
                return "";
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            StringBuilder sb = new StringBuilder();
            String s = null;
            while ((s = br.readLine()) != null) {
                sb.append(s).append("\n");
            }

            return sb.toString();

        } catch (Exception e) {
            return "";
        }

    }
}
