package com.example.greenstep;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;

public class RewardUser extends Fragment {

    TextView number,noLeavesCollected;
    Button button_plant;
    String  numberDB, noLeavesCollectedDB;
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    String uid=user.getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference = db.collection("User Info").document(uid);
    DocumentReference docRefTotal = db.collection("Statistics").document("globalStat");

    Boolean confirm;
    ProgressBar progressBar;

    @Override
    public View onCreateView(@org.checkerframework.checker.nullness.qual.NonNull LayoutInflater inflater, @Nullable ViewGroup container, @org.checkerframework.checker.nullness.qual.NonNull Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.reward_user,container,false);
        return rootView;
    }

   @Override
    public void onViewCreated(@org.checkerframework.checker.nullness.qual.NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        number = view.findViewById(R.id.number);
        noLeavesCollected = view.findViewById(R.id.noLeavesCollected);
        button_plant = view.findViewById(R.id.button_plant);
        progressBar = view.findViewById(R.id.progressBar);

        // Retrieve the current points collected and update UI elements accordingly
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Long currentPointCollected = documentSnapshot.getLong("pointCollected");
                    if (currentPointCollected == null) {
                        currentPointCollected = 0L;
                    }

                    // If enough points, enable the button; otherwise, disable it
                    button_plant.setEnabled(currentPointCollected >= 10000);

                    // Update progress bar
                    updateProgressBar(currentPointCollected, 10000);
                }
            }
        });


        // Set a click listener for the button
        button_plant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if the currentPointCollected is greater than or equal to 10000
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Long currentPointCollected = documentSnapshot.getLong("pointCollected");
                            if (currentPointCollected == null) {
                                currentPointCollected = 0L;
                            }

                            // If enough points, show the confirmation dialog; otherwise, do nothing
                            if (currentPointCollected >= 10000) {
                                showConfirmationDialog();
                            }
                        }
                    }
                });
            }
        });

        // Display the initial count when the activity is created
        displayCurrentTreesPlanted();
        displayPointCollected();
    }

    /**
     * Increment the count of trees planted by 1.
     */
    private void incrementTreesPlanted() {
        // Update the "trees_planted" field by incrementing by 1
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Document exists, update the "trees_planted" field
                            Long currentTreesPlanted = documentSnapshot.getLong("trees_planted");
                            if (currentTreesPlanted == null) {
                                currentTreesPlanted = 0L;
                            }

                            // Increment the number of trees planted
                            currentTreesPlanted++;

                            // Update the document with the new value of "trees_planted"
                            Long finalCurrentTreesPlanted = currentTreesPlanted;
                            documentReference.update("trees_planted", currentTreesPlanted)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Update successful
                                            Log.d(TAG, "Document updated successfully. Trees planted: " + finalCurrentTreesPlanted);
                                            // Update the TextView with the new count
                                            displayCurrentTreesPlanted();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle the failure to update the document
                                            Log.e(TAG, "Error updating document", e);
                                        }
                                    });
                        } else {
                            // Document does not exist, create a new document

                            // Set the initial "trees_planted" field value to 1
                            documentReference.set(new HashMap<String, Object>() {{
                                        put("trees_planted", 1L);
                                    }})
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Document creation successful
                                            Log.d(TAG, "Document created successfully with initial count: 1");
                                            // Update the TextView with the new count
                                            displayCurrentTreesPlanted();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle the failure to create the document
                                            Log.e(TAG, "Error creating document", e);
                                        }
                                    });
                        }
                    }
                });
    }

    /**
     * Increment the count of "totalTreesWaiting" in the docRefTotal document.
     */
    private void incrementTotalTreesWaiting() {
        docRefTotal.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Retrieve the current count of "totalTreesWaiting"
                            Long currentTotalTreesWaiting = documentSnapshot.getLong("totalTreesWaiting");
                            if (currentTotalTreesWaiting == null) {
                                currentTotalTreesWaiting = 0L;
                            }

                            // Increment the "totalTreesWaiting"
                            currentTotalTreesWaiting++;

                            // Update the document with the new value of "totalTreesWaiting"
                            Long finalCurrentTotalTreesWaiting = currentTotalTreesWaiting;
                            docRefTotal.update("totalTreesWaiting", currentTotalTreesWaiting)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Update successful
                                            Log.d(TAG, "Total Trees Waiting updated successfully. Total Trees Waiting: " + finalCurrentTotalTreesWaiting);
                                            // You can add further actions if needed
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle the failure to update the document
                                            Log.e(TAG, "Error updating totalTreesWaiting", e);
                                        }
                                    });
                        } else {
                            // Handle the case where the document doesn't exist
                            Log.e(TAG, "docRefTotal does not exist");
                        }
                    }
                });
    }

    /**
     * Decrement the points collected by 10,000 after redeeming rewards for tree plantation.
     */
    private void decrementPointCollected() {
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Retrieve the current number of points collected
                            Long currentPointCollected = documentSnapshot.getLong("pointCollected");
                            if (currentPointCollected == null) {
                                currentPointCollected = 0L;
                            }

                            // Check if there are enough points to decrement
                            if (currentPointCollected >= 10000) {
                                // Decrement points by 10000
                                currentPointCollected -= 10000;

                                // Ensure the points don't go below 0
                                currentPointCollected = Math.max(0, currentPointCollected);

                                // Update the ProgressBar
                                updateProgressBar(currentPointCollected, 10000);

                                // Update the document with the new value of "pointCollected"
                                Long finalCurrentPointCollected = currentPointCollected;
                                documentReference.update("pointCollected", currentPointCollected)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "Points decremented successfully. Points collected: " + finalCurrentPointCollected);
                                                displayPointCollected();
                                                // Check and update the button state after Firebase update
                                                updateButtonState(finalCurrentPointCollected);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "Error updating document", e);
                                            }
                                        });
                            } else {
                                // If points are not enough, disable the button
                                button_plant.setEnabled(false);
                            }
                        }
                    }
                });
    }

    /**
     * Update the button state based on the current points.
     *
     * @param currentPoints The current number of points.
     */
    private void updateButtonState(long currentPoints) {
        button_plant.setEnabled(currentPoints >= 10000);
    }

    /**
     * Retrieve the current number of trees planted and update the TextView.
     */
    private void displayCurrentTreesPlanted() {
        // Retrieve the current number of trees planted and update the TextView
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Retrieve the current number of trees planted
                            Long currentTreesPlanted = documentSnapshot.getLong("trees_planted");
                            if (currentTreesPlanted == null) {
                                currentTreesPlanted = 0L;
                            }

                            // Log the current count before updating the TextView
                            Log.d(TAG, "Current Trees Planted: " + currentTreesPlanted);

                            // Update the TextView with the current count
                            try {
                                number.setText(String.valueOf(currentTreesPlanted));
                            } catch (Exception e) {
                                // Log any exception that might occur during conversion or setting text
                                Log.e(TAG, "Error updating TextView", e);
                            }
                        }
                    }
                });
    }

    /**
     * Retrieve the current number of leaves collected and update the corresponding TextView.
     */
    private void displayPointCollected() {
        // Retrieve the current number of leaves planted and update the TextView
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            // Retrieve the current number of leaves collected
                            Long currentPointCollected = documentSnapshot.getLong("pointCollected");
                            if (currentPointCollected == null) {
                                currentPointCollected = 0L;
                            }

                            // Log the current count before updating the TextView
                            Log.d(TAG, "Point collected: " + currentPointCollected);

                            // Update the TextView with the current count
                            try {
                                noLeavesCollected.setText(String.valueOf(currentPointCollected));
                            } catch (Exception e) {
                                // Log any exception that might occur during conversion or setting text
                                Log.e(TAG, "Error updating TextView", e);
                            }
                        }
                    }
                });
    }

    /**
     * Show a confirmation dialog to confirm the redemption of rewards for tree plantation.
     */
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Inflate the custom layout
        View customLayout = getLayoutInflater().inflate(R.layout.activity_popup_reward, null);
        builder.setView(customLayout);

        TextView confirmationMsg = customLayout.findViewById(R.id.confirmation_msg);
        Button confirmButton = customLayout.findViewById(R.id.confirm_button);
        Button cancelButton = customLayout.findViewById(R.id.cancel_button);

        confirmationMsg.setText("Are you sure you want to redeem your\nrewards for tree plantation using\n10,000 leaves?");

        // Create the dialog
        AlertDialog dialog = builder.create();

        // Set click listeners for the buttons
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // User clicked Yes, proceed with the actions
                incrementTreesPlanted();
                incrementTotalTreesWaiting();
                decrementPointCollected();
                dialog.dismiss();
                showCompleteDialog();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    /**
     * Show a completion dialog after the rewards redemption is confirmed.
     */
    private void showCompleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Inflate the custom layout
        View customLayout = getLayoutInflater().inflate(R.layout.activity_popup_dialog, null);

        builder.setView(customLayout);

        TextView complete_msg = customLayout.findViewById(R.id.complete_msg);
        complete_msg.setText("Your rewards redemption is confirmed! Thank you for \nplanting a tree and contributing to a greener world.");

        // Create the dialog
        AlertDialog dialog = builder.create();

        // Set background to null to remove default AlertDialog background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // Show the dialog
        dialog.show();
    }

    /**
     * Update the ProgressBar based on the current and total points.
     *
     * @param currentPoints The current number of points.
     * @param totalPoints   The total number of points.
     */
    private void updateProgressBar(long currentPoints, long totalPoints) {
        // Calculate the progress percentage
        int progress = (int) ((currentPoints * 100) / totalPoints);

        // Ensure progress is between 0 and 100
        progress = Math.min(100, Math.max(0, progress));

        // Set the progress to the ProgressBar
        progressBar.setProgress(progress);
    }

}
