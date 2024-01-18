package com.example.greenstep;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class ProfileGreenStep extends AppCompatActivity {
    ProgressBar progressBar;

    TextView field_name, field_date_birth, field_gender, field_contact, field_country, field_username,leafCollectedProfile, treePlantedProfile, percentage;
    String nameUser, usernameUser, genderUser, contactNoUser, countryUser, dobUser;
    Button editProfile, logout;
    ImageView profilePic;
    ImageView backBtn;
    DatabaseReference reference;
    String userID;
    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    String uid=user.getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference = db.collection("User Info").document(uid);
    DocumentReference docRefTotal = db.collection("Statistics").document("globalStat");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);
        backBtn = findViewById(R.id.backButton);
        logout=(Button) findViewById(R.id.button_logout);

        backBtn.setOnClickListener(v ->{
//            onBackPressed();
            Intent intent = new Intent(ProfileGreenStep.this, MainActivity.class);
            startActivity(intent);
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileGreenStep.this, MainActivity.class));
            }
        });

        editProfile=(Button) findViewById(R.id.button_edit);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileGreenStep.this, EditProfileUser.class);
                startActivity(intent);
            }
        });

        user=FirebaseAuth.getInstance().getCurrentUser();
        userID=user.getUid();


        profilePic=findViewById(R.id.profile_pic);
        progressBar=findViewById(R.id.progressBar2);
        field_name = findViewById(R.id.field_name);
        field_username = findViewById(R.id.field_username);
        field_date_birth = findViewById(R.id.field_date_birth);
        field_gender = findViewById(R.id.field_gender);
        field_contact = findViewById(R.id.field_contact);
        field_country = findViewById(R.id.field_country);
        field_username = findViewById(R.id.field_username);
        leafCollectedProfile= findViewById(R.id.leafCollectedProfile);
        treePlantedProfile=findViewById(R.id.treePlantedProfile);
        percentage=findViewById(R.id.percentage);
        reference=FirebaseDatabase.getInstance().getReference("User Info");



        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("User Info").document(userID);

        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
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

                                // Special handling for title views
                                setTextIfNotNull(field_username, usernameUser);
                            }
                        }
                    }

                    private void setTextIfNotNull(TextView textView, String text) {
                        if (text != null) {
                            textView.setText(text);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure if needed
                    }
                });

        // Call this method to load the image from the database and display it
        loadImage();
        displayPointCollected();
        displayCurrentTreesPlanted();

    }

    /**
     * Fetches the current number of trees planted from Firestore and updates the corresponding TextView.
     */
    private void displayCurrentTreesPlanted() {
        // Retrieve the current number of trees planted and update the TextView
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Long currentTreesPlanted = documentSnapshot.getLong("trees_planted");
                            if (currentTreesPlanted == null) {
                                currentTreesPlanted = 0L;
                            }

                            // Log the current count before updating the TextView
                            Log.d(TAG, "Current Trees Planted: " + currentTreesPlanted);

                            // Update the TextView with the current count
                            try {
                                treePlantedProfile.setText(String.valueOf(currentTreesPlanted));
                            } catch (Exception e) {
                                // Log any exception that might occur during conversion or setting text
                                Log.e(TAG, "Error updating TextView", e);
                            }
                        }
                    }
                });
    }
    
    /**
     * Fetches the current number of points collected from Firestore, updates the corresponding TextView,
     * and calculates the percentage of completion.
     */
    private void displayPointCollected() {
        // Retrieve the current number of trees planted and update the TextView
        documentReference.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Long currentPointCollected = documentSnapshot.getLong("pointCollected");
                            if (currentPointCollected == null) {
                                currentPointCollected = 0L;
                            }
                            updateProgressBar(currentPointCollected, 10000);
                            int percentageValue = (int) ((currentPointCollected / 10000.0) * 100); // Corrected formula
                            if(percentageValue<=100) {
                                percentage.setText(percentageValue + "%");
                            }else{
                                percentage.setText("100%");

                            }
                            // Log the current count before updating the TextView
                            Log.d(TAG, "Point collected: " + currentPointCollected);

                            // Update the TextView with the current count
                            try {
                                leafCollectedProfile.setText(String.valueOf(currentPointCollected));
                            } catch (Exception e) {
                                // Log any exception that might occur during conversion or setting text
                                Log.e(TAG, "Error updating TextView", e);
                            }
                        }
                    }
                });
    }


    /**
     * Loads the user's profile image from Firebase Storage and displays it using Glide.
     * Retrieves the latest image based on the timestamp in the filename.
     */
    private void loadImage() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("profilePictures")
                .child("user")
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
                                Toast.makeText(ProfileGreenStep.this, "Image URL: " + imageUrl, Toast.LENGTH_SHORT).show();

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
                                Log.e("ImageUrl", "Failed to get image URL", exception);
                                // Handle failure to get the image URL
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

    /**
     * Updates the progress of a ProgressBar based on the current and total points.
     * Ensures the progress value is between 0 and 100.
     */
    private void updateProgressBar(long currentPoints, long totalPoints) {
        // Calculate the progress percentage
        int progress = (int) ((currentPoints * 100) / totalPoints);

        // Ensure progress is between 0 and 100
        progress = Math.min(100, Math.max(0, progress));

        // Set the progress to the ProgressBar
        progressBar.setProgress(progress);
    }

}





