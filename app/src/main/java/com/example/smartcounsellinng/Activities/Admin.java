package com.example.smartcounsellinng.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.smartcounsellinng.R;
import com.example.smartcounsellinng.Reports.DoctorReport;
import com.example.smartcounsellinng.Reports.ReportActivity;
import com.example.smartcounsellinng.Reports.ShowDoctors;
import com.example.smartcounsellinng.Reports.ShowUsers;
import com.example.smartcounsellinng.Reports.UserReport;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Repo;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.html.WebColors;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Admin extends AppCompatActivity {

    private DatabaseReference payRef;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    FirebaseAuth firebaseAuth;
    List<Constants> UsersList;
    private FirebaseAnalytics mFirebaseAnalytics;

    private MaterialCardView mseeuser,mseedoc,mdocreport,mreport,musereport;
    private Button logt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        mFirebaseAnalytics=FirebaseAnalytics.getInstance(this);

        mseeuser=findViewById(R.id.btnseeUsers);
        mseedoc=findViewById(R.id.btnseedoctors);
        musereport=findViewById(R.id.btnusereport);
        mdocreport=findViewById(R.id.btndocreport);
//        mreport=findViewById(R.id.btnreport);
        logt = findViewById(R.id.btn_logout);

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


//        mreport.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(Admin.this, ReportActivity.class);
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



    private void logout() {
        firebaseAuth.getInstance().signOut();
        Intent intent = new Intent(Admin.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}