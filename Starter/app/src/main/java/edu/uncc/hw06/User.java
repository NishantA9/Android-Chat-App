package edu.uncc.hw06;

import java.io.Serializable;

public class User implements Serializable {
    public String name, uid;
    public User() {}
    public User(String name, String uid) {
        this.name = name;
        this.uid = uid;
    }
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getUid() {return uid;}
    public void setUid(String uid) {this.uid = uid;}
    @Override
    public String toString() {
        return name;}}
