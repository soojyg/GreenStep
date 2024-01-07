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
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.atomic.AtomicReference;

public class EventsNewsFrame extends Fragment {
    TextView tabEvents, tabNews;
    FragmentContainerView container;
    BottomNavigationView bottomNavigationView;
    public static AtomicReference<String> selectedTabMain = new AtomicReference<>("Events");

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @NonNull Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.events_news_frame, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        tabEvents = view.findViewById(R.id.tabEvents);
        tabNews = view.findViewById(R.id.tabNews);
        container = view.findViewById(R.id.eventNewsFragmentContainer);
        bottomNavigationView = getActivity().findViewById(R.id.bottom_nav_view);

        // Initial fragment
        FirebaseUtils.getUserType(new FirebaseUtils.UserTypeCallback() {
            @Override
            public void onCallback(String userType) {
                if(userType.equals("Admin")){
                    showFragment(new EventAdminView(), tabEvents, tabNews);
                }
                else{
                    showFragment(new EventUserView(), tabEvents, tabNews);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(),"Error: Unable to determine user type.",Toast.LENGTH_SHORT).show();
            }
        });





        // Set up click listeners for the tabs
        tabEvents.setOnClickListener(v -> {
            selectedTabMain.set("Events");
            FirebaseUtils.getUserType(new FirebaseUtils.UserTypeCallback() {
                @Override
                public void onCallback(String userType) {
                    if(userType.equals("Admin")){
                        showFragment(new EventAdminView(), tabEvents, tabNews);
                    }
                    else{
                        showFragment(new EventUserView(), tabEvents, tabNews);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(requireContext(),"Error: Unable to determine user type.",Toast.LENGTH_SHORT).show();
                }
            });
            });
        tabNews.setOnClickListener(v -> {
            selectedTabMain.set("News");
            FirebaseUtils.getUserType(new FirebaseUtils.UserTypeCallback() {
                @Override
                public void onCallback(String userType) {
                    if(userType.equals("Admin")){
                        showFragment(new NewsAdminView(), tabEvents, tabNews);
                    }
                    else{
                        showFragment(new NewsUserView(), tabEvents, tabNews);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(requireContext(),"Error: Unable to determine user type.",Toast.LENGTH_SHORT).show();
                }
            });
        });

//        txtEdit.setPaintFlags(txtEdit.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//        txtUpload.setPaintFlags(txtUpload.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        // Set onClickListener for "Edit"
//        txtEdit.setOnClickListener(v ->{
//            if(selectedTabMain.get().equals("Events")){
//                Navigation.findNavController(view).navigate(R.id.action_to_editEventsPage);
//            } else{
//                Navigation.findNavController(view).navigate(R.id.action_to_editNewsPage);
//            }
//        });
//
//        // Set onClickListener for "Upload" text
//        txtUpload.setOnClickListener(v ->{
////            Intent intent = new Intent(requireContext(), EduContentUploadFrame.class);
////            startActivity(intent);
//
//            Navigation.findNavController(view).navigate(R.id.action_to_eventsNewsUploadFrame);
//
//        });


    }

    public static String getSelectedTabMain(){
        return selectedTabMain.get();
    }

    private void showFragment(Fragment fragment, TextView selectedTab, TextView unselectedTab){
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.eventNewsFragmentContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        // Update the appearance of the selected tab
        selectedTab.setTextColor(getResources().getColor(R.color.black));
        selectedTab.setTypeface(null, Typeface.BOLD);

        unselectedTab.setTextColor(getResources().getColor(R.color.bluegray_300));
        unselectedTab.setTypeface(null, Typeface.NORMAL);

    }
}
