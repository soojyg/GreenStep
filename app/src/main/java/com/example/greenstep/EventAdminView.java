package com.example.greenstep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EventAdminView extends Fragment {

    FloatingActionButton fab;
    RecyclerView recyclerView;
    List<DataClass> dataList;
    EventAdapter adapter;
    TextView edit,txtNews,txtEvents;
    private EditAvailabilityManager editAvailabilityManager = new EditAvailabilityManager();
    private boolean isEditAvailable=false;
    private NavController navController;

    @Override
    public View onCreateView(@org.checkerframework.checker.nullness.qual.NonNull LayoutInflater inflater, @Nullable ViewGroup container, @org.checkerframework.checker.nullness.qual.NonNull Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.event_admin_view,container, false);
        return rootView;

    }

     @Override
    public void onViewCreated(@org.checkerframework.checker.nullness.qual.NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        recyclerView = view.findViewById(R.id.recyclerViewEvent);
        fab = view.findViewById(R.id.fab);
        txtNews = view.findViewById(R.id.tabNews);
        txtEvents = view.findViewById(R.id.tabEvents);

        // Set initial styling for tabs
        txtEvents.setTextColor(getResources().getColor(R.color.black));
        txtEvents.setTypeface(null, Typeface.BOLD);

        // Set up GridLayoutManager for RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Show progress dialog while fetching data
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Initialize data list, adapter, and UI elements
        dataList = new ArrayList<>();
        adapter = new EventAdapter(requireContext(), dataList, editAvailabilityManager, navController);
        recyclerView.setAdapter(adapter);
        edit = view.findViewById(R.id.txtEdit);

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Events");

        // Retrieve Data from Firestore
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Clear previous data and populate dataList with new data
                    dataList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DataClass dataClass = document.toObject(DataClass.class);
                        dataClass.setKey(document.getId());
                        dataList.add(dataClass);
                    }
                    // Notify the adapter of data change and dismiss the progress dialog
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                } else {
                    // Handle errors
                    dialog.dismiss();
                    Toast.makeText(requireContext(), "Error getting data from Firestore", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Set up click listeners for UI elements
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_to_eventUpload);
            }
        });

        Log.d("EditStatusORI", "isEditAvailable ORI: " + isEditAvailable);


        // Set up click listener for the edit button
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editAvailabilityManager.toggleEditAvailability();
                handleEditAvailability();
            }
        });

        // Navigate to news fragment on tab click
        txtNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.navigate_to_news);
            }
        });

    }

    /**
     * Handles UI changes based on edit availability, showing a complete dialog if edit is available.
     * Updates the UI accordingly and displays a toast message.
     */
    public void handleEditAvailability() {
        boolean isEditAvailable = editAvailabilityManager.isEditAvailable();

        Log.d("EditStatusRETURN", "isEditAvailableRETURN: " + isEditAvailable);

        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isEditAvailable) {
                    Log.d("EditStatusCAN", "isEditAvailableCAN: " + isEditAvailable);
                    showCompleteDialog();
                    Toast.makeText(requireContext(), "Edit Available", Toast.LENGTH_SHORT).show();
                } else {
                    // Reset the text if edit is unavailable
                    edit.setText("Edit"); // You may want to set it to a different value or empty
                    Toast.makeText(requireContext(), "Edit Unavailable", Toast.LENGTH_SHORT).show();
                    Log.d("EditStatusCANNOT", "isEditAvailableCANNOT: " + isEditAvailable);
                }
            }
        });

        Log.d("EditStatusFINAL", "isEditAvailableFINAL: " + isEditAvailable);
    }

    /**
     * Shows a custom dialog with a completion message.
     * The dialog provides instructions to click on the card view to modify the information.
     */
    private void showCompleteDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());

        // Inflate the custom layout
        View customLayout = getLayoutInflater().inflate(R.layout.popup_edit, null);

        builder.setView(customLayout);

        // Set a message in the custom dialog
        TextView complete_msg = customLayout.findViewById(R.id.edit_msg);
        complete_msg.setText("Please click on the card view to modified the information.");

        // Create the dialog
        android.app.AlertDialog dialog = builder.create();

        // Set background to null to remove default AlertDialog background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        // Show the dialog
        dialog.show();
    }

}
