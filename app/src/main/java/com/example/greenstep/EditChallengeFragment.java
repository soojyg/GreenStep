package com.example.greenstep;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class EditChallengeFragment extends Fragment {

    private static final String TAG = "EditChallengeFragment";

    private Spinner challengeTypes_spinner;
    private Spinner frequencyTypes_spinner;
    private EditText challengeDescription_editText;
    private TextView quantityNumber_text;
    private TextView pointsCounted;

    private String documentId;
    private String userUid;
    private TextView quantity_textView;

    private int quantity;
    private int totalPointsPerCompletion;

    private int notificationStatus;

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_challenge, container, false);

        db = FirebaseFirestore.getInstance();

        // Initialize your views
        challengeTypes_spinner = view.findViewById(R.id.challengeType_spinner);
        frequencyTypes_spinner = view.findViewById(R.id.frequency_spinner);
        challengeDescription_editText = view.findViewById(R.id.challengeDescription_editText);
        quantity_textView = view.findViewById(R.id.quantity_number);
        quantityNumber_text = view.findViewById(R.id.quantity_number);
        pointsCounted = view.findViewById(R.id.pointsCounted);

        ImageButton backToHomePage_button = view.findViewById(R.id.backToMainPage_editChallenge_button);
        Button editChallenge_button = view.findViewById(R.id.editChallenge_button);
        Button deleteChallenge_button = view.findViewById(R.id.deleteChallenge_button);
        ImageButton quantityIncrement_button = view.findViewById(R.id.quantityIncrement_button);
        ImageButton quantityDecrement_button = view.findViewById(R.id.quantityDecrement_button);

        //challenge type spinner
        challengeTypes_spinner = view.findViewById(R.id.challengeType_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.challenge_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        challengeTypes_spinner.setAdapter(adapter);

        // Retrieve the documentId from the arguments
        Bundle args = getArguments();
        if (args != null) {
            documentId = args.getString("documentId", "");
            // Load challenge details based on documentId
            loadChallengeDetails(documentId);
        }
        quantity = Integer.parseInt(quantityNumber_text.getText().toString());



        //frequency type spinner
        frequencyTypes_spinner = view.findViewById(R.id.frequency_spinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                getContext(),
                R.array.frequency_types,
                android.R.layout.simple_spinner_item
        );
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencyTypes_spinner.setAdapter(adapter2);



        quantityIncrement_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity<10)
                    quantity++;
                quantityNumber_text.setText(String.valueOf(quantity));
                returnPoints(db, pointsCounted, frequencyTypes_spinner.getSelectedItem().toString(), quantity);
            }
        });

        quantityDecrement_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity>1)
                    quantity--;
                quantityNumber_text.setText(String.valueOf(quantity));
                returnPoints(db, pointsCounted, frequencyTypes_spinner.getSelectedItem().toString(), quantity);
            }
        });




        // return points once selected challenge types and frequency
        //once changed challenge types
        challengeTypes_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                returnPoints(db, pointsCounted, frequencyTypes_spinner.getSelectedItem().toString(), quantity);
                Log.d(TAG, "Challenge selected");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "No challenge selected");

            }
        });
        //once changed frequency
        frequencyTypes_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                returnPoints(db, pointsCounted, frequencyTypes_spinner.getSelectedItem().toString(), quantity);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });






        backToHomePage_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use requireActivity() since you are in a Fragment
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        editChallenge_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editChallengeInDatabase(db);
            }
        });

        deleteChallenge_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteChallengeFromDatabase(db);
            }
        });

        return view;
    }

    private void editChallengeInDatabase(FirebaseFirestore db) {
        userUid = getUserUid();

        if (userUid != null && !userUid.isEmpty()) {
            String challengeType = challengeTypes_spinner.getSelectedItem().toString();
            String frequency = frequencyTypes_spinner.getSelectedItem().toString();
            int quantity = Integer.parseInt(quantity_textView.getText().toString());
            String description = challengeDescription_editText.getText().toString().trim();

            totalPointsPerCompletion = Integer.parseInt(pointsCounted.getText().toString());

            Map<String, Object> updatedChallenge = new HashMap<>();
            updatedChallenge.put("challengeType", challengeType);


            updatedChallenge.put("frequency", frequency);
            updatedChallenge.put("quantity", quantity);
            updatedChallenge.put("description", description);
            updatedChallenge.put("notificationStatus", notificationStatus);
            updatedChallenge.put("totalPointsPerCompletion", totalPointsPerCompletion);
            updatedChallenge.put("progress", 0);

            db.collection("User Info")
                    .document(userUid)
                    .collection("Challenge Details")
                    .document(documentId)
                    .set(updatedChallenge, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(requireContext(), "Challenge updated", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error updating challenge", e);
                            Toast.makeText(requireContext(), "Error updating challenge", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void deleteChallengeFromDatabase(FirebaseFirestore db) {
        userUid = getUserUid();

        if (userUid != null && !userUid.isEmpty()) {
            db.collection("User Info")
                    .document(userUid)
                    .collection("Challenge Details")
                    .document(documentId)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(requireContext(), "Challenge deleted", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error deleting challenge", e);
                            Toast.makeText(requireContext(), "Error deleting challenge", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private String getUserUid() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            Log.d(TAG, "getUserUid: User not signed in");
            return "";
        }
    }

    private void loadChallengeDetails(String documentId) {
        userUid = getUserUid();

        if (userUid != null && !userUid.isEmpty()) {
            db.collection("User Info")
                    .document(userUid)
                    .collection("Challenge Details")
                    .document(documentId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Extract challenge details from the document
                                    String challengeType = document.getString("challengeType");
                                    String frequency = document.getString("frequency");
                                    quantity = document.getLong("quantity").intValue();
                                    String description = document.getString("description");
                                    int notificationStatus = document.getLong("notificationStatus").intValue();
                                    totalPointsPerCompletion = document.getLong("totalPointsPerCompletion").intValue();

                                    // Set values to your UI elements
                                    challengeTypes_spinner.setSelection(getIndexOfItem(challengeTypes_spinner, challengeType));
                                    frequencyTypes_spinner.setSelection(getIndexOfItem(frequencyTypes_spinner, frequency));
                                    quantityNumber_text.setText(String.valueOf(quantity)); // Update to use quantityNumber_text
                                    challengeDescription_editText.setText(description);
                                    pointsCounted.setText(String.valueOf(totalPointsPerCompletion));

                                    // Call returnPoints to recalculate points
                                    returnPoints(db, pointsCounted, frequencyTypes_spinner.getSelectedItem().toString(), quantity);
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.e(TAG, "Error getting document", task.getException());
                            }
                        }
                    });
        }
    }


    private int getIndexOfItem(Spinner spinner, String item) {
        if (spinner != null && spinner.getAdapter() != null) {
            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
            return adapter.getPosition(item);
        } else {
            return -1; // Return a default value or handle accordingly
        }
    }

    public void returnPoints(FirebaseFirestore db, TextView pointCounted, String frequency, int quantity) {
        Log.d(TAG, "return Points triggered");
        int frequencyValue = getFrequencyValue(frequency);

        if (challengeTypes_spinner.getSelectedItemPosition() != 0 && frequencyTypes_spinner.getSelectedItemPosition() != 0) {
            Log.d(TAG, "return Points conditions fulfilled");
            int challengeTypeCode = challengeTypes_spinner.getSelectedItemPosition();
            String challengeId = "challenge" + (getNumberOfDigits(challengeTypeCode) == 1 ? "00" : "0") + challengeTypeCode;
            Log.d(TAG, "challengeid: "+challengeId);
            searchChallengePoints(db, "Challenge", challengeId, pointCounted, frequencyValue, quantity);

        }
    }

    public int getFrequencyValue(String frequency){
        switch (frequency){
            case "Daily": return 1;
            case "Weekly": return 2;
            case "Monthly": return 4;
        }
        return -1;
    }

    private static int getNumberOfDigits(int number) {
        // Convert the integer to a string and get its length
        return Integer.toString(Math.abs(number)).length();
    }

    private static void searchChallengePoints(FirebaseFirestore db, String collectionName, String challengeName, TextView pointCounted,
                                              int frequencyValue, int quantity) {
        Log.d(TAG, "Search points triggered");

        CollectionReference challengesCollection = db.collection("Challenge");

        challengesCollection.document(challengeName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();

                            // Log the number of documents in the result
                            int resultSize = (document != null && document.exists()) ? 1 : 0;
                            Log.d(TAG, "Result size (number of documents): " + resultSize);

                            if (document != null && document.exists()) {
                                // Document exists, retrieve the points field
                                if (document.contains("Points")) {
                                    Object pointsObject = document.get("Points");

                                    if (pointsObject != null) {
                                        int challengePoints;

                                        if (pointsObject instanceof Long) {
                                            challengePoints = ((Long) pointsObject).intValue();
                                        } else if (pointsObject instanceof String) {
                                            try {
                                                challengePoints = Integer.parseInt((String) pointsObject);
                                            } catch (NumberFormatException e) {
                                                Log.e(TAG, "Error parsing 'Points' field as integer", e);
                                                challengePoints = 0; // Default value in case of parsing error
                                            }
                                        } else {
                                            Log.e(TAG, "'Points' field has an unexpected data type");
                                            challengePoints = 0; // Default value in case of unexpected data type
                                        }

                                        pointCounted.setText(String.valueOf((challengePoints)*quantity*frequencyValue));
                                        Log.d(TAG, "Challenge Points: " + challengePoints);
                                    } else {
                                        Log.e(TAG, "Points field is null in the document");
                                    }
                                } else {
                                    Log.e(TAG, "Document does not contain the 'Points' field");
                                }
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            // Handle errors
                            Exception e = task.getException();
                            if (e != null) {
                                Log.e(TAG, "Error querying Firestore", e);
                            }
                        }
                    }
                });
    }


}
