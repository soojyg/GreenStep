package com.example.greenstep;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CompletedChallengeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
// Import statements...

public class CompletedChallengeFragment extends DialogFragment {

    private static final String ARG_COMPLETED_CHALLENGES = "completed_challenges";

    public static CompletedChallengeFragment newInstance(Map<String, Object> completedChallenges) {
        CompletedChallengeFragment fragment = new CompletedChallengeFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_COMPLETED_CHALLENGES, new HashMap<>(completedChallenges));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed_challenge, container, false);

        // Extract completed challenges from arguments
        Map<String, Object> completedChallenges = (HashMap<String, Object>) getArguments().getSerializable(ARG_COMPLETED_CHALLENGES);

        // Log the content of completedChallenges
        Log.d("CompletedChallengeFragment", "Completed Challenges: " + completedChallenges);

        // Set dialog title
        TextView dialogTitle = view.findViewById(R.id.dialogTitle);
        dialogTitle.setText("Completed Challenges");

        // Display completed challenges dynamically
        LinearLayout completedChallengesLayout = view.findViewById(R.id.completedChallengesLayout);
        if (completedChallenges != null && !completedChallenges.isEmpty()) {
            for (Map.Entry<String, Object> entry : completedChallenges.entrySet()) {
                String challengeType = entry.getValue().toString();
                TextView challengeTextView = new TextView(getContext());
                challengeTextView.setText(challengeType);
                completedChallengesLayout.addView(challengeTextView);
            }
        } else {
            // No completed challenges to display
            TextView noChallengesTextView = new TextView(getContext());
            noChallengesTextView.setText("No challenges completed on this date.");
            completedChallengesLayout.addView(noChallengesTextView);
        }

        return view;
    }

}
