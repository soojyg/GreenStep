package com.example.greenstep;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
public class ProgressUpdateJobService extends JobService{

    private HandlerThread handlerThread;
    private Handler handler;

    public boolean onStartJob(JobParameters params){
        handlerThread = new HandlerThread("ProgressUpdateJobService");
        handlerThread.start();

        handler = new Handler(handlerThread.getLooper()){
            @Override
            public void handleMessage(Message msg){

                updateTaskProgress();

                jobFinished((JobParameters) msg.obj, false);

            }
        };

        handler.sendMessage(Message.obtain(handler, 1, params));

        return true;
    }


    @Override
    public boolean onStopJob(JobParameters params) {
        handler.removeMessages(1);
        handlerThread.quitSafely();
        return true;
    }

    private void updateTaskProgress() {
        String uid = getUserUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("User Info").document(uid);

        userDocRef.collection("Challenge Details")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot challengeDoc : task.getResult()) {
                            // Update progress based on frequency
                            String frequency = challengeDoc.getString("frequency");
                            Calendar now = Calendar.getInstance();

                            if ("Daily".equals(frequency)) {
                                // Update every day at 00:00 AM
                                if (now.get(Calendar.HOUR_OF_DAY) == 0 && now.get(Calendar.MINUTE) == 0) {
                                    updateChallengeProgress(challengeDoc.getReference());
                                }
                            } else if ("Weekly".equals(frequency)) {
                                // Update every week on Monday at 00:00 AM
                                if (now.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY &&
                                        now.get(Calendar.HOUR_OF_DAY) == 0 && now.get(Calendar.MINUTE) == 0) {
                                    updateChallengeProgress(challengeDoc.getReference());
                                }

                            } else if ("Monthly".equals(frequency)) {
                                // Update every month on the 1st day at 00:00 AM
                                if (now.get(Calendar.DAY_OF_MONTH) == 1 &&
                                        now.get(Calendar.HOUR_OF_DAY) == 0 && now.get(Calendar.MINUTE) == 0) {
                                    updateChallengeProgress(challengeDoc.getReference());
                                }
                            }
                        }
                    } else {
                        Log.e("TaskUpdateJobService", "Error getting ChallengeDetails documents", task.getException());
                    }
                });
    }

    private void updateChallengeProgress(DocumentReference challengeRef) {
        challengeRef.update("progress", 0)
                .addOnSuccessListener(aVoid -> Log.d("TaskUpdateJobService", "Progress updated successfully"))
                .addOnFailureListener(e -> Log.e("TaskUpdateJobService", "Error updating progress", e));
    }


    public String getUserUid(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            // Handle the case where the user is not signed in
            return "";
        }
    }

}
