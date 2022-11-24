package com.example.smartcounsellinng.Reports;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.smartcounsellinng.Activities.LoginActivity;
import com.example.smartcounsellinng.Models.User;
import com.example.smartcounsellinng.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

public class UserReport extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private DatabaseReference payRef;
    List<User> paymentUsersList;
    public static String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static int PERMISSION_ALL = 12;
    public static File pFile;
    private File payfile;
    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_report);
        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        payRef = FirebaseDatabase.getInstance().getReference().child("users");
        pdfView = findViewById(R.id.payment_pdf_viewer);

        Button reportButton = findViewById(R.id.button_disable_report);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previewDisabledUsersReport();
            }
        });

        paymentUsersList = new ArrayList<>();
        //create files in charity care folder
        payfile = new File("/storage/emulated/0/Report/");
        //check if they exist, if not create them(directory)
        if ( !payfile.exists()) {
            payfile.mkdirs();
        }
        pFile = new File(payfile, "UserReport.pdf");
        //fetch payment and disabled users details;
        fetchPaymentUsers();
    }

    private void fetchPaymentUsers() {
        payRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User pays = new User();
//                    pays.setAddress(snapshot.child("address").getValue().toString());
//                    pays.setDateOfBirth(snapshot.child("dateOfBirth").getValue().toString());
                    pays.setFullName(snapshot.child("fullName").getValue().toString());
                    pays.setPhoneNumber(snapshot.child("phoneNumber").getValue().toString());
                    pays.setDateOfBirth(snapshot.child("dateOfBirth").getValue().toString());

//                    Log.d("Payment", "Address: " + pays.getAddress());
//                    Log.d("Payment", "dateOfBirth: " + pays.getDateOfBirth());
                    Log.d("Payment", "Full Name: " + pays.getFullName());
                    Log.d("Payment", "phoneNumber: " + pays.getPhoneNumber());
                    Log.d("Payment", "D.O.B: " + pays.getDateOfBirth());

                    paymentUsersList.add(pays);
                }
                try {
                    createPaymentReport(paymentUsersList);
                } catch (DocumentException | FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void createPaymentReport(  List<User> paymentUsersList) throws DocumentException, FileNotFoundException{
        BaseColor colorWhite = WebColors.getRGBColor("#ffffff");
        BaseColor colorBlue = WebColors.getRGBColor("#056FAA");
        BaseColor grayColor = WebColors.getRGBColor("#425066");

        Font white = new Font(Font.FontFamily.HELVETICA, 15.0f, Font.BOLD, colorWhite);
        FileOutputStream output = new FileOutputStream(pFile);
        Document document = new Document(PageSize.A4);
        PdfPTable table = new PdfPTable(new float[]{6, 25, 20, 20});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setFixedHeight(50);
        table.setTotalWidth(PageSize.A4.getWidth());
        table.setWidthPercentage(100);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);

        Chunk noText = new Chunk("No.", white);
        PdfPCell noCell = new PdfPCell(new Phrase(noText));
        noCell.setFixedHeight(50);
        noCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        noCell.setVerticalAlignment(Element.ALIGN_CENTER);

//        Chunk nameText = new Chunk("Address", white);
//        PdfPCell nameCell = new PdfPCell(new Phrase(nameText));
//        nameCell.setFixedHeight(50);
//        nameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        nameCell.setVerticalAlignment(Element.ALIGN_CENTER);
//
//        Chunk phoneText = new Chunk("dateOfBirth", white);
//        PdfPCell phoneCell = new PdfPCell(new Phrase(phoneText));
//        phoneCell.setFixedHeight(50);
//        phoneCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        phoneCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk amountText = new Chunk("Full Name", white);
        PdfPCell amountCell = new PdfPCell(new Phrase(amountText));
        amountCell.setFixedHeight(50);
        amountCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        amountCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk phno = new Chunk("phoneNumber", white);
        PdfPCell phnoCell = new PdfPCell(new Phrase(phno));
        phnoCell.setFixedHeight(50);
        phnoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        phnoCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk Rle = new Chunk("D.O.B", white);
        PdfPCell rleCell = new PdfPCell(new Phrase(Rle));
        rleCell.setFixedHeight(50);
        rleCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        rleCell.setVerticalAlignment(Element.ALIGN_CENTER);

        Chunk footerText = new Chunk("Copyright @ 2022");
        PdfPCell footCell = new PdfPCell(new Phrase(footerText));
        footCell.setFixedHeight(70);
        footCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        footCell.setVerticalAlignment(Element.ALIGN_CENTER);
        footCell.setColspan(7);


        table.addCell(noCell);
//        table.addCell(nameCell);
//        table.addCell(phoneCell);
        table.addCell(amountCell);
        table.addCell(phnoCell);
        table.addCell(rleCell);
        table.setHeaderRows(1);

        PdfPCell[] cells = table.getRow(0).getCells();


        for (PdfPCell cell : cells) {
            cell.setBackgroundColor(grayColor);
        }
        for (int i = 0; i < paymentUsersList.size(); i++) {
            User pay = paymentUsersList.get(i);

            String id = String.valueOf(i + 1);
            String address = pay.getAddress();
            String dateOfBirth = pay.getDateOfBirth();
            String fullName = pay.getFullName();
            String phoneNumber = pay.getPhoneNumber();
            String dob = pay.getDateOfBirth();


            table.addCell(id + ". ");
//            table.addCell(address);
//            table.addCell(dateOfBirth);
            table.addCell(fullName);
            table.addCell(phoneNumber);
            table.addCell(dob);

        }
        PdfPTable footTable = new PdfPTable(new float[]{6, 25, 20, 20});
        footTable.setTotalWidth(PageSize.A4.getWidth());
        footTable.setWidthPercentage(100);
        footTable.addCell(footCell);

        PdfWriter.getInstance(document, output);
        document.open();
        Font g = new Font(Font.FontFamily.HELVETICA, 25.0f, Font.NORMAL, grayColor);
        document.add(new Paragraph(" User Report\n\n", g));
        document.add(table);
        document.add(footTable);

        document.close();
    }
    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser ==null){
            SendUserToLoginActivity();
        }
        else{
            //checking if the user exists in the firebase database
            CheckUserExistence();
        }
    }
    private void CheckUserExistence() {
        //get the user id
        final String currentUserId = mAuth.getCurrentUser().getUid();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(currentUserId)){
                    //user is authenticated but but his record is not present in real time firebase database
                    SendUserToStepTwoAuthentication();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToStepTwoAuthentication()	    {
        /*Intent steptwoIntent = new Intent(ReportActivity.this, StepTwoAuthentication.class);
        steptwoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(steptwoIntent);
        finish();*/
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(UserReport.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    public void previewDisabledUsersReport() {
        if (hasPermissions(this, PERMISSIONS)) {
            DisplayReport();
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    private void DisplayReport() {
        pdfView.fromFile(pFile)
                .pages(0,2,1,3,3,3)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .load();



    }
}