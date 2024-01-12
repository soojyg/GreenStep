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
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ChallengeAddNewChallengeFragment extends Fragment {


    private static final String TAG = ChallengeAddNewChallengeFragment.class.getSimpleName();
    private static final String COLLECTION_USER_INFO = "User Info";
    private static final String COLLECTION_CHALLENGE_DETAILS = "Challenge Details";

    private Spinner challengeTypesSpinner;
    private Spinner frequencyTypesSpinner;
    private ImageButton quantityIncrementButton;
    private ImageButton quantityDecrementButton;
    private TextView quantityNumberText;
    private int quantity = 1;
    private int notificationStatus;
    private Switch switchNotificationStatus;
    private TextView pointsCounted;
    private ImageButton backToHomePageButton;
    private Button startChallengeButton;
    private EditText challengeDescriptionEditText;

    private String challengeType;
    private String frequency;
    private String description;
    private String userUid;


    public ChallengeAddNewChallengeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_challenge, container, false);

        // Initialize Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Initialize UI components
        initializeUIComponents(view, db);

        return view;
    }

    private void initializeUIComponents(View view, FirebaseFirestore db) {
        // Initialize UI components...
        pointsCounted = view.findViewById(R.id.pointsCounted);

        challengeTypesSpinner = view.findViewById(R.id.challengeType_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.challenge_types,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        challengeTypesSpinner.setAdapter(adapter);

        frequencyTypesSpinner = view.findViewById(R.id.frequency_spinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.frequency_types,
                android.R.layout.simple_spinner_item
        );
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencyTypesSpinner.setAdapter(adapter2);

        quantityIncrementButton = view.findViewById(R.id.quantityIncrement_button);
        quantityDecrementButton = view.findViewById(R.id.quantityDecrement_button);
        quantityNumberText = view.findViewById(R.id.quantity_number);

        backToHomePageButton = view.findViewById(R.id.backToMainPage_addChallenge_button);

        quantityIncrementButton.setOnClickListener(v -> {
            if (quantity < 10)
                quantity++;
            quantityNumberText.setText(String.valueOf(quantity));
            returnPoints(db, pointsCounted, frequencyTypesSpinner.getSelectedItem().toString(), quantity);
        });

        quantityDecrementButton.setOnClickListener(v -> {
            if (quantity > 1)
                quantity--;
            quantityNumberText.setText(String.valueOf(quantity));
            returnPoints(db, pointsCounted, frequencyTypesSpinner.getSelectedItem().toString(), quantity);
        });

        challengeTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                returnPoints(db, pointsCounted, frequencyTypesSpinner.getSelectedItem().toString(), quantity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        frequencyTypesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                returnPoints(db, pointsCounted, frequencyTypesSpinner.getSelectedItem().toString(), quantity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        backToHomePageButton.setOnClickListener(v -> requireActivity().onBackPressed());

        startChallengeButton = view.findViewById(R.id.startChallenge_button);
        challengeDescriptionEditText = view.findViewById(R.id.challengeDescription_editText);
        startChallengeButton.setOnClickListener(v -> {
            if (challengeTypesSpinner.getSelectedItemPosition() == 0) {
                Toast.makeText(requireContext(), R.string.select_challenge_type, Toast.LENGTH_SHORT).show();
            } else if (frequencyTypesSpinner.getSelectedItemPosition() == 0) {
                Toast.makeText(requireContext(), R.string.select_frequency, Toast.LENGTH_SHORT).show();
            } else {
                addChallengeToDatabase(db);
            }
        });
    }
    private void addChallengeToDatabase(FirebaseFirestore db) {
        if (getActivity() == null || !isAdded()) {
            return;
        }

        challengeType = challengeTypesSpinner.getSelectedItem().toString();
        frequency = frequencyTypesSpinner.getSelectedItem().toString();
        description = challengeDescriptionEditText.getText().toString().trim();
        userUid = getUserUid();

        Map<String, Object> challengeData = new HashMap<>();
        challengeData.put("challengeType", challengeType);
        challengeData.put("frequency", frequency);
        challengeData.put("quantity", quantity);
        challengeData.put("description", description);
        challengeData.put("userUid", userUid);
        challengeData.put("totalPointsPerCompletion", Integer.parseInt("" + pointsCounted.getText()));
        challengeData.put("totalPointsCollected", 0);
        challengeData.put("progress", 0);
        challengeData.put("notificationStatus", notificationStatus);

        String timestampId = String.valueOf(System.currentTimeMillis());

        db.collection(COLLECTION_USER_INFO)
                .document(userUid)
                .collection(COLLECTION_CHALLENGE_DETAILS)
                .document(timestampId)
                .set(challengeData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), R.string.challenge_added, Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding challenge", e);
                    Toast.makeText(requireContext(), R.string.error_adding_challenge, Toast.LENGTH_SHORT).show();
                });
    }




    private void returnPoints(FirebaseFirestore db, TextView pointCounted, String frequency, int quantity) {
        int frequencyValue = getFrequencyValue(frequency);

        if (challengeTypesSpinner.getSelectedItemPosition() != 0 && frequencyTypesSpinner.getSelectedItemPosition() != 0) {
            int challengeTypeCode = challengeTypesSpinner.getSelectedItemPosition();
            String challengeId = "challenge" + (getNumberOfDigits(challengeTypeCode) == 1 ? "00" : "0") + challengeTypeCode;
            searchChallengePoints(db, "Challenge", challengeId, pointCounted, frequencyValue, quantity);
        }
    }

    private static void searchChallengePoints(FirebaseFirestore db, String collectionName, String challengeName,
                                              TextView pointCounted, int frequencyValue, int quantity) {
        CollectionReference challengesCollection = db.collection("Challenge");

        challengesCollection.document(challengeName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && task.getResult().exists()) {
                            Object pointsObject = task.getResult().get("Points");

                            if (pointsObject != null) {
                                int challengePoints;

                                if (pointsObject instanceof Long) {
                                    challengePoints = ((Long) pointsObject).intValue();
                                } else if (pointsObject instanceof String) {
                                    try {
                                        challengePoints = Integer.parseInt((String) pointsObject);
                                    } catch (NumberFormatException e) {
                                        challengePoints = 0;
                                    }
                                } else {
                                    challengePoints = 0;
                                }

                                pointCounted.setText(String.valueOf((challengePoints) * quantity * frequencyValue));
                            } else {
                                Log.e("ChallengeAddNewChallengeFragment", "Points field is null in the document");
                            }
                        } else {
                            Log.d("ChallengeAddNewChallengeFragment", "No such document");
                        }
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            Log.e("ChallengeAddNewChallengeFragment", "Error querying Firestore", e);
                        }
                    }
                });
    }

    private int getFrequencyValue(String frequency) {
        switch (frequency) {
            case "Daily":
                return 1;
            case "Weekly":
                return 2;
            case "Monthly":
                return 4;
            default:
                return -1;
        }
    }

    private static int getNumberOfDigits(int number) {
        return Integer.toString(Math.abs(number)).length();
    }

    private String getUserUid() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return "";
        }
    }
}
