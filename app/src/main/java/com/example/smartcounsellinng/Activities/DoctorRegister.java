package com.example.smartcounsellinng.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.smartcounsellinng.Fragments.DatePickerFragment;
import com.example.smartcounsellinng.Models.Doctor;
import com.example.smartcounsellinng.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class DoctorRegister extends AppCompatActivity implements View.OnFocusChangeListener {

    private EditText txtUserName, txtPassWord,txtRePassWord,txtFullName,txtAddress,txtPhoneNumber,txtDateofBirth;
    private CheckBox txtRole,txtRole1,txtRole2;
    private Button btnRegister, btnCancel;
    private RadioGroup radioGroupGender;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Date date;
    private String dateOfBirth;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_register);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getInstance().getReference();

        btnRegister = findViewById(R.id.buttonRegister);
        txtUserName = findViewById(R.id.editTextUsername);
        txtPassWord = findViewById(R.id.editTextPassword);
        txtRePassWord = findViewById(R.id.editTextRetypePassword);
        txtFullName = findViewById(R.id.editTextFullName);
        txtAddress = findViewById(R.id.editTextAddress);
        txtPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        txtRole = findViewById(R.id.checkbox_doctor);
        txtRole1=findViewById(R.id.checkbox_psychologist);
        txtRole2 = findViewById(R.id.checkbox_headdoc);
        txtDateofBirth = findViewById(R.id.editTextDateOfBirth);
        txtDateofBirth.setOnFocusChangeListener(this);

        btnCancel = findViewById(R.id.buttonCancelRegister);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iLogin = new Intent(DoctorRegister.this, Admin.class);
                startActivity(iLogin);
                finish();
            }
        });

        txtRole.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()){
                    txtRole1.setChecked(false);
                    txtRole2.setChecked(false);

                }
            }
        });

        txtRole1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()){
                    txtRole.setChecked(false);
                    txtRole2.setChecked(false);

                }
            }
        });

        txtRole2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()){
                    txtRole.setChecked(false);
                    txtRole1.setChecked(false);

                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = txtUserName.getText().toString().trim();
                final String password = txtPassWord.getText().toString().trim();
                final String repassword = txtRePassWord.getText().toString().trim();
                final String fullname = "Dr"+ txtFullName.getText().toString().trim();
                final String address = txtAddress.getText().toString().trim();
                final String phonenumber = txtPhoneNumber.getText().toString().trim();
                final String[] role = {""};

                if (!(txtRole.isChecked()||txtRole1.isChecked()||txtRole2.isChecked())){
                    Toast.makeText(DoctorRegister.this, "Select The Account type", Toast.LENGTH_SHORT).show();
                    return;
                }

                date = new Date();
                //
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    dateOfBirth = txtDateofBirth.getText().toString().trim();
                    date = formatter.parse(dateOfBirth);
                } catch (Exception e) {
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

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {


                                if (!task.isSuccessful()) {
                                    Toast.makeText(DoctorRegister.this, "Registration error, please check again. Each email only registered 1 time only!",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(DoctorRegister.this, "Registration is complete!",
                                            Toast.LENGTH_SHORT).show();
                                    if (txtRole.isChecked()){
                                        role[0] ="Addict Doctor";
                                    }
                                    else if(txtRole1.isChecked()){
                                        role[0] ="Psychologist";
                                    }
                                    else if(txtRole2.isChecked()){
                                        role[0] ="Head Doctor";
                                    }



                                    Doctor account = new Doctor(email, phonenumber, address, dateOfBirth, "", role[0], fullname);
                                    String uid = firebaseAuth.getCurrentUser().getUid();
                                    databaseReference.child("doctors").child(uid).setValue(account);
                                    startActivity(new Intent(DoctorRegister.this, Admin.class));
                                    finish();
                                }
                            }

                        });
            }
        });
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