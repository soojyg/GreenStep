package com.example.greenstep;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.nullness.qual.Nullable;

public class NewsUpload extends Fragment {

    ImageView uploadImage,cancel;
    Button btnComplete;
    EditText uploadSource, uploadEventName, uploadAuthor, uploadDate;
    String imageURL,oldImageURL;
    Uri uri;
    String newsId;
    BottomNavigationView bottomNavigationView;

    @Override
    public View onCreateView(@org.checkerframework.checker.nullness.qual.NonNull LayoutInflater inflater, @Nullable ViewGroup container, @org.checkerframework.checker.nullness.qual.NonNull Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.news_upload,container, false);
        return rootView;

    }

        @Override
    public void onViewCreated(@org.checkerframework.checker.nullness.qual.NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        uploadImage = view.findViewById(R.id.uploadNewsImage);
        uploadAuthor = view.findViewById(R.id.uploadAuthor);
        uploadEventName = view.findViewById(R.id.uploadNewsName);
        uploadSource = view.findViewById(R.id.uploadNewsSourceRef);
        uploadDate = view.findViewById(R.id.uploadDate);
        btnComplete = view.findViewById(R.id.btnComplete);
        cancel=view.findViewById(R.id.close);
        bottomNavigationView = getActivity().findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setVisibility(View.GONE);

        // Set up ActivityResultLauncher for image selection
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            oldImageURL = String.valueOf(uri);
                            Log.d("URI_DEBUG", "Selected URI: " + uri.toString());
                            uploadImage.setImageURI(uri);
                        } else {
                            Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Set click listener for image upload button
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        // Set click listener for completion button
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(view);
            }
        });

        // Set click listener for cancel button
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display a message indicating an incomplete update
                Toast.makeText(requireContext(), "Update incomplete", Toast.LENGTH_SHORT).show();

                // Navigate back and make the bottom navigation view visible
                Navigation.findNavController(view).popBackStack();
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });


    }

    /**
     * Saves data to Firestore. Uploads the selected image and triggers data upload.
     *
     * @param view The view associated with the action.
     */
    public void saveData(View view){
        // Set up StorageReference for image upload
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("newsPictures").child("newsPic")
                .child(uri.getLastPathSegment());

        // Create a progress dialog for the image upload process
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Upload the image to Firebase Storage
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get the download URL of the uploaded image
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                // Update imageURL with the download URL
                imageURL = urlImage.toString();
                // Trigger data upload to Firestore
                uploadData(view);
                // Dismiss the progress dialog
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }

    /**
     * Uploads data to Firestore after the image has been successfully uploaded.
     *
     * @param view The view associated with the action.
     */
    public void uploadData(View view){
        String newsName = uploadEventName.getText().toString();
        String author = uploadAuthor.getText().toString();
        String source = uploadSource.getText().toString();
        String date = uploadDate.getText().toString();
        // Create a NewsDataClass object with the retrieved data
        NewsDataClass newsDataClass = new NewsDataClass(newsName, author, source, imageURL,date);
        // Get a reference to the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new document reference with an automatically generated ID
        DocumentReference newsRef = db.collection("News").document();

        // Store the generated ID for potential further use
        newsId = newsRef.getId();

        // Set the data to the Firestore document
        newsRef.set(newsDataClass)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            // Display a success message and navigate back
                            Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show();
                            Navigation.findNavController(view).popBackStack();
                            bottomNavigationView.setVisibility(View.VISIBLE);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
