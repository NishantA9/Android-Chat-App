package edu.uncc.hw06;

import java.io.Serializable;
import java.util.ArrayList;

public class Chat implements Serializable {
    public ArrayList<String> names;
    public ArrayList<String> uids;
    public String id;
    public Message lastMsg;
    public Chat() {}
    public String chatStarterName;  // Add a field for the person who started the chat
    public String getChatStarterName() {return chatStarterName;}
    public void setChatStarterName(String chatStarterName) {this.chatStarterName = chatStarterName;}
    @Override
    public String toString() {
        return "Chat{" +
                "names=" + names +
                ", uids=" + uids +
                ", id='" + id + '\'' +
                ", lastMsg=" + lastMsg +
                ", chatStarterName='" + chatStarterName + '\'' +
                '}';}
    public ArrayList<String> getNames() {return names;}
    public void setNames(ArrayList<String> names) {this.names = names;}
    public ArrayList<String> getUids() {return uids;}
    public void setUids(ArrayList<String> uids) {this.uids = uids;}
    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public Message getLastMsg() {return lastMsg;}
    public void setLastMsg(Message lastMsg) {this.lastMsg = lastMsg;}
    public Chat(ArrayList<String> names, ArrayList<String> uids, String id, Message lastMsg,  String chatStarterName) {
        this.names = names;
        this.uids = uids;
        this.id = id;
        this.lastMsg = lastMsg;
        this.chatStarterName = chatStarterName;
    }}
