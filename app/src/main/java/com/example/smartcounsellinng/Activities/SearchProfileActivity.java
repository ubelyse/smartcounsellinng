package com.example.smartcounsellinng.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartcounsellinng.Models.Account;
import com.example.smartcounsellinng.Models.FriendRequest;
import com.example.smartcounsellinng.Models.User;
import com.example.smartcounsellinng.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchProfileActivity extends AppCompatActivity{

    private TextView txtPhoneNumber, txtDateOfBirth, txtAddress,txtGender, txtFullName, txtDescription;
    private Button btnAddFriend, btnRejectFriend;
    private FirebaseAnalytics mFirebaseAnalytics;

    private Toolbar toolBarViewProfile;
    private ImageView btnBackSearchFriend;
    private RelativeLayout relativeLayout;
    private CircleImageView civAvatar;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference nodeRoot, nodeFriends, nodeFriendRequests;

    Bundle bundle;
    private String userEmail, userPhoneNumber, uidSearchFriend;

    private Account account = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_profile);

        mFirebaseAnalytics=FirebaseAnalytics.getInstance(this);

        toolBarViewProfile = (Toolbar)findViewById(R.id.toolBarViewProfile);
        setSupportActionBar(toolBarViewProfile);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // turns off the default toolbar title
        relativeLayout = findViewById(R.id.profile_layout_search);

        btnBackSearchFriend = (ImageView)findViewById(R.id.iconBackSearchFriend);
        //btnBackSearchFriend.setOnClickListener(this);
        civAvatar = findViewById(R.id.profile_search);

        txtFullName = (TextView)findViewById(R.id.textViewFullNameSearch);
