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
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.nullness.qual.Nullable;


public class EventUpload extends Fragment {
    ImageView uploadImage, cancel;
    Button btnComplete;
    EditText uploadSource, uploadEventName, uploadCompanyOrgani;
    String imageURL,oldImageURL;
    Uri uri;
    String eventId;
    BottomNavigationView bottomNavigationView;

    @Override
    public View onCreateView(@org.checkerframework.checker.nullness.qual.NonNull LayoutInflater inflater, @Nullable ViewGroup container, @org.checkerframework.checker.nullness.qual.NonNull Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.event_upload,container, false);
        return rootView;

    }

    @Override
    public void onViewCreated(@org.checkerframework.checker.nullness.qual.NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseApp.initializeApp(requireContext());
        uploadImage = view.findViewById(R.id.uploadEventImage);
        uploadCompanyOrgani = view.findViewById(R.id.uploadCompanyOrgani);
        uploadEventName = view.findViewById(R.id.uploadEventName);
        uploadSource = view.findViewById(R.id.uploadSourceRef);
        btnComplete = view.findViewById(R.id.btnComplete);
        cancel = view.findViewById(R.id.close);
        bottomNavigationView = getActivity().findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setVisibility(View.GONE);
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
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(view);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Upload incomplete", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(EventUpload.this, EventAdminView.class);
//                startActivity(intent);
                Navigation.findNavController(view).popBackStack();
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });


    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.event_upload);
//
//        FirebaseApp.initializeApp(this);
//        uploadImage = findViewById(R.id.uploadEventImage);
//        uploadCompanyOrgani = findViewById(R.id.uploadCompanyOrgani);
//        uploadEventName = findViewById(R.id.uploadEventName);
//        uploadSource = findViewById(R.id.uploadSourceRef);
//        btnComplete = findViewById(R.id.btnComplete);
//        cancel = findViewById(R.id.cancel);
//        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                new ActivityResultCallback<ActivityResult>() {
//                    @Override
//                    public void onActivityResult(ActivityResult result) {
//                        if (result.getResultCode() == Activity.RESULT_OK){
//                            Intent data = result.getData();
//                            uri = data.getData();
//                            oldImageURL = String.valueOf(uri);
//                            Log.d("URI_DEBUG", "Selected URI: " + uri.toString());
//                            uploadImage.setImageURI(uri);
//                        } else {
//                            Toast.makeText(EventUpload.this, "No Image Selected", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//        );
//        uploadImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent photoPicker = new Intent(Intent.ACTION_PICK);
//                photoPicker.setType("image/*");
//                activityResultLauncher.launch(photoPicker);
//            }
//        });
//        btnComplete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                saveData();
//            }
//        });
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(EventUpload.this, "Upload incomplete", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(EventUpload.this, EventAdminView.class);
//                startActivity(intent);
//            }
//        });
//
//
//    }
    public void saveData(View view){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("eventPictures").child("eventPic")
                .child(uri.getLastPathSegment());
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();
        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageURL = urlImage.toString();
                uploadData(view);

                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }


    public void uploadData(View view){
        String eventName = uploadEventName.getText().toString();
        String companyOrgani = uploadCompanyOrgani.getText().toString();
        String source = uploadSource.getText().toString();
        DataClass dataClass = new DataClass(eventName, companyOrgani, source, imageURL);

        // Get a reference to the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

// Create a new document reference with an automatically generated ID
        DocumentReference eventRef = db.collection("Events").document();

        eventId = eventRef.getId();

// Set the data to the Firestore document
        eventRef.set(dataClass)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show();
//                            finish();
//
//                            startActivity(new Intent(EventUpload.this, EventAdminView.class));
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