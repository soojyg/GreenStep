package com.example.greenstep;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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

public class NewsDetail extends Fragment {
    TextView detailAuthor, detailTitle, detailLang, detailDate;
    ImageView detailImage,cancelBtn;
    FloatingActionButton deleteButton, editButton;
    String key = "";
    String imageUrl = "";
    private NewsDetail.OnItemDeletedListener onItemDeletedListener;
    BottomNavigationView bottomNavigationView;
    private NavController navController;


    @Override
    public View onCreateView(@org.checkerframework.checker.nullness.qual.NonNull LayoutInflater inflater, @Nullable ViewGroup container, @org.checkerframework.checker.nullness.qual.NonNull Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.news_detail,container, false);
        return rootView;

    }

        @Override
    public void onViewCreated(@org.checkerframework.checker.nullness.qual.NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        detailAuthor = view.findViewById(R.id.detailAuthor);
        detailImage = view.findViewById(R.id.detailImage);
        detailDate = view.findViewById(R.id.detailDate);
        detailTitle = view.findViewById(R.id.detailNewsTitle);
        deleteButton = view.findViewById(R.id.deleteButton);
        editButton = view.findViewById(R.id.editButton);
        cancelBtn = view.findViewById(R.id.cancel);
        detailLang = view.findViewById(R.id.detailLang);
        bottomNavigationView = getActivity().findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setVisibility(View.GONE);
        Bundle args = getArguments();

        if (args != null){
            detailTitle.setText(args.getString("Title"));
            detailAuthor.setText(args.getString("Author"));
            detailLang.setText(args.getString("Language"));
            detailDate.setText(args.getString("Date"));
            key = args.getString("Key");
            imageUrl = args.getString("Image");
            Glide.with(this).load(args.getString("Image")).into(detailImage);
        }
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(view);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("Title", detailTitle.getText().toString());
                bundle.putString("Author", detailAuthor.getText().toString());
                bundle.putString("Date",detailDate.getText().toString());
                bundle.putString("Language", detailLang.getText().toString());
                bundle.putString("Image", imageUrl);
                bundle.putString("Key", key);
                navController.navigate(R.id.action_to_newsUpdate, bundle);

            }
        });
        onItemDeletedListener = (NewsDetail.OnItemDeletedListener) requireActivity().getIntent().getSerializableExtra("listener");

        cancelBtn.setOnClickListener(v -> {
            navController.navigate(R.id.navigate_to_news);
            bottomNavigationView.setVisibility(View.VISIBLE);
        });
    }

    /**
     * Displays a confirmation dialog for item deletion.
     *
     * @param view The view associated with the action.
     */
    private void deleteItem(View view) {
        // Create an AlertDialog to confirm item deletion
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this item?");

        // Set up positive button for confirmation
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked Yes, delete the item
                // Delete from Firestore
                deleteFromFirestore(key);

                // Delete from Storage
                deleteFromStorage(imageUrl);

                Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show();

                // Notify the listener if available
                if (onItemDeletedListener != null) {
                    onItemDeletedListener.onItemDeleted(key);
                }

                // Navigate back and make bottom navigation visible
                Navigation.findNavController(view).popBackStack();
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });
        // Set up negative button for cancellation
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked No, do nothing or handle accordingly
                dialogInterface.dismiss();
            }
        });

        // Show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    /**
     * Deletes a document from Firestore based on the provided document ID.
     *
     * @param documentId The ID of the document to be deleted.
     */
    private void deleteFromFirestore(String documentId) {
        // Get reference to Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("News");

        // Delete the document from Firestore
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
     * Deletes a file from Firebase Storage based on the provided image URL.
     *
     * @param imageUrl The URL of the image to be deleted from Storage.
     */
    private void deleteFromStorage(String imageUrl) {
        // Get reference to Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);

        // Delete the file from Storage
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
     * Interface to notify listeners when an item is deleted.
     */
    public interface OnItemDeletedListener {
        void onItemDeleted(String key);
    }
}