//        txtGender = (TextView)findViewById(R.id.textViewGenderSearch);
        txtPhoneNumber = (TextView)findViewById(R.id.textViewPhoneNumberSearch);
        txtDateOfBirth = (TextView)findViewById(R.id.textViewDateofBirthSearch);
        txtAddress = (TextView)findViewById(R.id.textViewAddressSearch);
        txtDescription = (TextView)findViewById(R.id.textViewDescriptionSearch);

        bundle = getIntent().getExtras();
        nodeRoot = FirebaseDatabase.getInstance().getReference();
        nodeFriendRequests = FirebaseDatabase.getInstance().getReference().child("friend_requests");
        nodeFriends = FirebaseDatabase.getInstance().getReference().child("friends");

        if(bundle != null){
            userEmail = bundle.getString("username");
            userPhoneNumber = bundle.getString("phoneNumber");
            uidSearchFriend = bundle.getString("UID_Friend");

            mFirebaseAnalytics.logEvent("search",bundle);

            nodeRoot.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ///Load user information
                    DataSnapshot nodeUsers = dataSnapshot.child("users");
                    DataSnapshot nodeDoctors = dataSnapshot.child("doctors");

                    if (nodeUsers.exists()){
                        for (DataSnapshot nodeUIDs : nodeUsers.getChildren()) {
                            Account acc = nodeUIDs.getValue(Account.class);
                            Log.d("Check account:", acc.getUsername());
                            // check back below
                            if (acc.getUsername().equals(userEmail) || acc.getPhoneNumber().equals(userPhoneNumber)) {
                                account = acc;
                            }
                        }
                    }
                    else if (nodeDoctors.exists()){
                        for (DataSnapshot nodeUIDs : nodeUsers.getChildren()) {
                            Account acc = nodeUIDs.getValue(Account.class);
                            Log.d("Check account:", acc.getUsername());
                            // check back below
                            if (acc.getUsername().equals(userEmail) || acc.getPhoneNumber().equals(userPhoneNumber)) {
                                account = acc;
                            }
                        }
                    }

                    if (account != null) {
                        ////////Check user with uid to search for ///////
                        DataSnapshot nodeUserSearch = dataSnapshot.child("users");
                        for (DataSnapshot nodeUIDs : nodeUserSearch.getChildren()) {
                            User user = nodeUIDs.getValue(User.class);
                            if (user.getUsername().equals(account.getUsername())
                                    || user.getPhoneNumber().equals(account.getPhoneNumber())) {
                                uidSearchFriend = nodeUIDs.getKey();
                            }
                        }

                        txtFullName.setText(account.getFullName());
                        txtPhoneNumber.setText(account.getPhoneNumber() + "");
                        txtDateOfBirth.setText(account.getDateOfBirth());
                        txtAddress.setText(account.getAddress());
//                        String gender = account.isGender()? "Addict": "Depression";
//                        txtGender.setText(gender);
                        txtDescription.setText(account.getDescription());

                        getAvatar();
                        getBackground();

                        ///////Check the user in the list of friend_requests///////
                        DataSnapshot nodeRequests = dataSnapshot.child("friend_requests");
                        FriendRequest checkLogin = null, checkFriend = null;
                        for(DataSnapshot nodeSingleRequests : nodeRequests.getChildren()) {
                            FriendRequest temp = nodeSingleRequests.getValue(FriendRequest.class);
                            if (temp.getUidUserLogin().equals(FirebaseAuth.getInstance().getUid())
                                    && temp.getUidUserFriend().equals(uidSearchFriend)
                                    && temp.getStatus().equals("sent")) {
                                // send a friend request
                                checkLogin = temp;
                            }
                            if (temp.getUidUserLogin().equals(FirebaseAuth.getInstance().getUid())
                                    && temp.getUidUserFriend().equals(uidSearchFriend)
                                    && temp.getStatus().equals("received")) {
                                // received a friend request
                                checkFriend = temp;
                            }
                        }
                        /////////////////////////////////////////////////////////////

                        ////////Check you are on your friends list/////
                        boolean isFriend = false;
                        DataSnapshot nodeMatchingFriends = dataSnapshot.child("friends");
                        for (DataSnapshot nodeSingleFriends : nodeMatchingFriends.getChildren()) {
                            if (nodeSingleFriends.getKey().equals(FirebaseAuth.getInstance().getUid())) {
                                if (nodeSingleFriends.hasChild(uidSearchFriend)) {
                                    isFriend = true;
                                }
                            }
                        }
                        ////////////////////////////////////////////////////////////


                        //////Check out whether the friend has been sent////////
                        if(checkLogin == null && checkFriend == null){
                            if(isFriend){
                                btnAddFriend.setText("Unfriend");
                                btnRejectFriend.setVisibility(View.INVISIBLE);
                            }
                            else{
                                btnAddFriend.setText("Make friend");
                                btnRejectFriend.setVisibility(View.INVISIBLE);
                            }

                        }
                        else if (checkLogin != null && checkFriend == null){
                            btnAddFriend.setText("Canceling friend request");
                            btnRejectFriend.setVisibility(View.INVISIBLE);
                        }
                        else if(checkLogin == null && checkFriend != null){
                            btnAddFriend.setText("Agree to make friends");
                            btnRejectFriend.setVisibility(View.VISIBLE);
                        }
                        ////////////////////////////////////////////////////////////////

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void getAvatar()
    {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        StorageReference ref = storageReference.child("avatar").child(uidSearchFriend+"avatar.jpg");
        try {
            final long ONE_MEGABYTE = 1024 * 1024;
            ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Data for "images/island.jpg" is returns, use this as needed
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
                    // Handle any errors/
                }
            });

        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }
    public void getBackground()
    {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        StorageReference ref = storageReference.child("background").child(uidSearchFriend+"background.jpg");
        try {
            final long ONE_MEGABYTE = 1024 * 1024;
            ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Data for "images/island.jpg" is returns, use this as needed
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Bitmap bmp2 = bmp.createScaledBitmap(bmp,300,170,false);
                    Drawable temp = new BitmapDrawable(getResources(), bmp2);
                    relativeLayout.setBackground(temp);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    if(((StorageException) exception).getErrorCode()==-13010)
                    {
                        relativeLayout.setBackgroundResource(R.drawable.header);
                        return;
                    }
                    // Handle any errors
                }
            });

        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }
}
