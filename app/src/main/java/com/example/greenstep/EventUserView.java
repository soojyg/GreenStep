package com.example.greenstep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class EventUserView extends Fragment {
    RecyclerView recyclerView;
    List<DataClass> dataList;
    EventAdapter adapter;
    TextView txtNews,txtEvents;
    private EditAvailabilityManager editAvailabilityManager = new EditAvailabilityManager();
    SearchView searchView;

    private boolean isEditAvailable=false;
    private NavController navController;

    @Override
    public View onCreateView(@org.checkerframework.checker.nullness.qual.NonNull LayoutInflater inflater, @Nullable ViewGroup container, @org.checkerframework.checker.nullness.qual.NonNull Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.event_user_view,container, false);
        return rootView;

    }

    @Override
    public void onViewCreated(@org.checkerframework.checker.nullness.qual.NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        recyclerView = view.findViewById(R.id.recyclerViewEvent);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        dataList = new ArrayList<>();
        adapter = new EventAdapter(requireContext(), dataList, editAvailabilityManager, navController);
        recyclerView.setAdapter(adapter);
        txtNews = view.findViewById(R.id.tabNews);
        txtEvents = view.findViewById(R.id.tabEvents);
        txtEvents.setTextColor(getResources().getColor(R.color.black));
        txtEvents.setTypeface(null, Typeface.BOLD);
        searchView = view.findViewById(R.id.search);
        searchView.clearFocus();


// Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Events");

// Retrieve Data from Firestore
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    dataList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DataClass dataClass = document.toObject(DataClass.class);
                        dataClass.setKey(document.getId());
                        dataList.add(dataClass);
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


        txtNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(EventUserView.this,NewsUserView.class);
//                startActivity(intent);
                Navigation.findNavController(view).navigate(R.id.navigate_to_news);
            }
        });

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


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.event_user_view);
//        navController = Navigation.findNavController(view);
//        recyclerView = findViewById(R.id.recyclerViewEvent);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(EventUserView.this, 1);
//        recyclerView.setLayoutManager(gridLayoutManager);
//        AlertDialog.Builder builder = new AlertDialog.Builder(EventUserView.this);
//        builder.setCancelable(false);
//        builder.setView(R.layout.progress_layout);
//        AlertDialog dialog = builder.create();
//        dialog.show();
//        dataList = new ArrayList<>();
//        adapter = new EventAdapter(EventUserView.this, dataList, editAvailabilityManager, navController);
//        recyclerView.setAdapter(adapter);
////        txtNews = findViewById(R.id.txtNews);
//        searchView = findViewById(R.id.search);
//        searchView.clearFocus();
//
//
//// Initialize Firestore
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        CollectionReference collectionReference = db.collection("Events");
//
//// Retrieve Data from Firestore
//        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    dataList.clear();
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        DataClass dataClass = document.toObject(DataClass.class);
//                        dataClass.setKey(document.getId());
//                        dataList.add(dataClass);
//                    }
//                    adapter.notifyDataSetChanged();
//                    dialog.dismiss();
//                } else {
//                    // Handle errors
//                    dialog.dismiss();
//                    Toast.makeText(EventUserView.this, "Error getting data from Firestore", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//
////        txtNews.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                Intent intent = new Intent(EventUserView.this,NewsUserView.class);
////                startActivity(intent);
////            }
////        });
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                searchList(newText);
//                return true;
//            }
//        });
//
//
//    }
    public void searchList(String text){
        ArrayList<DataClass> searchList = new ArrayList<>();
        for (DataClass dataClass: dataList){
            if (dataClass.getDataTitle().toLowerCase().contains(text.toLowerCase())){
                searchList.add(dataClass);
            }
        }
        adapter.searchDataList(searchList);
    }




}