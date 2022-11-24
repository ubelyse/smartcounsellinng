package com.example.smartcounsellinng.Controllers;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcounsellinng.Adapters.RecyclerListMessageAdapter;
import com.example.smartcounsellinng.Models.Message;
import com.example.smartcounsellinng.Models.RecentlyChat;
import com.example.smartcounsellinng.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MessageController {

    Context context;
    String uidFriend;
    Message msg;
    List<Message> listMessage;

    List<String> keyMessage;

    RecyclerView recyclerViewMessage;
    RecyclerListMessageAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    private DatabaseReference nodeInfoMine, nodeInfoFriend, nodeMessageUpdate;


    public MessageController(Context context, String uidFriend, RecyclerView recyclerViewMessage){
        this.context = context;
        this.uidFriend = uidFriend;
        this.recyclerViewMessage = recyclerViewMessage;

        msg = new Message();

        listMessage = new ArrayList<>();
        keyMessage = new ArrayList<>();

        layoutManager = new LinearLayoutManager(context);

        recyclerViewMessage.setLayoutManager(layoutManager);
        adapter = new RecyclerListMessageAdapter(listMessage, context, R.layout.item_message_mine,
                R.layout.item_message_friend, R.layout.item_message_image_mine, R.layout.item_message_image_friend,
                R.layout.item_message_audio_mine,R.layout.item_message_audio_friend);
        recyclerViewMessage.setAdapter(adapter);
    }

    public void scrollMessageEditText(){
        recyclerViewMessage.scrollToPosition(listMessage.size() - 1); // scroll the latest message up
    }

    public void refreshMessage(DataSnapshot nodeMessage, String myName, String friendName){
        //listMessage.clear();

        for(DataSnapshot nodeSingleMessage : nodeMessage.getChildren()) {
            Message newMsg = nodeSingleMessage.getValue(Message.class);
            if ((newMsg.getUidSender().equals(FirebaseAuth.getInstance().getUid())
                    && newMsg.getUidReceiver().equals(uidFriend))
                    || (newMsg.getUidSender().equals(uidFriend)
                    && newMsg.getUidReceiver().equals(FirebaseAuth.getInstance().getUid()))) {
                if(!keyMessage.contains(nodeSingleMessage.getKey())){
                    listMessage.add(newMsg);
                    keyMessage.add(nodeSingleMessage.getKey());
                    adapter.notifyDataSetChanged(); // Have a change with new message sent
                }
            }
        }

        //call refresh the message if and only when the sender is in active chat
        // So to check whether or not will retrieve the final message
        if(!listMessage.isEmpty())
        {
            Message temp = listMessage.get(listMessage.size() - 1);

            String typeFile = "text";
            if(temp.isImage()){
                typeFile = "image";
            }
            else if(temp.isAudio()){
                typeFile = "audio";
            }

            if(!temp.getUidSender().equals(FirebaseAuth.getInstance().getUid())){ // the message just loaded is not sent by itself
                //push information required for retrieving recent listings
                nodeInfoMine = FirebaseDatabase.getInstance().getReference().child("more_info")
                        .child(temp.getUidReceiver()).child("last_messages");
                // push the last message to show it when retrieving the recent message list
                nodeInfoMine.child(temp.getUidSender()).setValue(new RecentlyChat(temp.getUidSender(),
                        temp.getUidSender(), friendName, temp.getContent(), typeFile, temp.getTimeMessage(),true));
                nodeInfoFriend = FirebaseDatabase.getInstance().getReference().child("more_info")
                        .child(temp.getUidSender()).child("last_messages"); // your last message node
                nodeInfoFriend.child(temp.getUidReceiver()).setValue(new RecentlyChat(temp.getUidSender(),
                        temp.getUidReceiver(), myName, temp.getContent(), typeFile, temp.getTimeMessage(),true));

                nodeMessageUpdate = FirebaseDatabase.getInstance().getReference().child("messages")
                        .child(keyMessage.get(keyMessage.size() - 1));
                temp.setLastMessageSeen(true); // return the last message to the viewed state

                nodeMessageUpdate.setValue(temp); // change on database
            }
        }

        scrollMessageEditText();
    }
}
