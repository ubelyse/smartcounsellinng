package com.example.smartcounsellinng.Fragments;

import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartcounsellinng.Activities.ChatWithFriendActivity;
import com.example.smartcounsellinng.Adapters.ListRecentlyChatAdapter;
import com.example.smartcounsellinng.ChatWithHeadDoctorActivity;
import com.example.smartcounsellinng.Models.RecentlyChat;
import com.example.smartcounsellinng.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MessagesDoctorFragment extends Fragment implements ChildEventListener {

    private DatabaseReference nodeRoot;

    private List<RecentlyChat> recentlyChatList;
    private ListView listviewRecentlyChat;

    private ListRecentlyChatAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_messages_doctor, container, false);

        recentlyChatList = new ArrayList<>();
        listviewRecentlyChat = (ListView)v.findViewById(R.id.listViewRecentlyChat);
        listviewRecentlyChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecentlyChat recentlyChat = (RecentlyChat)parent.getAdapter().getItem(position);
                Intent iChat = new Intent(getActivity(), ChatWithHeadDoctorActivity.class);
                iChat.putExtra("UID_Friend",recentlyChat.getUidRecentlyChat());
                iChat.putExtra("Name_Friend",recentlyChat.getNameRecentlychat());
                iChat.putExtra("From","Message_Fragment");
                startActivity(iChat);
                getActivity().finish();
            }
        });
        adapter = new ListRecentlyChatAdapter(getActivity(),R.layout.item_recently_chat_in_list,recentlyChatList);
        listviewRecentlyChat.setAdapter(adapter);

        nodeRoot = FirebaseDatabase.getInstance().getReference().child("more_info")
                .child(FirebaseAuth.getInstance().getUid());

        nodeRoot.addChildEventListener(this);

        return v;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        recentlyChatList.clear();

        for(DataSnapshot data : dataSnapshot.getChildren()){
            RecentlyChat temp = data.getValue(RecentlyChat.class);
            recentlyChatList.add(temp);
        }

        Collections.sort(recentlyChatList, new Comparator<RecentlyChat>() {
            DateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            @Override
            public int compare(RecentlyChat o1, RecentlyChat o2) {
                try {
                    if(f.parse(o1.getLastMessageTime()).after(f.parse(o2.getLastMessageTime()))){
                        return -1;
                    }
                    else if(f.parse(o1.getLastMessageTime()).before(f.parse(o2.getLastMessageTime()))){
                        return 1;
                    }
                    return 0;
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        recentlyChatList.clear();
        nodeRoot.removeEventListener(this);
        nodeRoot.addChildEventListener(this);
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}