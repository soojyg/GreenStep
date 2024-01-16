package com.example.greenstep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentHostCallback;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NewsUserView extends Fragment {

    RecyclerView recyclerView;
    List<NewsDataClass> dataList;
    NewsAdapter adapter;
    TextView txtEvents, txtNews;
    private EditAvailabilityManager editAvailabilityManager = new EditAvailabilityManager();
    SearchView searchView;
    private NavController navController;

    @Override
    public View onCreateView(@org.checkerframework.checker.nullness.qual.NonNull LayoutInflater inflater, @Nullable ViewGroup container, @org.checkerframework.checker.nullness.qual.NonNull Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.news_user_view,container, false);
        return rootView;

    }

     @Override
    public void onViewCreated(@org.checkerframework.checker.nullness.qual.NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize navigation controller and recycler view
        navController = Navigation.findNavController(view);
        recyclerView = view.findViewById(R.id.recyclerViewNews);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Show progress dialog while data is being fetched
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Initialize data list and adapter
        dataList = new ArrayList<>();
        adapter = new NewsAdapter(requireContext(), dataList, editAvailabilityManager,navController);
        recyclerView.setAdapter(adapter);
        txtEvents = view.findViewById(R.id.tabEvents);
        txtNews = view.findViewById(R.id.tabNews);
        txtNews.setTextColor(getResources().getColor(R.color.black));
        txtNews.setTypeface(null, Typeface.BOLD);
        searchView = view.findViewById(R.id.search);
        searchView.clearFocus();

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("News");

        // Retrieve Data from Firestore
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    dataList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Convert Firestore document to NewsDataClass object
                        NewsDataClass NewsDataClass = document.toObject(NewsDataClass.class);
                        NewsDataClass.setKey(document.getId());
                        dataList.add(NewsDataClass);
                    }
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                } else {
                    // Handle errors
                    dialog.dismiss();
                    Toast.makeText(requireContext(), "Error getting data from Firestore", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set click listener for switching to Events tab
        txtEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.navigate_to_events);
            }
        });

        // Set search view query text change listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

    }

    /**
     * Filters the data list based on the given text and updates the adapter.
     *
     * @param text The text to search for in the data list.
     */
    public void searchList(String text){
        // Create a new list to store the filtered items
        ArrayList<NewsDataClass> searchList = new ArrayList<>();
        // Iterate through the original data list
        for (NewsDataClass NewsDataClass: dataList){
            // Check if the item's title contains the search text (case-insensitive)
            if (NewsDataClass.getDataTitle().toLowerCase().contains(text.toLowerCase())){
                // Add the matching item to the search list
                searchList.add(NewsDataClass);
            }
        }
        // Update the adapter with the filtered data list
        adapter.searchDataList(searchList);
    }
}
