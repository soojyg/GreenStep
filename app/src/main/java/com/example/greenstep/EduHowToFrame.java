package com.example.greenstep;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class EduHowToFrame extends Fragment {

    TextView tabHowTo, tabAlternatives, txtEdit, txtUpload;
    FragmentContainerView container;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @NonNull Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.edu_howto_frame, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        tabHowTo = view.findViewById(R.id.tabHowTo);
        tabAlternatives = view.findViewById(R.id.tabAlternatives);
        txtEdit = view.findViewById(R.id.txtEdit);
        txtUpload = view.findViewById(R.id.txtUpload);
        container = view.findViewById(R.id.eduFragmentContainer);

        // Set the visibility of the "edit" and "upload" button
        FirebaseUtils.getUserType(new FirebaseUtils.UserTypeCallback() {
            @Override
            public void onCallback(String userType) {
                if(userType.equals("Admin")){
                    txtEdit.setVisibility(View.VISIBLE);
                    txtUpload.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(),"Error: Unable to determine user type.",Toast.LENGTH_SHORT).show();
            }
        });

        // Initial fragment
        showFragment(new EduHowToContent(),tabHowTo,tabAlternatives);

        // Set up click listeners for the tabs
        tabHowTo.setOnClickListener(v -> showFragment(new EduHowToContent(), tabHowTo, tabAlternatives));
        tabAlternatives.setOnClickListener(v -> showFragment(new EduAlternativesContent(), tabAlternatives, tabHowTo));

        txtEdit.setPaintFlags(txtEdit.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtUpload.setPaintFlags(txtUpload.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Set onClick listener for "Edit" and "Upload" text
//        txtEdit.setOnClickListener();
//
//        txtUpload.setOnClickListener();


    }

    private void showFragment(Fragment fragment, TextView selectedTab, TextView unselectedTab){
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.eduFragmentContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        // Update the appearance of the selected tab
        selectedTab.setTextColor(getResources().getColor(R.color.black));
        selectedTab.setTypeface(null, Typeface.BOLD);

        unselectedTab.setTextColor(getResources().getColor(R.color.bluegray_300));
        unselectedTab.setTypeface(null, Typeface.NORMAL);

    }
}
