package com.example.greenstep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;

public class RewardAdmin extends Fragment {

    private int noTreePlantedDB, noTreeWaitingDB;
    TextView noTreePlanted, noTreeWaiting;
    Button buttonUpdateTree;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRefTotal = db.collection("Statistics").document("globalStat");
    private int noTreeSetting;

    @Override
    public View onCreateView(@org.checkerframework.checker.nullness.qual.NonNull LayoutInflater inflater, @Nullable ViewGroup container, @org.checkerframework.checker.nullness.qual.NonNull Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.reward_admin,container,false);
        return rootView;
    }

 @Override
    public void onViewCreated(@org.checkerframework.checker.nullness.qual.NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        noTreePlanted = view.findViewById(R.id.noTreePlanted);
        noTreeWaiting = view.findViewById(R.id.noTreeWaiting);
        buttonUpdateTree = view.findViewById(R.id.buttonUpdateTree);

        // Retrieve initial values from Firestore document
        docRefTotal.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Retrieve the initial values from the document
                    noTreePlantedDB = documentSnapshot.getLong("totalTreesPlanted").intValue();
                    noTreeWaitingDB = documentSnapshot.getLong("totalTreesWaiting").intValue();

                    // Set the values to the TextViews
                    noTreePlanted.setText(String.valueOf(noTreePlantedDB));
                    noTreeWaiting.setText(String.valueOf(noTreeWaitingDB));
                }
            }
        });

        // Set click listener for the update button
        buttonUpdateTree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the values when the button is clicked
                showCompleteDialog();

                // Update Firestore with the new values
                Map<String, Object> updates = new HashMap<>();
                updates.put("totalTreesPlanted", noTreePlantedDB);
                updates.put("totalTreesWaiting", noTreeWaitingDB);

                // Update Firestore document
                docRefTotal.update(updates)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Update successful, update the TextViews
                                noTreePlanted.setText(String.valueOf(noTreePlantedDB));
                                noTreeWaiting.setText(String.valueOf(noTreeWaitingDB));
                                Toast.makeText(requireContext(), "Trees updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure
                                Toast.makeText(requireContext(), "Error updating trees: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    /**
     * Display a custom dialog for updating the number of trees planted.
     */
    private void showCompleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Inflate the custom layout
        View customLayout = getLayoutInflater().inflate(R.layout.activity_popup_reward_admin, null);
        builder.setView(customLayout);
        TextView noTree = customLayout.findViewById(R.id.noTree);
        ImageButton increment = customLayout.findViewById(R.id.increment);
        ImageButton decrement = customLayout.findViewById(R.id.decrement);
        Button confirmPlanted = customLayout.findViewById(R.id.confirmPlanted);
        noTreeSetting = 0;

        // Create the dialog
        AlertDialog dialog = builder.create();

        // Increment button click listener
        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noTreeSetting < noTreeWaitingDB) {
                    noTreeSetting++;
                    noTree.setText(String.valueOf(noTreeSetting));
                }
                if((noTreeSetting > noTreeWaitingDB) || noTreeSetting==0 ) {
                    Toast.makeText(requireContext(), "No more tree to waiting to be planted.", Toast.LENGTH_SHORT).show();
                }
                // Enable or disable buttons based on conditions
                updateButtonStates(increment, decrement);
            }
        });

        // Decrement button click listener
        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noTreeSetting > 0) {
                    noTreeSetting--;
                    noTree.setText(String.valueOf(noTreeSetting));
                }
                if(noTreeSetting <= 0)
                    Toast.makeText(requireContext(), "Please enter valid input.", Toast.LENGTH_SHORT).show();
                // Enable or disable buttons based on conditions
                updateButtonStates(increment, decrement);
            }
        });



        // Confirm button click listener
        confirmPlanted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the TextViews and Firestore values based on user input
                updateNumberTextView(noTreeSetting);

                // Update Firestore with the new values
                Map<String, Object> updates = new HashMap<>();
                updates.put("totalTreesPlanted", noTreePlantedDB);
                updates.put("totalTreesWaiting", noTreeWaitingDB);

                // Update Firestore document
                docRefTotal.update(updates)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Update successful, update the TextViews
                                noTreePlanted.setText(String.valueOf(noTreePlantedDB));
                                noTreeWaiting.setText(String.valueOf(noTreeWaitingDB));
                                Toast.makeText(requireContext(), "Trees updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure
                                Toast.makeText(requireContext(), "Error updating trees: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                // Placeholder action, replace it with your logic
                Toast.makeText(requireContext(), "Confirmed. Number: " + noTreeSetting, Toast.LENGTH_SHORT).show();

                // Dismiss the dialog after actions
                dialog.dismiss();
            }
        });
        // Set dialog background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
        updateButtonStates(increment, decrement);
    }

    /**
     * Update the enabled/disabled state of increment and decrement buttons based on conditions.
     *
     * @param increment The increment button.
     * @param decrement The decrement button.
     */
    private void updateButtonStates(ImageButton increment, ImageButton decrement) {
        // Enable or disable increment button
        increment.setEnabled(noTreeSetting <= noTreeWaitingDB);

        // Enable or disable decrement button
        decrement.setEnabled(noTreeSetting >= 0);


    }

    /**
     * Update the TextViews and Firestore values based on the user's input.
     *
     * @param noTreeSetting The number of trees to be updated.
     */
    private void updateNumberTextView(int noTreeSetting) {
        if(noTreeSetting <= noTreeWaitingDB)
            noTreeWaitingDB -= noTreeSetting;
            noTreePlantedDB += noTreeSetting;

            // Update the TextViews with the new values
            noTreePlanted.setText(String.valueOf(noTreePlantedDB));
            noTreeWaiting.setText(String.valueOf(noTreeWaitingDB));

    }



}
