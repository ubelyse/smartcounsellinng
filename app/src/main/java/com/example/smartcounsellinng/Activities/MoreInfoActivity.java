package com.example.smartcounsellinng.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcounsellinng.Adapters.ListMediaStorageAdapter;
import com.example.smartcounsellinng.Adapters.UserDocadapter;
import com.example.smartcounsellinng.Models.Account;
import com.example.smartcounsellinng.Models.Message;
import com.example.smartcounsellinng.Models.User;
import com.example.smartcounsellinng.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MoreInfoActivity extends AppCompatActivity implements ValueEventListener {

    ImageView btnBackToMessage,editicon;
    TextView textViewNameFriend, profileMoreInfoMessage;
    CircleImageView avatarMoreInfoProfile;
    Intent iMoreInfo;
    private DatabaseReference nodeRoot, nodeUsers, nodeStatus;
    private String uidFriend, nameFriend, emailFriend, phoneNumberFriend;
    Account account=new Account();
    List<Message> listImages;
    ListView listShowImage;
    ListMediaStorageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);



        iMoreInfo = getIntent();

        if (!iMoreInfo.getStringExtra("UID").isEmpty() && !iMoreInfo.getStringExtra("Name").isEmpty()) {
            uidFriend = iMoreInfo.getStringExtra("UID");
            nameFriend = iMoreInfo.getStringExtra("Name");

            textViewNameFriend = (TextView) findViewById(R.id.nameMoreInfoProfile);
            textViewNameFriend.setText(nameFriend);
            textViewNameFriend.setOnClickListener(goToFriendProfile);

//            profileMoreInfoMessage = (TextView)findViewById(R.id.profileMoreInfoMessage);
//            profileMoreInfoMessage.setOnClickListener(goToFriendProfile);

            // set avt cho bạn
            avatarMoreInfoProfile = (CircleImageView)findViewById(R.id.avatarMoreInfoProfile);
            getAvatar(avatarMoreInfoProfile,uidFriend);
            avatarMoreInfoProfile.setOnClickListener(goToFriendProfile);

            btnBackToMessage = (ImageView) findViewById(R.id.btnBackToMessage);
            btnBackToMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent iChat = new Intent(MoreInfoActivity.this, ChatWithFriendActivity.class);
                    iChat.putExtra("UID_Friend",uidFriend);
                    iChat.putExtra("Name_Friend",nameFriend);
                    iChat.putExtra("From","MoreInfoMessage");
                    startActivity(iChat);
                    finish();
                }
            });

//            editicon = findViewById(R.id.editicon);
//
//            editicon.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    final DialogPlus dialogPlus=DialogPlus.newDialog(view.getContext())
//                            .setContentHolder(new ViewHolder(R.layout.dialogcontentpatient))
//                            .setExpanded(true,1100)
//                            .create();
//
////                    DialogPlus dialogPlus1 = DialogPlus.newDialog();
//
//                    View myview=dialogPlus.getHolderView();
//                    final EditText address=myview.findViewById(R.id.address);
//                    final EditText descri=myview.findViewById(R.id.descri);
//                    final EditText name=myview.findViewById(R.id.uname);
//                    final EditText phoneno=myview.findViewById(R.id.dphone);
////                    final EditText role=myview.findViewById(R.id.drole);
//                    final EditText email=myview.findViewById(R.id.uemail);
//
//                    Button submit=myview.findViewById(R.id.usubmit);
//
//                    address.setText(account.getAddress());
//                    descri.setText(account.getDescription());
//                    name.setText(nameFriend);
//                    phoneno.setText(phoneNumberFriend);
//                    email.setText(nameFriend);
////                role.setText(model.isGender());
//
//                    dialogPlus.show();
//                    submit.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Map<String,Object> map=new HashMap<>();
//                            map.put("address",address.getText().toString());
//                            map.put("description",descri.getText().toString());
//                            map.put("fullName",name.getText().toString());
//                            map.put("phoneNumber",phoneno.getText().toString());
//                            map.put("email",email.getText().toString());
////                        map.put("disease",role.getText().toString());
//
//                            FirebaseDatabase.getInstance().getReference().child("users")
//                                    .child(uidFriend).updateChildren(map)
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//                                            dialogPlus.dismiss();
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            dialogPlus.dismiss();
//                                        }
//                                    });
//                        }
//                    });
//
//
//                }
//            });

            listImages = new ArrayList<>();
            adapter = new ListMediaStorageAdapter(MoreInfoActivity.this,R.layout.item_image_in_list_storage,listImages);
            listShowImage = (ListView)findViewById(R.id.listImageStorage);
            listShowImage.setAdapter(adapter);


            nodeRoot = FirebaseDatabase.getInstance().getReference();
            nodeRoot.addValueEventListener(this);

            nodeUsers = FirebaseDatabase.getInstance().getReference();
            nodeUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    DataSnapshot nodeUser=dataSnapshot.child("users").child(uidFriend);
                    DataSnapshot nodeDoc=dataSnapshot.child("doctors").child(uidFriend);
