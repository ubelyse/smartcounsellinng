package com.example.smartcounsellinng.Models;

import com.google.firebase.database.DatabaseReference;

public class Message {
    private String uidSender;
    private String uidReceiver;
    private String nameSender;
    private String nameReceiver;
    private String content;
    private boolean image;
    private boolean audio;
    private String timeMessage;
    private boolean lastMessageSeen;

    public Message(){

    }

    public Message(String uid, String uidFriendChat, String nodeGetMyName, String nodeGetName, String contentMessage, boolean image, boolean audio, String timeMsg, boolean lastMessageSeen) {
        this.uidSender = uid;
        this.uidReceiver = uidFriendChat;
        this.nameSender=nodeGetMyName;
        this.nameReceiver=nodeGetName;
        this.content = contentMessage;
        this.image = image;
        this.audio = audio;
        this.timeMessage = timeMsg;
        this.lastMessageSeen = lastMessageSeen;
    }

    public Message(String uid, String uidFriendChat, String contentMessage, boolean b, boolean b1, String timeMsg, boolean b2) {
        this.uidSender = uid;
        this.uidReceiver = uidFriendChat;
        this.content = contentMessage;
        this.image = image;
        this.audio = audio;
        this.timeMessage = timeMsg;
        this.lastMessageSeen = lastMessageSeen;
    }

    public String getUidSender() {
        return uidSender;
    }

    public void setUidSender(String uidSender) {
        this.uidSender = uidSender;
    }

    public String getUidReceiver() {
        return uidReceiver;
    }

    public void setUidReceiver(String uidReceiver) {
        this.uidReceiver = uidReceiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isImage() {
        return image;
    }

    public void setImage(boolean image) {
        this.image = image;
    }

    public boolean isAudio() {
        return audio;
    }

    public void setAudio(boolean audio) {
        this.audio = audio;
    }

    public String getTimeMessage() {
        return timeMessage;
    }

    public void setTimeMessage(String timeMessage) {
        this.timeMessage = timeMessage;
    }

    public boolean isLastMessageSeen() {
        return lastMessageSeen;
    }

    public void setLastMessageSeen(boolean lastMessageSeen) {
        this.lastMessageSeen = lastMessageSeen;
    }

    public String getNameSender() {
        return nameSender;
    }

    public void setNameSender(String nameSender) {
        this.nameSender = nameSender;
    }

    public String getNameReceiver() {
        return nameReceiver;
    }

    public void setNameReceiver(String nameReceiver) {
        this.nameReceiver = nameReceiver;
    }
}
