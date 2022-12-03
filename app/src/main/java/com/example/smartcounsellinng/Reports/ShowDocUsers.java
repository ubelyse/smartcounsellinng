package com.example.smartcounsellinng.Reports;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.example.smartcounsellinng.Activities.RegisterActivity;
import com.example.smartcounsellinng.Adapters.UserDocadapter;
import com.example.smartcounsellinng.Adapters.Useradapter;
import com.example.smartcounsellinng.Adapters.myadapter;
import com.example.smartcounsellinng.Models.Doctor;
import com.example.smartcounsellinng.Models.User;
import com.example.smartcounsellinng.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

public class ShowDocUsers extends AppCompatActivity {

    RecyclerView recview;
    UserDocadapter adapter;
    FloatingActionButton fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_doc_users);

        setTitle("Search here...");

        recview=(RecyclerView)findViewById(R.id.recycleview);
        recview.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users"), User.class)
                        .build();

        adapter=new UserDocadapter(options);
        recview.setAdapter(adapter);

//        fb=(FloatingActionButton)findViewById(R.id.fuadd);
//        fb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.searchmenu,menu);

        MenuItem item=menu.findItem(R.id.search);

        android.widget.SearchView searchView=(android.widget.SearchView)item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String s) {

                processsearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                processsearch(s);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void processsearch(String s)
    {
        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("users").orderByChild("fullName").startAt(s).endAt(s+"\uf8ff"), User.class)
                        .build();

        adapter=new UserDocadapter(options);
        adapter.startListening();
        recview.setAdapter(adapter);

    }
}