//                    DataSnapshot nodeheadDo=dataSnapshot.child("head doctors").child(uidFriend);
                    for (DataSnapshot snapshot : nodeUser.getChildren()) {
                        if (snapshot.getKey().equals(uidFriend)) {
                            account = dataSnapshot.getValue(Account.class);
                        }
                    }

                    for (DataSnapshot snapshot : nodeDoc.getChildren()) {
                        if (snapshot.getKey().equals(uidFriend)) {
                            account = dataSnapshot.getValue(Account.class);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    View.OnClickListener goToFriendProfile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            nodeStatus = FirebaseDatabase.getInstance().getReference();
            nodeStatus.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    DataSnapshot nodeDoc = dataSnapshot.child("doctors").child(uidFriend);
                    DataSnapshot nodeUser = dataSnapshot.child("users").child(uidFriend);
//                    DataSnapshot nodeHeadDo = dataSnapshot.child("head doctors").child(uidFriend);
                    if (nodeDoc.exists()) {
                        //Account account=new Account();
                        Intent iFriendProfile = new Intent(MoreInfoActivity.this, SearchProfileActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("UID_Friend", uidFriend);
                        bundle.putString("username", account.getUsername());
                        bundle.putString("phoneNumber", account.getPhoneNumber());
                        bundle.putString("From", "MoreInfoMessage");
                        bundle.putString("Name_Friend", nameFriend);
                        iFriendProfile.putExtras(bundle);
                        startActivity(iFriendProfile);
                        finish();
                    }
                    else if (nodeUser.exists()) {
                        //Account account=new Account();
                        Intent iFriendProfile = new Intent(MoreInfoActivity.this, SearchProfileActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("UID_Friend", uidFriend);
                        bundle.putString("username", account.getUsername());
                        bundle.putString("phoneNumber", account.getPhoneNumber());
                        bundle.putString("From", "MoreInfoMessage");
                        bundle.putString("Name_Friend", nameFriend);
                        iFriendProfile.putExtras(bundle);
                        startActivity(iFriendProfile);
                        finish();
                    }

//                    else if (nodeHeadDo.exists()) {
//                        //Account account=new Account();
//                        Intent iFriendProfile = new Intent(MoreInfoActivity.this, SearchProfileActivity.class);
//                        Bundle bundle = new Bundle();
//                        bundle.putString("UID_Friend", uidFriend);
//                        bundle.putString("username", account.getUsername());
//                        bundle.putString("phoneNumber", account.getPhoneNumber());
//                        bundle.putString("From", "MoreInfoMessage");
//                        bundle.putString("Name_Friend", nameFriend);
//                        iFriendProfile.putExtras(bundle);
//                        startActivity(iFriendProfile);
//                        finish();
//                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    };

    public void getAvatar(final CircleImageView civAvatar, String uidRequest)
    {
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("avatar").child(uidRequest+"avatar.jpg");
        try {
            final long ONE_MEGABYTE = 1024 * 1024;
            ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    civAvatar.setImageBitmap(bmp);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    if(((StorageException) exception).getErrorCode()==-13010) {
                        civAvatar.setImageResource(R.drawable.avatar_default);
                        return;
                    }
                }
            });

        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        listImages.clear();
        DataSnapshot nodeMessage = dataSnapshot.child("messages");
        for(DataSnapshot nodeSingleMessage : nodeMessage.getChildren()){
            Message temp = nodeSingleMessage.getValue(Message.class);
            if(temp.isImage()){
                if((temp.getUidSender().equals(FirebaseAuth.getInstance().getUid())
                        && temp.getUidReceiver().equals(uidFriend))
                        || (temp.getUidSender().equals(uidFriend)
                        && temp.getUidReceiver().equals(FirebaseAuth.getInstance().getUid()))){
                    listImages.add(temp);
                }
            }

            if((temp.getUidSender().equals(FirebaseAuth.getInstance().getUid())
                    && temp.getUidReceiver().equals(uidFriend))
                    || (temp.getUidSender().equals(uidFriend)
                    && temp.getUidReceiver().equals(FirebaseAuth.getInstance().getUid()))){
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}