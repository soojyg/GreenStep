package com.example.greenstep;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;

public class UploadAlternatives extends Fragment {
    private EditText inputTitle, inputTip1, inputTip2;
    private ImageButton uploadImg;
    private Button submitBtn;
    BottomNavigationView bottomNavigationView;
    private ImageView close;
    private ProgressBar progressBar;
    private Uri imageUri;
    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
    NavController navController;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @NonNull Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.edu_alternatives_upload, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        close = view.findViewById(R.id.close);
        inputTitle = view.findViewById(R.id.editTitle);
        inputTip1 = view.findViewById(R.id.editTip1);
        inputTip2 = view.findViewById(R.id.editTip2);
        uploadImg = view.findViewById(R.id.uploadImage);
        submitBtn = view.findViewById(R.id.btnComplete);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        bottomNavigationView = getActivity().findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setVisibility(View.GONE);

        // In the gallery page
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null && data.getData() != null) {
                                imageUri = data.getData();
                                uploadImg.setImageURI(imageUri);
                            }
                        } else {
                            Toast.makeText(requireContext(), "No image selected.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            boolean title, tip, img;
            @Override
            public void onClick(View view) {
                String titleText = inputTitle.getText().toString().trim();
                String tip1Text = inputTip1.getText().toString().trim();
                String tip2Text = inputTip2.getText().toString().trim();

                if(!titleText.isEmpty()){
                    title = true;
                } else{
                    Toast.makeText(requireContext(), "Please enter title.", Toast.LENGTH_SHORT).show();
                }
                if(!tip1Text.isEmpty() || !tip2Text.isEmpty()){
                    tip = true;
                } else{
                    Toast.makeText(requireContext(), "Please enter at least one tip.", Toast.LENGTH_SHORT).show();
                }
                if (imageUri != null) {
                    img = true;

                } else {
                    Toast.makeText(requireContext(), "Please select image.", Toast.LENGTH_SHORT).show();
                }
                if(title && tip && img){
                    uploadToFirebase(imageUri);
                }
            }
        });
        close.setOnClickListener(v ->{

            navController.navigate(R.id.navigate_to_eduAlternativesContent);
            bottomNavigationView.setVisibility(View.VISIBLE);

        });

    }

    // Upload image to Storage AND upload details to Firestore DB
    private void uploadToFirebase(Uri uri) {
        StorageReference imageReference = storageReference.child("eduAlternativesPictures").child(String.valueOf(System.currentTimeMillis()));
        imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String title = inputTitle.getText().toString();
                String tip1 = inputTip1.getText().toString();
                String tip2 = inputTip2.getText().toString();

                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    // Create a new document in "Edu Content Tips" collection
                    Map<String, Object> data = new HashMap<>();
                    data.put("Title", title);
                    data.put("Tip 1",tip1);
                    data.put("Tip 2",tip2);
                    data.put("ImageUrl", imageUrl);
                    firestoreDB.collection("Edu Content Tips")
                            .add(data)
                            .addOnSuccessListener(documentReference -> {
                                progressBar.setVisibility(View.VISIBLE);
                                Toast.makeText(requireContext(), "Uploaded", Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(requireContext(), MainActivity.class);
//                                startActivity(intent);
                                navController.navigate(R.id.navigate_to_eduAlternativesContent);
                                bottomNavigationView.setVisibility(View.VISIBLE);

//                                finish();
                            }).addOnFailureListener(e -> {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(requireContext(), "Failed to upload", Toast.LENGTH_SHORT).show();
                            });
                }).addOnFailureListener(exception -> {
                    Log.e("FirebaseStorage","Error getting download URL",exception);
                });

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
