package edu.uncc.hw06;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Message implements Serializable{
    public Timestamp createdAt;
    public String ownerName, ownerId, msgText, id;
    public Message() {}
    public Timestamp getCreatedAt() {return createdAt;}
    public void setCreatedAt(Timestamp createdAt) {this.createdAt = createdAt;}
    public String getOwnerName() {return ownerName;}
    public void setOwnerName(String ownerName) {this.ownerName = ownerName;}
    public String getOwnerId() {return ownerId;}
    public void setOwnerId(String ownerId) {this.ownerId = ownerId;}
    public String getMsgText() {return msgText;}
    public void setMsgText(String msgText) {this.msgText = msgText;}
    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public Message(Timestamp createdAt, String ownerName, String ownerId, String msgText, String id) {
        this.createdAt = createdAt;
        this.ownerName = ownerName;
        this.ownerId = ownerId;
        this.msgText = msgText;
        this.id = id;}
    @Override
    public String toString() {
        return "Message{" +
                "createdAt=" + createdAt +
                ", ownerName='" + ownerName + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", msgText='" + msgText + '\'' +
                ", id='" + id + '\'' +
                '}';}}
