package com.example.smartcounsellinng.Activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcounsellinng.Adapters.RecyclerListMessageAdapter;
import com.example.smartcounsellinng.Controllers.MessageController;
import com.example.smartcounsellinng.Fragments.AudioMessageFragment;
import com.example.smartcounsellinng.Interfaces.GetAudioFromRecordFragment;
import com.example.smartcounsellinng.MainActivity;
import com.example.smartcounsellinng.Models.Account;
import com.example.smartcounsellinng.Models.Message;
import com.example.smartcounsellinng.Models.RecentlyChat;
import com.example.smartcounsellinng.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

public class ChatWithFriendActivity extends AppCompatActivity implements ValueEventListener,
        View.OnClickListener, GetAudioFromRecordFragment {

    private Intent iChat;
    private String uidFriendChat, nameFriendChat;
    private FirebaseAnalytics mFirebaseAnalytics;
    private String uidFriend, nameFriend;

    private TextView textViewNameFriend;
    private ImageButton btnSendMessage, btnBackButton, btnSendImage, btnMoreInfo, btnSendAudio,btnSendImageWithCamera;
    private EditText editTextMessage;
    private RecyclerView recyclerViewMessage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private DatabaseReference nodeRoot, nodeUsers, nodeStatus;
    private ProgressDialog mAuthProgressDialog;

    private DatabaseReference nodeRefreshMessage, nodeMessage, nodeInfoMine, nodeInfoFriend, nodeGetMyName,nodeGetName;
    private String myName = "";

    private MessageController messageController;

    private Uri filePath;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    Account account;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_with_friend);
        mFirebaseAnalytics=FirebaseAnalytics.getInstance(this);

        iChat= getIntent();
        uidFriendChat = iChat.getStringExtra("UID_Friend");
        nameFriendChat = iChat.getStringExtra("Name_Friend");

        Bundle bundle = new Bundle();


        recyclerViewMessage = (RecyclerView)findViewById(R.id.recyclerViewMessage);


        textViewNameFriend = (TextView)findViewById(R.id.textViewNameFriend);
        textViewNameFriend.setText(nameFriendChat + "");

        btnSendMessage = (ImageButton)findViewById(R.id.btnSendMessage);
        btnSendMessage.setOnClickListener(this);

        btnSendImageWithCamera = (ImageButton)findViewById(R.id.btnOpenCamera);
        btnSendImageWithCamera.setOnClickListener(this);

        btnSendImage = (ImageButton)findViewById(R.id.btnSendImage);
        btnSendImage.setOnClickListener(this);

        btnBackButton = (ImageButton)findViewById(R.id.btnBackMessages);
        btnBackButton.setOnClickListener(this);

        btnSendAudio = (ImageButton)findViewById(R.id.btnSendAudio);
        btnSendAudio.setOnClickListener(this);

        btnMoreInfo = (ImageButton)findViewById(R.id.btnMoreInfo);
        btnMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iMoreInfo = new Intent(ChatWithFriendActivity.this,MoreInfoActivity.class);
                iMoreInfo.putExtra("UID",uidFriendChat);
                iMoreInfo.putExtra("Name",nameFriendChat);
                startActivity(iMoreInfo);
                finish();
            }
        });


        editTextMessage = (EditText)findViewById(R.id.editTextMessage);
        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                messageController.scrollMessageEditText();
                if(editTextMessage.getText().toString().isEmpty()){
                    btnSendMessage.setImageResource(R.drawable.icon_message_empty);
                }
                else {
                    btnSendMessage.setImageResource(R.drawable.icon_send_message);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                messageController.scrollMessageEditText();
                if(editTextMessage.getText().toString().isEmpty()){
                    btnSendMessage.setImageResource(R.drawable.icon_message_empty);
                }
                else {
                    btnSendMessage.setImageResource(R.drawable.icon_send_message);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                messageController.scrollMessageEditText();
                if(editTextMessage.getText().toString().isEmpty()){
                    btnSendMessage.setImageResource(R.drawable.icon_message_empty);
                }
                else {
                    btnSendMessage.setImageResource(R.drawable.icon_send_message);
                }
            }
        });
        //
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            nodeGetName = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
            nodeGetName.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Account doc = dataSnapshot.getValue(Account.class);
                    if (doc != null) {
                        myName = doc.getFullName();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            nodeGetMyName = FirebaseDatabase.getInstance().getReference().child("doctors").child(user.getUid());
            nodeGetMyName.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Account doc = dataSnapshot.getValue(Account.class);
                    if (doc != null) {
                        myName = doc.getFullName();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        nodeRefreshMessage = FirebaseDatabase.getInstance().getReference();
        nodeRefreshMessage.addValueEventListener(this); // lắng nghe sự kiện khi có tin nhắn mới hoặc gửi đi
    }


    @Override
    protected void onStart() {
        super.onStart();
        messageController = new MessageController(ChatWithFriendActivity.this,uidFriendChat,recyclerViewMessage);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        DataSnapshot nodeMessage = dataSnapshot.child("messages");
        messageController.refreshMessage(nodeMessage,myName,nameFriendChat);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSendAudio:
                if(RecyclerListMessageAdapter.getCurrentMedia!=null && RecyclerListMessageAdapter.btnCurrentPlay!=null) {

                    MediaPlayer mediaPlayer = RecyclerListMessageAdapter.getCurrentMedia;
                    if(mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        RecyclerListMessageAdapter.btnCurrentPlay.setImageResource(R.drawable.icon_pause_audio_message);
                    }
                }
                FragmentManager fm = getFragmentManager();
                AudioMessageFragment audioMessageFragment = AudioMessageFragment.newInstance(uidFriendChat);
                audioMessageFragment.show(fm, null); // show dialog
                break;
            case R.id.btnBackMessages:

                Intent iFriendFragment = new Intent(ChatWithFriendActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                if(iChat.getStringExtra("From").equals("Friend_Fragment")){
                    bundle.putInt("ReturnTab", 1);
                }
                else if (iChat.getStringExtra("From").equals("Message_Fragment")
                        || iChat.getStringExtra("From").equals("MoreInfoMessage")){
                    bundle.putInt("ReturnTab", 0);
                }
                bundle.putString("UID",FirebaseAuth.getInstance().getUid());
                iFriendFragment.putExtras(bundle);
                startActivity(iFriendFragment);
                mFirebaseAnalytics.logEvent("chat",bundle);
                finish();
                break;
            case R.id.btnSendImage:
                Intent iSendPicture = new Intent();
                iSendPicture.setType("image/*");
                iSendPicture.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(iSendPicture, "Choose a photo to send"), 1);
                break;
            case R.id.btnSendMessage:
                String contentMessage = editTextMessage.getText().toString();
                if(!contentMessage.isEmpty()){
                    pushMessage("text",contentMessage);

//                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                    nodeGetMyName = FirebaseDatabase.getInstance().getReference().child("users").child(uidFriendChat).child("description");
                    nodeGetMyName.setValue("In Progress");
                }
                break;
            case R.id.btnOpenCamera:
                Intent intentopencamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intentopencamera,2);
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        nodeRefreshMessage.addValueEventListener(this);
    }

    public void pushMessage(String type, String contentMessage){
        TimeZone timeZone = TimeZone.getTimeZone("Africa/Kigali");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        simpleDateFormat.setTimeZone(timeZone);
        String timeMsg = simpleDateFormat.format(Calendar.getInstance().getTime());

        nodeMessage = FirebaseDatabase.getInstance().getReference().child("messages");

        Message msg = null;

        switch (type){
            case "text":
                msg = new Message(FirebaseAuth.getInstance().getUid(), uidFriendChat,myName,nameFriendChat,
                        contentMessage, false, false, timeMsg,false);
                break;
            case "image":
                msg = new Message(FirebaseAuth.getInstance().getUid(), uidFriendChat,myName,nameFriendChat,
                        contentMessage, true, false, timeMsg,false);
                break;
            case "audio":
                msg = new Message(FirebaseAuth.getInstance().getUid(), uidFriendChat,myName,nameFriendChat,
                        contentMessage, false, true, timeMsg,false); // co duoi .mp3 san r
                break;
        }
        nodeMessage.push().setValue(msg);


        nodeInfoMine = FirebaseDatabase.getInstance().getReference().child("more_info")
                .child(FirebaseAuth.getInstance().getUid()).child("last_messages");


        nodeInfoMine.child(uidFriendChat).setValue(new RecentlyChat(FirebaseAuth.getInstance().getUid(),uidFriendChat,nameFriendChat,
                contentMessage,type, timeMsg,false));

        nodeInfoFriend = FirebaseDatabase.getInstance().getReference().child("more_info")
                .child(uidFriendChat).child("last_messages");

        nodeInfoFriend.child(FirebaseAuth.getInstance().getUid()).setValue(new RecentlyChat(FirebaseAuth.getInstance().getUid(),
                FirebaseAuth.getInstance().getUid(), myName, contentMessage,type, timeMsg,false));

        editTextMessage.setText("");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            nodeRefreshMessage.addValueEventListener(this);
            filePath = data.getData();
            try {
                final String nameImage = UUID.randomUUID().toString();

                StorageReference ref = storageReference.child(FirebaseAuth.getInstance().getUid())
                        .child(uidFriendChat).child(nameImage+".jpg");

                ref.putFile(filePath)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                pushMessage("image",nameImage+".jpg");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ChatWithFriendActivity.this,
                                        "Error uploading image. Check your photo or Internet link",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {


                                    mAuthProgressDialog.show();
                                mAuthProgressDialog= new ProgressDialog(ChatWithFriendActivity.this);
//                                    mAuthProgressDialog = new ProgressDialog(this);
                                    mAuthProgressDialog.setTitle("Loading...");
                                    mAuthProgressDialog.setMessage("Image Loading into Firebase...");
                                    mAuthProgressDialog.setCancelable(false);

                            }
                        });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        else{
            nodeRefreshMessage.addValueEventListener(this);
        }

        if (requestCode == 2 && resultCode == Activity.RESULT_OK
                && data != null) {
            nodeRefreshMessage.addValueEventListener(this);
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            try {
                final String nameImage = UUID.randomUUID().toString(); // tạo tên bất kì cho ảnh

                StorageReference ref = storageReference.child(FirebaseAuth.getInstance().getUid())
                        .child(uidFriendChat).child(nameImage+".jpg");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data2 = baos.toByteArray();
                UploadTask uploadTask = ref.putBytes(data2);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(ChatWithFriendActivity.this,"If you do not receive the photo, please check again!!",Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pushMessage("image",nameImage+".jpg");
                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // tat nhac
        if(RecyclerListMessageAdapter.getCurrentMedia!=null) {
            MediaPlayer mediaPlayer = RecyclerListMessageAdapter.getCurrentMedia;
            if(mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void getAudioName(String audioName) {
        pushMessage("audio",audioName);
    }

}