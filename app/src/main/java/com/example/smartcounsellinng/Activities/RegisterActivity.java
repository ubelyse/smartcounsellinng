package com.example.smartcounsellinng.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcounsellinng.Fragments.DatePickerFragment;
import com.example.smartcounsellinng.Models.Account;
import com.example.smartcounsellinng.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RegisterActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    private EditText txtUserName, txtPassWord,txtRePassWord,txtFullName,txtAddress,txtPhoneNumber,txtDateofBirth,txtAge;
    private Button btnRegister, btnCancel;
    private RadioGroup radioGroupdiseases;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Date date;
    private String dateOfBirth;
    private boolean disease=true;

    private ProgressDialog mAuthProgressDialog;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        createAuthProgressDialog();

        // works with firebase and database real time
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(); // root button
        mFirebaseAnalytics=FirebaseAnalytics.getInstance(this);

        txtUserName = findViewById(R.id.editTextUsername);
        txtPassWord = findViewById(R.id.editTextPassword);
        txtRePassWord = findViewById(R.id.editTextRetypePassword);
        txtFullName = findViewById(R.id.editTextFullName);
        txtAddress = findViewById(R.id.editTextAddress);
        txtPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        txtDateofBirth = findViewById(R.id.editTextDateOfBirth);
        txtDateofBirth.setOnFocusChangeListener(this);

        radioGroupdiseases = findViewById(R.id.radioGroupDisease);
        radioGroupdiseases.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int radioButtonID = radioGroupdiseases.getCheckedRadioButtonId();
                View radioButton = radioGroupdiseases.findViewById(radioButtonID);
                int idx = radioGroupdiseases.indexOfChild(radioButton);
                RadioButton r = (RadioButton)radioGroupdiseases.getChildAt(idx);
                String selectedtext = r.getText().toString();
                if(selectedtext.equals("Addict")){
                    disease = true;
                }
                else{
                    disease = false;
                }
            }
        });

        mFirebaseAnalytics.setUserProperty("dateOfBirth", dateOfBirth);

        btnCancel = findViewById(R.id.buttonCancelRegister);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(iLogin);
                finish();
            }
        });

        btnRegister = findViewById(R.id.buttonRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = txtUserName.getText().toString().trim();
                final String password = txtPassWord.getText().toString().trim();
                final String repassword = txtRePassWord.getText().toString().trim();
                final String fullname = txtFullName.getText().toString().trim();
                final String address = txtAddress.getText().toString().trim();
                final String phonenumber = txtPhoneNumber.getText().toString().trim();
                date = new Date();
                // on the ladder
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    dateOfBirth = txtDateofBirth.getText().toString().trim();
                    date = formatter.parse(dateOfBirth);
                }
                catch (Exception e){
                    e.printStackTrace();
                }


                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Please enter your email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(fullname)) {
                    Toast.makeText(getApplicationContext(), "Please enter your Names!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(getApplicationContext(), "Please enter your Address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(phonenumber)) {
                    Toast.makeText(getApplicationContext(), "Please enter your Phone Number!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Please enter a password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password must be longer than 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(repassword)) {
                    Toast.makeText(getApplicationContext(), "password incorrect!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //create user
                mAuthProgressDialog.show();
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                mAuthProgressDialog.dismiss();

                                if (!task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Registration error, please check again. Each email only registered 1 time only!",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Registration is complete!", Toast.LENGTH_SHORT).show();
                                    Account account = new Account(email, "", fullname, disease, address, phonenumber, dateOfBirth);
                                    String uid = firebaseAuth.getCurrentUser().getUid();
                                    databaseReference.child("users").child(uid).setValue(account);
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    finish();
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

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()){
            case R.id.editTextDateOfBirth:
                if(hasFocus){
                    DatePickerFragment datePickerFragment = new DatePickerFragment();
                    datePickerFragment.show(getSupportFragmentManager(),"Date of birth");
                }
                break;
        }
    }
}
