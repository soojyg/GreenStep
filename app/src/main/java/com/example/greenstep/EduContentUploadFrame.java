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
//
//        // Save the original listener to a temporary variable
//        BottomNavigationView.OnNavigationItemSelectedListener originalListener = item -> {
//            // Handle item selection here
//            return true; // Return true if you want to handle the item selection
//        };
//
//        // Set the original listener
//        bottomNavigationView.setOnNavigationItemSelectedListener(originalListener);

        // Disable user interaction with the bottom navigation bar
//        if (bottomNavigationView != null) {
////            bottomNavigationView.setOnTouchListener((v, event) -> true); // Disable touch events
////            bottomNavigationView.setFocusable(false); // Disable focus
////            bottomNavigationView.setClickable(false);
//////            bottomNavigationView.setEnabled(false);
////            bottomNavigationView.setOnNavigationItemSelectedListener(item -> false);
////            bottomNavigationView.setEnabled(false);
////            bottomNavigationView.setFocusable(false);
////            bottomNavigationView.setFocusableInTouchMode(false);
////            bottomNavigationView.setClickable(false);
////            bottomNavigationView.setContextClickable(false);
////            bottomNavigationView.setOnClickListener(null);
            bottomNavigationView.setVisibility(View.GONE);
//        } else{
//            Toast.makeText(requireContext(), "Bottom nav bar not found.",Toast.LENGTH_SHORT).show();
//            Log.e("EduContentUploadFrame", "Bottom nav bar not found.");
//        }

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
            // Re-enable the BottomNavigationView
//            if (bottomNavigationView != null) {
//                bottomNavigationView.setClickable(true);
//                bottomNavigationView.setEnabled(true);
//                bottomNavigationView.setOnNavigationItemSelectedListener(item -> true);
//
//
//            }
            Navigation.findNavController(view).popBackStack();
//            Navigation.findNavController(view).navigate(R.id.DestEventNews);
//            onBackPressed();
//            if (bottomNavigationView != null) {
                bottomNavigationView.setVisibility(View.VISIBLE);
                // Re-enable the BottomNavigationView when leaving the fragment
//            bottomNavigationView.setClickable(true);
//            bottomNavigationView.setEnabled(true);
//                bottomNavigationView.setOnNavigationItemSelectedListener(null);
//
//                // Set back the original listener
//                bottomNavigationView.setOnNavigationItemSelectedListener(originalListener);
//                bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
//                    int itemId = item.getItemId();
//
//                    if (itemId == R.id.DestReward) {
//                        // Handle action for menu item DestReward
////                        Navigation.findNavController(view).navigate(R.id.action_to_reward);
//                        return true;
//                    } else if (itemId == R.id.DestEventNews) {
//                        // Handle action for another menu item
////                        Navigation.findNavController(view).navigate(R.id.DestEventNews);
//                        return true;
//                    } else {
//                        return false;
//                    }
//                });
//                bottomNavigationView.setOnNavigationItemSelectedListener(null);
//                // Set a new listener or the original listener if you had saved it
//                bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
//                    // Handle item selection here
//                    return true; // Return true if you want to handle the item selection
//                });
//            } else{
//                Toast.makeText(requireContext(), "Bottom nav bar not found.",Toast.LENGTH_SHORT).show();
//                Log.e("EduContentUploadFrame", "Bottom nav bar not found.");
//            }
//
//
        });
    }

//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        Log.d("EduContentUploadFrame", "onDestroyView called");
//        if (getView() != null){
//            if (bottomNavigationView != null) {
//                // Re-enable the BottomNavigationView when leaving the fragment
////            bottomNavigationView.setClickable(true);
////            bottomNavigationView.setEnabled(true);
////                bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
////                    int itemId = item.getItemId();
////
////                    if (itemId == R.id.DestReward) {
////                        // Handle action for menu item DestReward
////                        Navigation.findNavController(requireView()).navigate(R.id.DestReward);
////                        return true;
////                    } else if (itemId == R.id.DestEventNews) {
////                        // Handle action for another menu item
////                        Navigation.findNavController(requireView()).navigate(R.id.DestEventNews);
////                        return true;
////                    } else {
////                        return false;
////                    }
////                });
//
//
//            } else{
//                Toast.makeText(requireContext(), "Bottom nav bar not found.",Toast.LENGTH_SHORT).show();
//                Log.e("EduContentUploadFrame", "Bottom nav bar not found.");
//            }
//        }
//
//    }

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
