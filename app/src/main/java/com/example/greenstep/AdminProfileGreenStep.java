package com.example.greenstep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AdminProfileGreenStep extends AppCompatActivity {
    TextView field_name, field_date_birth, field_gender, field_contact, field_country, field_username;
    String nameUser, usernameUser, genderUser, contactNoUser, countryUser, dobUser;
    Button editProfile, logout;
    ImageView profilePic;
    FirebaseUser user;
    DatabaseReference reference;
    String userID;
    ImageView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile_green_step);
        // Initialization of UI elements
        backBtn = findViewById(R.id.backButton);
        logout=(Button) findViewById(R.id.button_logout);
        editProfile=(Button) findViewById(R.id.button_edit);
        profilePic=findViewById(R.id.profile_pic);
        field_name = findViewById(R.id.field_name);
        field_username = findViewById(R.id.field_username);
        field_date_birth = findViewById(R.id.field_date_birth);
        field_gender = findViewById(R.id.field_gender);
        field_contact = findViewById(R.id.field_contact);
        field_country = findViewById(R.id.field_country);
        field_username = findViewById(R.id.field_username);

        // Firebase initialization
        user=FirebaseAuth.getInstance().getCurrentUser();
        userID=user.getUid();
        reference= FirebaseDatabase.getInstance().getReference("User Info");

        // Fetch user details from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("User Info").document(userID);

        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        // Handle successful document retrieval

                        if (snapshot.exists()) {
                            ReadWriteUserDetails userProfile = snapshot.toObject(ReadWriteUserDetails.class);

                            nameUser = snapshot.getString("name");
                            usernameUser = snapshot.getString("username");
                            genderUser = snapshot.getString("gender");
                            contactNoUser = snapshot.getString("contactNo");
                            countryUser = snapshot.getString("country");
                            dobUser = snapshot.getString("dob");

                            if (userProfile != null) {
                                setTextIfNotNull(field_name, nameUser);
                                setTextIfNotNull(field_username, usernameUser);
                                setTextIfNotNull(field_date_birth, dobUser);
                                setTextIfNotNull(field_gender, genderUser);
                                setTextIfNotNull(field_contact, contactNoUser);
                                setTextIfNotNull(field_country, countryUser);
                                setTextIfNotNull(field_username, usernameUser);
                            }
                        }
                    }

                    private void setTextIfNotNull(TextView textView, String text) {
                        // Helper method to set text if not null

                        if (text != null) {
                            textView.setText(text);
                        }
                    }
                });

        backBtn.setOnClickListener(v ->{
            Intent intent = new Intent(AdminProfileGreenStep.this, MainActivity.class);
            startActivity(intent);
        });

        // Logout button functionality
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(AdminProfileGreenStep.this, MainActivity.class));
            }
        });

        // Edit profile button functionality
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminProfileGreenStep.this, EditProfileAdmin.class);
                startActivity(intent);
            }
        });

        // Load and display the user's profile picture
        loadImage();
    }


    /**
     * Loads and displays the admin's profile picture from Firebase Storage using Glide.
     * Retrieves the latest image based on the timestamp in the filename.
     */
    private void loadImage() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("profilePictures")
                .child("admin")
                .child(userID)
                .child("images");

        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                if (!listResult.getItems().isEmpty()) {
                    // Find the latest image based on timestamp in the filename
                    StorageReference latestImageRef = findLatestImage(listResult.getItems());

                    if (latestImageRef != null) {
                        latestImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                Log.d("ImageUrl", "Image URL: " + imageUrl);

                                // Display the image URL in a Toast
                                Toast.makeText(AdminProfileGreenStep.this, "Image URL: " + imageUrl, Toast.LENGTH_SHORT).show();

                                Glide.with(getApplicationContext())
                                        .load(imageUrl)
                                        .placeholder(R.drawable.akwhitelogo)
                                        .error(R.drawable.error)
                                        .transform(new RoundedCornersTransformation(60)) // Adjust the radius as needed
                                        .into(profilePic);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle failure to get the image URL
                                Log.e("ImageUrl", "Failed to get image URL", exception);
                            }
                        });
                    } else {
                        Log.e("ImageUrl", "No images found in the 'images' folder");
                        // Handle the case where no images are found
                    }
                } else {
                    Log.e("ImageUrl", "No images found in the 'images' folder");
                    // Handle the case where no images are found
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("ImageUrl", "Failed to list items in the 'images' folder", exception);
                // Handle failure to list items (files) in the "images" folder
            }
        });
    }

    /**
     * Finds and returns the latest image from a list of StorageReference items based on their names.
     * Assumes that the names contain timestamps.
     */
    private StorageReference findLatestImage(List<StorageReference> items) {
        // Check if there is only one item, return it directly
        if (items.size() == 1) {
            return items.get(0);
        }

        // Sort the items based on their names (assuming names contain timestamps)
        Collections.sort(items, new Comparator<StorageReference>() {
            @Override
            public int compare(StorageReference o1, StorageReference o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        // Get the latest image (last item after sorting)
        return items.get(items.size() - 1);
    }
}




