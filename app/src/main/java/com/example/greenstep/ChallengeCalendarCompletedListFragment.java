package com.example.greenstep;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

/**
 * A DialogFragment to display completed challenges for a specific date.
 */
public class ChallengeCalendarCompletedListFragment extends DialogFragment {

    private static final String ARG_COMPLETED_CHALLENGES = "completed_challenges";

    /**
     * Factory method to create a new instance of ChallengeCalendarCompletedListFragment.
     *
     * @param completedChallenges A map containing completed challenges data.
     * @return A new instance of ChallengeCalendarCompletedListFragment.
     */
    public static ChallengeCalendarCompletedListFragment newInstance(Map<String, Object> completedChallenges) {
        ChallengeCalendarCompletedListFragment fragment = new ChallengeCalendarCompletedListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_COMPLETED_CHALLENGES, new HashMap<>(completedChallenges));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed_challenge, container, false);

        // Extract completed challenges from arguments
        Map<String, Object> completedChallenges = extractCompletedChallenges();

        // Log the content of completedChallenges
        logCompletedChallenges(completedChallenges);

        // Set dialog title
        setDialogTitle(view);

        // Display completed challenges dynamically
        displayCompletedChallenges(view, completedChallenges);

        return view;
    }

    private Map<String, Object> extractCompletedChallenges() {
        return (HashMap<String, Object>) getArguments().getSerializable(ARG_COMPLETED_CHALLENGES);
    }

    private void logCompletedChallenges(Map<String, Object> completedChallenges) {
        Log.d("ChallengeCalendarCompletedListFragment", "Completed Challenges: " + completedChallenges);
    }

    private void setDialogTitle(View view) {
        TextView dialogTitle = view.findViewById(R.id.dialogTitle);
        dialogTitle.setText("Completed Challenges");
    }

    private void displayCompletedChallenges(View view, Map<String, Object> completedChallenges) {
        LinearLayout completedChallengesLayout = view.findViewById(R.id.completedChallengesLayout);

        if (completedChallenges != null && !completedChallenges.isEmpty()) {
            for (Map.Entry<String, Object> entry : completedChallenges.entrySet()) {
                String challengeType = entry.getValue().toString();
                TextView challengeTextView = createChallengeTextView(view.getContext(), challengeType);
                completedChallengesLayout.addView(challengeTextView);
            }
        } else {
            displayNoChallengesMessage(completedChallengesLayout);
        }
    }

    private TextView createChallengeTextView(android.content.Context context, String text) {
        TextView challengeTextView = new TextView(context);
        challengeTextView.setText(text);
        return challengeTextView;
    }

    private void displayNoChallengesMessage(LinearLayout completedChallengesLayout) {
        TextView noChallengesTextView = createNoChallengesTextView(completedChallengesLayout.getContext());
        completedChallengesLayout.addView(noChallengesTextView);
    }

    private TextView createNoChallengesTextView(android.content.Context context) {
        TextView noChallengesTextView = new TextView(context);
        noChallengesTextView.setText("No challenges completed on this date.");
        return noChallengesTextView;
    }
}
