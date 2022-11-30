package com.example.smartcounsellinng.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.smartcounsellinng.R;
import com.example.smartcounsellinng.Reports.DoctorReport;
import com.example.smartcounsellinng.Reports.ShowDoctors;
import com.example.smartcounsellinng.Reports.ShowUsers;
import com.example.smartcounsellinng.Reports.UserReport;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class Admin extends AppCompatActivity {

    private DatabaseReference payRef;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    FirebaseAuth firebaseAuth;
    List<Constants> UsersList;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Button logt;

    private MaterialCardView mseeuser,mseedoc,mdocreport,musereport, btnheadDo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        mFirebaseAnalytics=FirebaseAnalytics.getInstance(this);

        mseeuser=findViewById(R.id.btnseeUsers);
        mseedoc=findViewById(R.id.btnseedoctors);
        musereport=findViewById(R.id.btnusereport);
        mdocreport=findViewById(R.id.btndocreport);
//        btnheadDo = findViewById(R.id.btnheadDoc);

        logt=findViewById(R.id.btn_logout);

        mAuth = FirebaseAuth.getInstance();

        UsersList = new ArrayList<>();

        mseeuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Admin.this, ShowUsers.class);
                startActivity(intent);
            }
        });

        mseedoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Admin.this, ShowDoctors.class);
                startActivity(intent);
            }
        });

        mdocreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Admin.this, DoctorReport.class);
                startActivity(intent);
            }
        });

        musereport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Admin.this, UserReport.class);
                startActivity(intent);
            }
        });

//        btnheadDo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(Admin.this, ShowHeadDoc.class);
//                startActivity(intent);
//            }
//        });


        logt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        firebaseAuth.getInstance().signOut();
        Intent intent = new Intent(Admin.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}