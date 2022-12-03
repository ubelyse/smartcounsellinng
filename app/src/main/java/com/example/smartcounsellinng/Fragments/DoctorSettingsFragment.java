package com.example.smartcounsellinng.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartcounsellinng.Activities.ChangePassWordActivity;

import com.example.smartcounsellinng.Activities.LoginActivity;
import com.example.smartcounsellinng.R;
import com.example.smartcounsellinng.Reports.ShowDocUsers;
import com.example.smartcounsellinng.Reports.ShowUsers;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileOutputStream;

public class DoctorSettingsFragment extends Fragment implements View.OnClickListener{
    private Button btnLogout, btnChangePassWord;
    private Button btnPrivatePolicy, btnAboutApp,btnAccountUsersList;

    FirebaseAuth firebaseAuth;

    private DatabaseReference nodeMoreInfo, nodeMoreInfo2;

//    String status = "";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_doctor_settings, container, false);
        btnLogout = v.findViewById(R.id.btnLogout);
        btnChangePassWord = v.findViewById(R.id.btnAccountPolicy);
        btnAccountUsersList= v.findViewById(R.id.btnAccountUsersList);

        btnLogout.setOnClickListener(this);
        btnChangePassWord.setOnClickListener(this);;


        btnAccountUsersList.setOnClickListener(this);


        btnAboutApp = v.findViewById(R.id.btnAboutApp);

        btnAboutApp.setOnClickListener(this);

        return v;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogout:
                firebaseAuth.getInstance().signOut();
                saveFile();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
                break;

            case  R.id.btnAccountPolicy:
                Intent intent2 = new Intent(getActivity(), ChangePassWordActivity.class);
                startActivity(intent2);
                break;
            case  R.id.btnAccountUsersList:
                Intent intent3 = new Intent(getActivity(), ShowDocUsers.class);
                startActivity(intent3);
                break;
            case R.id.btnAboutApp:
                Toast.makeText(getActivity(),"SMART COUNSELLING \n WE GOT YOU COVERED",
                        Toast.LENGTH_SHORT).show();
                break;

        }
    }
    public  void saveFile()
    {
        try {

            // Opens a file recording stream.
            FileOutputStream out = getActivity().openFileOutput("session.txt", Context.MODE_PRIVATE);
            // Record data.
            String fulluser = "";
            out.write(fulluser.getBytes());
            out.close();
        } catch (Exception e) {
            Toast.makeText(getActivity(),"Error:"+ e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

}