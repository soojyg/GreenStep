package com.example.greenstep;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class EduContentUploadFrame extends Fragment {

    TextView title, tabHowTo, tabAlternatives;
    ImageView closeIcon;

    FragmentContainerView container;
    BottomNavigationView bottomNavigationView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @NonNull Bundle savedInstanceState){
        setRetainInstance(false);
        View rootView = inflater.inflate(R.layout.edu_content_upload_frame, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        title = view.findViewById(R.id.uploadTitle);
        tabHowTo = view.findViewById(R.id.tabHowTo);
        tabAlternatives = view.findViewById(R.id.tabAlternatives);
        closeIcon = view.findViewById(R.id.close);
        container = view.findViewById(R.id.eduUploadContainer);
        bottomNavigationView = getActivity().findViewById(R.id.bottom_nav_view);

        bottomNavigationView.setVisibility(View.GONE);


        // Initial fragment
        // get the current selected tab from eduContentFrame
        if(EduContentFrame.getSelectedTabMain().equals("How To"))
            showFragment(new UploadHowTo(),tabHowTo,tabAlternatives);
        else
            showFragment(new UploadAlternatives(),tabAlternatives,tabHowTo);

        // Set up click listeners for the tabs
        tabHowTo.setOnClickListener(v -> {
            showFragment(new UploadHowTo(), tabHowTo, tabAlternatives);
        });
        tabAlternatives.setOnClickListener(v -> {
            showFragment(new UploadAlternatives(), tabAlternatives, tabHowTo);
        });

        closeIcon.setOnClickListener(v ->{

            Navigation.findNavController(view).popBackStack();
                bottomNavigationView.setVisibility(View.VISIBLE);

        });
    }

    private void showFragment(Fragment fragment, TextView selectedTab, TextView unselectedTab){
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.eduUploadContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        // Update the appearance of the selected tab
        selectedTab.setTextColor(getResources().getColor(R.color.black));
        selectedTab.setTypeface(null, Typeface.BOLD);

        unselectedTab.setTextColor(getResources().getColor(R.color.bluegray_300));
        unselectedTab.setTypeface(null, Typeface.NORMAL);

    }

}
