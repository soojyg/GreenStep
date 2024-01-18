package com.example.greenstep;

import android.os.Parcel;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import android.os.Parcelable;

public class Challenge implements Parcelable {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userUid;
    String description;
    String challengeType;
    String frequency;
    private String documentId;
    int quantity;

    int totalPointsPerCompletion;
    int totalPointsCollected;
    int progress;
    int notificationStatus; //0==off, 1==on



    public Challenge() {

    }

    public Challenge(String challengeType, String frequency, int quantity, String description, int totalPointsPerCompletion, String documentId) {
        this.challengeType = challengeType;
        this.frequency = frequency;
        this.quantity = quantity;
        this.description = description;
        this.totalPointsPerCompletion = totalPointsPerCompletion;
        this.documentId=documentId;

    }

    public Challenge(String userId, String challengeType, String frequency, int quantity, String description) {
        this.userUid = userId;
        this.challengeType = challengeType;
        this.frequency = frequency;
        this.quantity = quantity;
        this.description = description;
    }

    protected Challenge(Parcel in) {
        challengeType = in.readString();
        progress = in.readInt();
        frequency = in.readString();
        quantity = in.readInt();
        notificationStatus = in.readInt();
        description = in.readString();
        totalPointsPerCompletion = in.readInt();
        documentId=in.readString();
    }

    public static final Creator<Challenge> CREATOR = new Creator<Challenge>() {
        @Override
        public Challenge createFromParcel(Parcel in) {
            return new Challenge(in);
        }

        @Override
        public Challenge[] newArray(int size) {
            return new Challenge[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getChallengeType() {
        return challengeType;
    }

    public void setChallengeType(String challengeType) {
        this.challengeType = challengeType;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotalPointsPerCompletion() {
        return totalPointsPerCompletion;
    }

    public void setTotalPointsPerCompletion(int totalPointsPerCompletion) {
        this.totalPointsPerCompletion = totalPointsPerCompletion;
    }

    public int getTotalPointsCollected() {
        return totalPointsCollected;
    }

    public void setTotalPointsCollected(int totalPointsCollected) {
        this.totalPointsCollected = totalPointsCollected;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgressPercentage() {
        if (quantity != 0) {
            return (int) ((progress / (double) quantity) * 100);
        } else {
            // Handle the case where quantity is zero to avoid divide by zero error
            return 0;
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userUid);
        dest.writeString(description);
        dest.writeString(challengeType);
        dest.writeString(frequency);
        dest.writeString(documentId);
        dest.writeInt(quantity);
        dest.writeInt(totalPointsPerCompletion);
        dest.writeInt(totalPointsCollected);
        dest.writeInt(progress);
        dest.writeInt(notificationStatus);
    }
}


