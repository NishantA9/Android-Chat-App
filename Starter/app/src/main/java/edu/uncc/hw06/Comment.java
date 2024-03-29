package edu.uncc.hw06;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Comment implements Serializable {
    public String comment, docId, ownerId, ownerName;
    public Timestamp createdAt;
    public Comment() {
    }
    @Override
    public String toString() {
        return "Comment{" +
                "comment='" + comment + '\'' +
                ", docId='" + docId + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getDocId() {
        return docId;
    }
    public void setDocId(String docId) {
        this.docId = docId;
    }
    public String getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    public String getOwnerName() {
        return ownerName;
    }
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
