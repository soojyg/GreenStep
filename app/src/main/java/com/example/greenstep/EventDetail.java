package com.example.greenstep;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.nullness.qual.Nullable;

public class EventDetail extends Fragment {
    TextView detailDesc, detailTitle, detailLang;
    ImageView detailImage,cancelBtn;
    FloatingActionButton deleteButton, editButton;
    String key = "";
    String imageUrl = "";
    private EventDetail.OnItemDeletedListener onItemDeletedListener;
    private NavController navController;
    BottomNavigationView bottomNavigationView;

    @Override
    public View onCreateView(@org.checkerframework.checker.nullness.qual.NonNull LayoutInflater inflater, @Nullable ViewGroup container, @org.checkerframework.checker.nullness.qual.NonNull Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.event_detail,container, false);
        return rootView;

    }

        @Override
    public void onViewCreated(@org.checkerframework.checker.nullness.qual.NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        detailDesc = view.findViewById(R.id.detailDesc);
        navController = Navigation.findNavController(view);
        detailImage = view.findViewById(R.id.detailImage);
        detailTitle = view.findViewById(R.id.detailEventsTitle);
        deleteButton = view.findViewById(R.id.deleteButton);
        editButton = view.findViewById(R.id.editButton);
        detailLang = view.findViewById(R.id.detailLang);
        cancelBtn = view.findViewById(R.id.cancel);

        // Hide bottom navigation initially
        bottomNavigationView = getActivity().findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setVisibility(View.GONE);

        // Retrieve data from arguments
        Bundle args = getArguments();

        if (args != null){
            detailDesc.setText(args.getString("Description"));
            detailTitle.setText(args.getString("Title"));
            detailLang.setText(args.getString("Language"));
            key = args.getString("Key");
            imageUrl = args.getString("Image");

            // Load image using Glide library
            Glide.with(this).load(args.getString("Image")).into(detailImage);
        }

        // Set click listener for delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(view);
            }
        });

        // Set click listener for edit button
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Prepare bundle for navigation to edit screen
                Bundle bundle = new Bundle();
                bundle.putString("Title", detailTitle.getText().toString());
                bundle.putString("Description", detailDesc.getText().toString());
                bundle.putString("Language", detailLang.getText().toString());
                bundle.putString("Image", imageUrl);
                bundle.putString("Key", key);
                // Navigate to edit screen
                navController.navigate(R.id.action_to_eventUpdate, bundle);
            }
        });

        // Retrieve the item deletion listener from the parent activity
        onItemDeletedListener = (EventDetail.OnItemDeletedListener) requireActivity().getIntent().getSerializableExtra("listener");

        // Set click listener for cancel button
        cancelBtn.setOnClickListener(v -> {
            // Navigate back to events screen
            navController.navigate(R.id.navigate_to_events);
            // Make bottom navigation visible
            bottomNavigationView.setVisibility(View.VISIBLE);
        });
    }

    /**
     * Displays a confirmation dialog for item deletion. Initiates deletion from Firestore and Storage upon user confirmation.
     *
     * @param view The current view.
     */
    private void deleteItem(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this item?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked Yes, delete the item
                // Delete from Firestore
                deleteFromFirestore(key);

                // Delete from Storage
                deleteFromStorage(imageUrl);

                Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show();

                if (onItemDeletedListener != null) {
                    onItemDeletedListener.onItemDeleted(key);
                }

                Navigation.findNavController(view).popBackStack();
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked No, do nothing or handle accordingly
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    /**
     * Deletes a document from Firestore based on the provided documentId.
     *
     * @param documentId The unique identifier of the document to be deleted.
     */
    private void deleteFromFirestore(String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Events");

        collectionReference.document(documentId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Document successfully deleted from Firestore
                        Log.d(TAG, "DocumentSnapshot successfully deleted from Firestore");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle errors
                        Log.w(TAG, "Error deleting document from Firestore", e);
                        Toast.makeText(requireContext(), "Error deleting document from Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Deletes a file from Firebase Storage based on the provided imageUrl.
     *
     * @param imageUrl The URL of the file in Firebase Storage.
     */
    private void deleteFromStorage(String imageUrl) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);

        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // File successfully deleted from Storage
                Log.d(TAG, "File successfully deleted from Storage");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle errors
                Log.w(TAG, "Error deleting file from Storage", e);
                Toast.makeText(requireContext(), "Error deleting file from Storage", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Interface to be implemented by components interested in being notified when an item is deleted.
     */
    public interface OnItemDeletedListener {
        void onItemDeleted(String key);
    }
}
