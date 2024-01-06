package com.example.greenstep;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log; // Import Log class
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChallengeListAdapter extends RecyclerView.Adapter<ChallengeListAdapter.ChallengeProgressViewHolder> {

    private FragmentManager fragmentManager;

    private static final String TAG = "ChallengeListAdapter"; // Define a tag for logging


    private Context context;
    private ArrayList<Challenge> challengeList;


    public ChallengeListAdapter(Context context, ArrayList<Challenge> challengeList) {
        this.context = context;
        this.challengeList = challengeList;

        Log.d(TAG, "ChallengeListAdapter: Created with " + challengeList.size() + " challenges");
    }



    public ChallengeListAdapter(FragmentManager fragmentManager, ArrayList<Challenge> challengeList) {
        this.fragmentManager = fragmentManager;
        this.challengeList = challengeList;
    }

    @NonNull
    @Override
    public ChallengeProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.challenge_progress, parent, false);
        Log.d(TAG, "onCreateViewHolder: Creating view holder");
        return new ChallengeProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeProgressViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.d(TAG, "onBindViewHolder: Position = " + position);









        if (position >= 0 && position < challengeList.size()) {
            Challenge challenge = challengeList.get(position);

            // Log relevant information
            Log.d(TAG, "onBindViewHolder: Binding data for position " + position +
                    ", Challenge type: " + challenge.getChallengeType() +
                    ", Frequency: " + challenge.getFrequency() +
                    ", Progress Percentage: " + challenge.getProgressPercentage() + "%");

            if (holder.frequency == null) {
                Log.e(TAG, "onBindViewHolder: frequency TextView is null!");
            }

            if (holder.challengeType == null) {
                Log.e(TAG, "onBindViewHolder: challengeType TextView is null!");
            }

            if (holder.progressPercentage == null) {
                Log.e(TAG, "onBindViewHolder: progressPercentage TextView is null!");
            }

            if (holder.frequency == null || holder.challengeType == null || holder.progressPercentage == null) {
                Log.e(TAG, "onBindViewHolder: One or more TextView objects are null!");
            } else {
                holder.frequency.setText(challenge.getFrequency());
                holder.challengeType.setText(challenge.getChallengeType());
                holder.progressPercentage.setText(challenge.getProgressPercentage() + "%");
            }
            holder.createDynamicCheckBoxes(challenge.getQuantity(),challenge.getProgress(), challenge.getDocumentId(),challenge, holder);

        }
    }

    @Override
    public int getItemCount() {
        return challengeList.size();
    }

    public class ChallengeProgressViewHolder extends RecyclerView.ViewHolder {

        TextView challengeType, frequency, progressPercentage;
        ProgressBar progressBar;
        LinearLayout buttonLayout_challengeProgress;
        Button button_challengeProgress, newButton_challengeProgress, button_editChallenge;
        ImageButton imageButton_editChallenge ;

        public ChallengeProgressViewHolder(@NonNull View itemView) {
            super(itemView);



//            int position = getBindingAdapterPosition();
//            if (position >= 0 && position < challengeList.size()) {
//                Challenge challenge = challengeList.get(position);

                challengeType = itemView.findViewById(R.id.tvChallengeName);
                frequency = itemView.findViewById(R.id.tvFrequency);
                progressPercentage = itemView.findViewById((R.id.tvProgressPercentage));
                buttonLayout_challengeProgress = itemView.findViewById(R.id.buttonlayout_challengeProgress);
                imageButton_editChallenge = itemView.findViewById(R.id.imageButton_editChallenge);

            imageButton_editChallenge.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the adapter position of the clicked item
                    int adapterPosition = getAdapterPosition();

                    // Ensure a valid position
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        // Get the clicked challenge
                        Challenge clickedChallenge = challengeList.get(adapterPosition);

                        // Get the document ID of the clicked challenge
                        String documentId = clickedChallenge.getDocumentId();

                        // Open the EditChallengeFragment with the document ID
                        openEditChallengeFragment(itemView.getContext(), documentId);
                    }
                }
            });




                if(buttonLayout_challengeProgress==null){
                    Log.d(TAG, "Button layout view is null ");
                }



                if (challengeType == null) {
                    Log.e(TAG, "Challenge type view is null");
                }

                if (frequency == null) {
                    Log.e(TAG, "Frequency view is null");
                }

                if (progressPercentage == null) {
                    Log.e(TAG, "Progress percentage view is null");
                }

                if (progressBar == null) {
                    Log.e(TAG, "Progress bar view is null");
                }

                // Log information about the view holder creation
                Log.d(TAG, "ChallengeProgressViewHolder: Created for Challenge type: " );
            }

        public void createDynamicCheckBoxes(int quantity, int progress, String documentId, Challenge challenge, ChallengeProgressViewHolder holder) {
            buttonLayout_challengeProgress.removeAllViews();

            final int[] currentProgress = {challenge.getProgress()};

            if (currentProgress[0] == quantity) {
                buttonLayout_challengeProgress.removeAllViews();
                TextView completedText = new TextView(itemView.getContext());
                completedText.setText("Completed");
                buttonLayout_challengeProgress.addView(completedText);
                return;  // Exit the method if already completed
            }

            // Dynamically create checkboxes based on the quantity
            for (int i = 0; i < quantity; i++) {
                CheckBox dynamicCheckBox = new CheckBox(itemView.getContext());

                if (i < currentProgress[0]) {
                    dynamicCheckBox.setChecked(true);
                    dynamicCheckBox.setEnabled(false);
                }

                dynamicCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        // Handle checkbox state change
                        dynamicCheckBox.setEnabled(false);

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        currentProgress[0]++; // Increment the quantity

                        // Replace "User Info" and "Challenge Details" with your actual collection names
                        db.collection("User Info")
                                .document(getUserUid())
                                .collection("Challenge Details")
                                .document(documentId)
                                .set(new HashMap<String, Object>() {{
                                    put("progress", currentProgress[0]);
                                    if (currentProgress[0] == quantity) {
                                        put("totalPointsCollected", challenge.getTotalPointsCollected() + challenge.getTotalPointsPerCompletion());
                                        db.collection("User Info")
                                                .document(getUserUid())
                                                .update("Points Collected", FieldValue.increment(challenge.getTotalPointsPerCompletion()));
                                    }

                                }}, SetOptions.merge())  // Use SetOptions.merge() to add or update fields
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Progress updated successfully at document id " + documentId);
                                        challenge.setProgress(currentProgress[0]);
                                        int newProgressPercentage = challenge.getProgressPercentage();
                                        holder.progressPercentage.setText(newProgressPercentage + "%");

                                        // Check if progress is equal to quantity
                                        if (currentProgress[0] == quantity) {
                                            // If yes, remove all checkboxes and display "Completed" text
                                            buttonLayout_challengeProgress.removeAllViews();
                                            TextView completedText = new TextView(itemView.getContext());
                                            completedText.setText("Completed");
                                            buttonLayout_challengeProgress.addView(completedText);
                                            challenge.setTotalPointsCollected(challenge.getTotalPointsCollected() + challenge.getTotalPointsPerCompletion());

                                            // Record challenge completion in Firestore
                                            recordChallengeCompletion(documentId, challenge);
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "Error updating progress", e);
                                    }
                                });
                    }
                });

                // Add the checkbox to the container
                buttonLayout_challengeProgress.addView(dynamicCheckBox);
            }
        }



    }
    public String getUserUid() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            // Log a message if the user is not signed in
            Log.d(TAG, "getUserUid: User not signed in");
            return "";
        }
    }

    private void openEditChallengeFragment(Context context, String documentId) {
        // Create an instance of EditChallengeFragment
        EditChallengeFragment fragmentB = new EditChallengeFragment();

        // Pass the documentId as an argument to the fragment
        Bundle args = new Bundle();
        args.putString("documentId", documentId);
        fragmentB.setArguments(args);

        // Get the FragmentManager from the context
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();

        // Start a FragmentTransaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace the current fragment with EditChallengeFragment
        fragmentTransaction.replace(R.id.mainFragmentContainer, fragmentB);

        // Add the transaction to the back stack
        fragmentTransaction.addToBackStack(null);

        // Commit the transaction
        fragmentTransaction.commit();
    }

    private void recordChallengeCompletion(String documentId, Challenge challenge) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Format the date to exclude the time component
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        DocumentReference dateDocumentRef = db.collection("User Info")
                .document(getUserUid())
                .collection("Calendar")
                .document(formattedDate);

        // Fetch the current document to determine the existing task count
        dateDocumentRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // If the document exists, determine the existing task count
                            long existingTaskCount = documentSnapshot.getData().entrySet().stream()
                                    .filter(entry -> entry.getKey().startsWith("challengeCompleted"))
                                    .count();

                            // Increment the task count for the new task
                            int newTaskNumber = (int) existingTaskCount + 1;

                            // Add the new task
                            dateDocumentRef.set(new HashMap<String, Object>() {{


                                        // Add the new task
                                        put("challengeCompleted" + newTaskNumber, challenge.getChallengeType());
                                    }}, SetOptions.merge()) // Use SetOptions.merge() to add or update fields
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void documentReference) {
                                            Log.d(TAG, "Challenge completion recorded successfully");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error recording challenge completion", e);
                                        }
                                    });
                        } else {
                            // If the document doesn't exist, create it with the first task
                            dateDocumentRef.set(new HashMap<String, Object>() {{

                                        // Add the first task
                                        put("challengeCompleted1", challenge.getChallengeType());
                                    }}, SetOptions.merge()) // Use SetOptions.merge() to add or update fields
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void documentReference) {
                                            Log.d(TAG, "Challenge completion recorded successfully");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error recording challenge completion", e);
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error fetching existing tasks", e);
                    }
                });
    }




}

