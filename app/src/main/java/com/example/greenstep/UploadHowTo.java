package com.example.greenstep;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class UploadHowTo extends AppCompatActivity {
    EditText inputTitle,inputSourceRef;
    private ImageButton uploadImage;
    private Button submitBtn;
    ProgressBar progressBar;
    private Uri imageUri;
    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edu_howto_upload);
        inputTitle = findViewById(R.id.editTitle);
        inputSourceRef = findViewById(R.id.editSourceRef);
        uploadImage = findViewById(R.id.uploadImage);
        submitBtn = findViewById(R.id.submitEduBtn);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);


        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null && data.getData() != null) {
                                imageUri = data.getData();
                                uploadImage.setImageURI(imageUri);
                            }
                        } else {
                            Toast.makeText(UploadHowTo.this, "No image selected.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUri != null) {
                    uploadToFirebase(imageUri);
                } else {
                    Toast.makeText(UploadHowTo.this, "Please select image", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        CropImage.activity()
//                .setAspectRatio(1,1)
//                .start(UploadHowTo.this);

    }

    private void uploadToFirebase(Uri uri) {
        StorageReference imageReference = storageReference.child("eduContentPictures").child(String.valueOf(System.currentTimeMillis()));
        imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String sourceRef = inputSourceRef.getText().toString();
                String title = inputTitle.getText().toString();
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    // Create a new document in "Edu Content Posts" collection
                    Map<String, Object> data = new HashMap<>();
                    data.put("Title", title);
                    data.put("Source/ Reference",sourceRef);
                    data.put("ImageUrl", imageUrl);
                    firestoreDB.collection("Edu Content Posts")
                            .add(data)
                            .addOnSuccessListener(documentReference -> {
                                progressBar.setVisibility(View.VISIBLE);
                                Toast.makeText(UploadHowTo.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(UploadHowTo.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }).addOnFailureListener(e -> {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(UploadHowTo.this, "Failed to upload", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(UploadHowTo.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private String getFileExtension(Uri fileUri){
//        ContentResolver contentResolver = getContentResolver();
//        MimeTypeMap mime = MimeTypeMap.getSingleton();
//        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
//    }
}
