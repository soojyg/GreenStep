package com.example.greenstep;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import androidx.appcompat.widget.SearchView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

//import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;

public class EduAlternativesContent extends Fragment {
    private SearchView searchBar;
    private RecyclerView recyclerView;
    private ScrollView scrollView;
    private LinearLayout editLayout;
    TextView txtEdit,tabHowto, tabAlter;
    FloatingActionButton uploadBtn;
    private ArrayList<AlternativesDataClass> dataList;
    private AdapterDisplayEduAlternatives adapter;
    private NavController navController;
    final private FirebaseFirestore firestoreDbRef = FirebaseFirestore.getInstance();
    final private String collectionPath = "Edu Content Tips";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @NonNull Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.edu_alternatives_content,container,false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        searchBar = view.findViewById(R.id.searchBar);
        searchBar.clearFocus();

        scrollView = view.findViewById(R.id.scrollView);
        editLayout = view.findViewById(R.id.linearRowedit);
        txtEdit = view.findViewById(R.id.txtEdit);
        tabHowto = view.findViewById(R.id.tabHowTo);
        tabAlter = view.findViewById(R.id.tabAlternatives);
        tabAlter.setTextColor(getResources().getColor(R.color.black));
        tabAlter.setTypeface(null, Typeface.BOLD);
        uploadBtn = view.findViewById(R.id.uploadBtn);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        dataList = new ArrayList<>();
        adapter = new AdapterDisplayEduAlternatives(dataList, requireContext(), navController);
        recyclerView.setAdapter(adapter);
        // Set the visibility of the "edit" and "upload" button
        FirebaseUtils.getUserType(new FirebaseUtils.UserTypeCallback() {
            @Override
            public void onCallback(String userType) {
                if(userType.equals("Admin")){
                    editLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(),"Error: Unable to determine user type.",Toast.LENGTH_SHORT).show();
            }
        });

        // Load documentID, title, tip1, tip2, image url, and image from firestore database
        firestoreDbRef.collection(collectionPath).get()
                .addOnCompleteListener(task ->{
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            // Create a AlternativesDataClass instance with data from the document
                            AlternativesDataClass alternativesDataClass = new AlternativesDataClass(
                                    document.getId(),
                                    (String) document.get(FieldPath.of("Title")),
                                    (String) document.get(FieldPath.of("Tip 1")),
                                    (String) document.get(FieldPath.of("Tip 2")),
                                    document.getString("ImageUrl")
                            );
                            dataList.add(alternativesDataClass);
                        }
                        // Notify the adapter that the data has changed
                        adapter.notifyDataSetChanged();

                    } else{
                        Toast.makeText(requireContext(),"Failed to load image.",Toast.LENGTH_SHORT).show();
                    }
                });

        txtEdit.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_to_editAlternativesPage);
        });

        uploadBtn.setOnClickListener(v ->{
            Navigation.findNavController(view).navigate(R.id.action_to_alternativesUploadPage);
        });

        tabHowto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.navigate_to_howto);
            }
        });

        // Set up a SearchView to filter the RecyclerView based on user input
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // Update the RecyclerView as the user types in the search query
            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });
    }

    // Method to filter the data list based on the search query
    public void searchList(String text){
        // Create a new list to store filtered items
        ArrayList<AlternativesDataClass> searchList = new ArrayList<>();
        // Iterate through the original data list and add matching items to the searchList
        for(AlternativesDataClass dc: dataList){
            if(dc.getTitle().toLowerCase().contains(text.toLowerCase())){
                searchList.add(dc);
            }
        }
        // Update the adapter with the filtered list
        adapter.searchDataList(searchList);
    }
}
