package com.example.greenstep;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChallengeScreen extends Fragment {
    private static final String TAG = "ChallengeScreen"; // Define a tag for logging

    TextView tv;
    ImageButton button_addChallenge;
    RecyclerView challengeListRecyclerView;
    ArrayList<Challenge> challengeDetailsList;
    ChallengeListAdapter clAdapter;
    String userUid;

    final private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final private String collectionPath = "User Info";
    final private String collectionPath2 = getUserUid();
    final private String collectionPath3 = "Challenge Details";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @NonNull Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_challenge_screen, container, false);

        // Log the start of the onCreateView method
        Log.d(TAG, "onCreateView: Started");

        // from challenge fragment to addChallenge fragment
        button_addChallenge = rootView.findViewById(R.id.button_addChallenge);
        button_addChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an instance of the AddChallengeFragment
                AddChallengeFragment addChallengeFragment = new AddChallengeFragment();

                // Get the FragmentManager and start a FragmentTransaction
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the current fragment with the AddChallengeFragment
                fragmentTransaction.replace(R.id.mainFragmentContainer, addChallengeFragment);

                // Optional: Add the transaction to the back stack, so the user can navigate back
                fragmentTransaction.addToBackStack(null);

                // Commit the transaction
                fragmentTransaction.commit();
            }
        });

        Log.d(TAG, "onCreateView: Initialized RecyclerView and Adapter");

        challengeDetailsList = new ArrayList<>();
        fetchDataFromFirestore();
        clAdapter = new ChallengeListAdapter(getContext(), challengeDetailsList);
        challengeListRecyclerView = rootView.findViewById(R.id.challengeProgressRecyclerView);
        challengeListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        challengeListRecyclerView.setHasFixedSize(false);

        challengeListRecyclerView.setAdapter(clAdapter);

        CalendarView calendarView = rootView.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                // Ensure that month and day have two digits
                String formattedMonth = String.format("%02d", month + 1); // Adding 1 to month because it is zero-based
                String formattedDay = String.format("%02d", dayOfMonth);

                String selectedDate = year + "-" + formattedMonth + "-" + formattedDay;
                checkCompletedChallenges(selectedDate);
            }
        });





        // Log the end of the onCreateView method
        Log.d(TAG, "onCreateView: Ended");

        return rootView;
    }

    // ...

    // Add log messages within your methods as needed for troubleshooting

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

    public void updateChallenges(List<Challenge> newChallenges) {
        // Log information about updating challenges
        Log.d(TAG, "updateChallenges: Updating challenges");

        challengeDetailsList.clear();
        challengeDetailsList.addAll(newChallenges);

        // Notify the adapter about the data change
        clAdapter.notifyDataSetChanged();
    }

    private void fetchDataFromFirestore() {
        // Use Firebase Firestore to fetch data and update the dataList
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the "userinfo" collection for a specific user UID
        CollectionReference userCollectionRef = db.collection(collectionPath).document(collectionPath2).collection(collectionPath3);

        userCollectionRef
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int documentCount = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Challenge challengeDetails = document.toObject(Challenge.class);
                            String generatedDocumentId = document.getId();
                            challengeDetails.setDocumentId(generatedDocumentId);

                            challengeDetailsList.add(challengeDetails);
                            documentCount++;
                        }

                        Log.d(TAG, "fetchDataFromFirestore: Fetched " + documentCount + " documents");
                        clAdapter.notifyDataSetChanged();
                    } else {
                        Log.w("ChallengeScreen", "Error getting documents.", task.getException());
                    }
                });
    }

    private void checkCompletedChallenges(String selectedDate) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Replace "User Info" with your actual collection name
        // Replace "Calendar" with your actual sub-collection name
        db.collection("User Info")
                .document(getUserUid())
                .collection("Calendar")
                .document(selectedDate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Challenges completed on the selected date exist
                                Map<String, Object> completedChallengesData = document.getData();
                                Log.d(TAG, "Completed Challenges Data: " + completedChallengesData);
                                showCompletedChallengesDialog(completedChallengesData);
                            } else {
                                // No challenges completed on the selected date
                                Log.d(TAG, "No challenges completed on this date: "+selectedDate);
                                // You can handle this case or simply do nothing
                            }
                        } else {
                            Log.e(TAG, "Error getting completed challenges", task.getException());
                        }
                    }
                })
                .addOnSuccessListener(documentSnapshot -> {
                    // Log a message when the dialog is shown successfully
                    Log.d(TAG, "showCompletedChallengesDialog: Dialog shown successfully");
                })
                .addOnFailureListener(e -> {
                    // Log an error message if the dialog fails to show
                    Log.e(TAG, "showCompletedChallengesDialog: Error showing dialog", e);
                });
    }




    private void showCompletedChallengesDialog(Map<String, Object> completedChallenges) {
        // Use a DialogFragment or any other UI component to display the completed challenges
        CompletedChallengeFragment completedChallengeFragment = CompletedChallengeFragment.newInstance(completedChallenges);
        completedChallengeFragment.show(getChildFragmentManager(), "CompletedChallengesDialog");

    }
}
