package com.example.greenstep;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
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
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.Manifest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class EditProfileAdmin extends AppCompatActivity {
    public Uri imageUri;
    private Uri selectedImageUri;
    private String originalImageUrl;
    private ImageView profilePic;
    EditText editName, editUsername, editGender, editContactNo, editCountry, editDOB;
    FirebaseAuth mAuth;
    String nameUser, usernameUser, genderUser, contactNoUser, countryUser, dobUser,imageUriDB;
    TextView resetPassword, textButton_save,textButton_cancel;
    DatabaseReference reference;
    FirebaseStorage storage;
    StorageReference storageRef;
    FloatingActionButton fab;
    FirebaseUser user;
    String userID;
    ReadWriteUserDetails writeUserDetails =new ReadWriteUserDetails();
    GoogleSignInOptions gOptions;
    GoogleSignInClient gClient;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference userRef;

    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private String currentPhotoPath;

                                                                                                                                 
                                                                                                  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_admin);

        // Initialize Firebase components and views
        initializeFirebase();
        initializeViews();
        showData();

        FirebaseApp.initializeApp(EditProfileAdmin.this);

        // Set click listeners for cancel and save buttons
        textButton_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileAdmin.this, AdminProfileGreenStep.class);
                startActivity(intent);
            }
        });

        textButton_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(imageUri);
                if (isAnyFieldChanged()||!imageUri.toString().equals(originalImageUrl)) {
                    Toast.makeText(EditProfileAdmin.this, "Saved", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(EditProfileAdmin.this, "No Changes Found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Load image from storage and set click listener for selecting a profile picture
        loadImage();
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });

        // Set click listeners for reset buttons
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileAdmin.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
                EditText emailBox = dialogView.findViewById(R.id.emailBox);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userEmail = emailBox.getText().toString();
                        if (TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                            Toast.makeText(EditProfileAdmin.this, "Enter your registered email id", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(EditProfileAdmin.this, "Check your email", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(EditProfileAdmin.this, "Unable to send, failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                if (dialog.getWindow() != null){
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                dialog.show();
            }
        });

        // Initialize Google Sign-In components
        gOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gClient = GoogleSignIn.getClient(this, gOptions);
        GoogleSignInAccount gAccount = GoogleSignIn.getLastSignedInAccount(this);

        // Check if user is already signed in with Google account
        if (gAccount != null){
            finish();
            Intent intent = new Intent(EditProfileAdmin.this, MainActivity.class);
            startActivity(intent);
        }


        // Set up activity result launcher for Google Sign-In
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                            try {
                                task.getResult(ApiException.class);
                                finish();
                                Intent intent = new Intent(EditProfileAdmin.this, AdminProfileGreenStep.class);
                                startActivity(intent);
                            } catch (ApiException e){
                                Toast.makeText(EditProfileAdmin.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        // Set up camera button click listener
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCameraPermission();
            }
        });


    }

    /**
     * Initializes Firebase components, such as FirebaseAuth, FirebaseDatabase, and FirebaseStorage.
     * Also initializes references to the current user and the database.
     */
    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User Info");
        userID = user.getUid();
        userRef = db.collection("User Info").document(userID);

        // Initialize FirebaseStorage and get the root reference
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
    }

    /**
     * Initializes views by finding and assigning references to UI elements.
     * This method is called in the onCreate method to set up the initial state of the UI.
     */
    private void initializeViews() {
        profilePic = findViewById(R.id.profile_pic);
        editName = findViewById(R.id.editText_name);
        editUsername = findViewById(R.id.editText_username);
        editGender = findViewById(R.id.editText_gender);
        editDOB = findViewById(R.id.editText_dateOfBirth);
        editContactNo = findViewById(R.id.editText_contact);
        editCountry = findViewById(R.id.editText_country);
        resetPassword=findViewById(R.id.editText_password);
        textButton_save = findViewById(R.id.textButton_save);
        textButton_cancel=findViewById(R.id.textButton_cancel);
        fab = findViewById(R.id.floatingActionButton);

    }

    /**
     * Uploads the selected image to Firebase Storage and updates the database with the image URL.
     *
     * @param image Uri of the selected image
     */
    private void uploadImage(Uri image) {
        if (image != null) {
            // Generate a unique filename for each image
            String imageName = "profilePicture_" + System.currentTimeMillis() + ".jpg";

            // Create a StorageReference for the image in Firebase Storage
            StorageReference reference = storageRef.child("profilePictures").child("admin").
                    child(userID).child("images/" + imageName);
            // Show a progress dialog while the image is being uploaded
            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileAdmin.this);
            builder.setCancelable(false);
            builder.setView(R.layout.progress_layout);
            AlertDialog dialog = builder.create();
            dialog.show();

            // Upload the image file to Firebase Storage
            reference.putFile(image).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Update database with the image URL
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Convert Uri to a string representing the image URL
                            String imageUrl = uri.toString();
                            // Update the database with the image URL
                            updateDatabaseWithImageUrl(imageUrl);
                        }
                    });

                    // Display a success message
                    Toast.makeText(EditProfileAdmin.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileAdmin.this, "Something went wrong, failed to upload", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // If no new image is selected, use the original image URL
            String imageUrl = originalImageUrl;
            // Update the database with the image URL
            updateDatabaseWithImageUrl(imageUrl);
        }

    }


    /**
     * Updates the Firebase Firestore database with the provided image URL.
     *
     * @param imageUrl The URL of the image to be stored in the database
     */
    private void updateDatabaseWithImageUrl(String imageUrl) {
        if (imageUrl != null) {
            // a database reference pointing to the user's profile node
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("User Info").document(userID);

            // Update the profileImageUrl field in the database
            userRef.update("profileImageUrl", imageUrl)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // After successful database update, trigger other changes and navigate to the profile screen
                            updateAllChanges();
                            Intent intent = new Intent(EditProfileAdmin.this, AdminProfileGreenStep.class);
                            startActivity(intent);
                            // Display a success message
                            Toast.makeText(EditProfileAdmin.this, "Database updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EditProfileAdmin.this, "Failed to update database", Toast.LENGTH_SHORT).show();
                        }
                    });

        }

    }
    /**
     * Checks if any user profile field has been changed.
     *
     * @return true if any field has changed, false otherwise
     */
    private boolean isAnyFieldChanged() {
        return isNameChanged() || isUsernameChanged() || isGenderChanged() || isContactNoChanged() || isCountryChanged() || isdobChanged();
    }

    /**
     * Updates all changes in user profile fields in the Firebase Firestore database.
     */
    private void updateAllChanges() {


        if (isNameChanged()) {
            userRef.update("name", editName.getText().toString());
            nameUser = editName.getText().toString();
        }

        if (isUsernameChanged()) {
            userRef.update("username", editUsername.getText().toString());
            usernameUser = editUsername.getText().toString();

        }

        if (isGenderChanged()) {
            userRef.update("gender", editGender.getText().toString());
            genderUser = editGender.getText().toString();
        }

        if (isdobChanged()) {
            userRef.update("dob", editDOB.getText().toString());
            dobUser = editDOB.getText().toString();
        }

        if (isContactNoChanged()) {
            userRef.update("contactNo", editContactNo.getText().toString());
            contactNoUser = editContactNo.getText().toString();
        }

        if (isCountryChanged()) {
            userRef.update("country", editCountry.getText().toString());
            countryUser = editCountry.getText().toString();
        }

    }

    /**
     * Checks if the user's name has changed and updates it in the database if needed.
     *
     * @return true if the name has changed, false otherwise
     */
    private boolean isNameChanged() {
        if (!nameUser.equals(editName.getText().toString())){
            userRef.update("name", editName.getText().toString());
            nameUser = editName.getText().toString();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the user's gender has changed and updates it in the database if needed.
     *
     * @return true if the gender has changed, false otherwise
     */
    private boolean isGenderChanged(){
        String newGender = editGender.getText().toString();
        if (genderUser == null) {
            // Assuming writeUserDetails has a setCountry method
            writeUserDetails.setGender(newGender);

            // Extracting user reference from the database for "User Info"
            userRef.update("gender", editGender.getText().toString());


            // Update the local variable
            genderUser = newGender;

            return true;
        } else if (genderUser!=null && !genderUser.equals(editGender.getText().toString())) {
            userRef.update("gender", newGender);

            genderUser = newGender;
            return true;
        }else{
            return false;
        }
    }

    /**
     * Checks if the user's username has changed and updates it in the database if needed.
     *
     * @return true if the username has changed, false otherwise
     */
    private boolean isUsernameChanged(){
        String newUsername = editUsername.getText().toString();
        if (usernameUser == null) {
            // Assuming writeUserDetails has a setCountry method
            writeUserDetails.setUsername(newUsername);

            userRef.update("username", newUsername);

            // Update the local variable
            usernameUser = newUsername;

            return true;
        } else if (usernameUser!=null && !usernameUser.equals(editUsername.getText().toString())) {
            userRef.update("username", newUsername);
            usernameUser = newUsername;
            return true;
        }else{
            return false;
        }
    }

    /**
     * Checks if the user's day of birth has changed and updates it in the database if needed.
     *
     * @return true if the day of birth has changed, false otherwise
     */
    private boolean isdobChanged(){
        String newDOB = editDOB.getText().toString();
        if (dobUser == null) {
            // Assuming writeUserDetails has a setCountry method
            writeUserDetails.setDob(newDOB);

            userRef.update("dob", newDOB);

            // Update the local variable
            dobUser = newDOB;

            return true;
        } else if (dobUser!=null && !dobUser.equals(editDOB.getText().toString())) {
            userRef.update("dob", newDOB);
            dobUser = newDOB;
            return true;
        }else{
            return false;
        }
    }

    /**
     * Checks if the user's contact has changed and updates it in the database if needed.
     *
     * @return true if the contact has changed, false otherwise
     */
    private boolean isContactNoChanged() {
        String newContactNo = editContactNo.getText().toString();

        if (contactNoUser == null) {
            writeUserDetails.setContactNo(newContactNo);

            userRef.update("contactNo", newContactNo);

            // Update the local variable
            contactNoUser = newContactNo;


            return true;
        } else if (contactNoUser!=null && !contactNoUser.equals(editContactNo.getText().toString())) {
            userRef.update("contactNo", editContactNo.getText().toString());
            contactNoUser = editContactNo.getText().toString();
            return true;
        }else{
            return false;
        }
    }

    /**
     * Checks if the user's country has changed and updates it in the database if needed.
     *
     * @return true if the country has changed, false otherwise
     */
    private boolean isCountryChanged() {
        String newCountry = editCountry.getText().toString();
        if (countryUser == null) {
            // Assuming writeUserDetails has a setCountry method
            writeUserDetails.setCountry(newCountry);

            userRef.update("country", newCountry);

            // Update the local variable
            countryUser = newCountry;

            return true;
        } else if (countryUser!=null && !countryUser.equals(editCountry.getText().toString())) {
            userRef.update("country", newCountry);
            countryUser = newCountry;
            return true;
        }else{
            return false;
        }
    }


    /**
     * Retrieves user data from Firestore and populates the UI elements with the user's details.
     * If user data exists, it updates the corresponding variables and sets text for non-null fields.
     * If no data is found or an error occurs, it handles the failure accordingly.
     */
    public void showData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("User Info").document(userID);

        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Convert the Firestore document to a custom object
                            ReadWriteUserDetails userProfile = snapshot.toObject(ReadWriteUserDetails.class);

                            // Update local variables with user details
                            nameUser = snapshot.getString("name");
                            usernameUser = snapshot.getString("username");
                            genderUser = snapshot.getString("gender");
                            contactNoUser = snapshot.getString("contactNo");
                            countryUser = snapshot.getString("country");
                            dobUser = snapshot.getString("dob");

                            // Set text for non-null fields in UI
                            setTextIfNotNull(editName, nameUser);
                            setTextIfNotNull(editUsername, usernameUser);
                            setTextIfNotNull(editDOB, dobUser);
                            setTextIfNotNull(editGender, genderUser);
                            setTextIfNotNull(editContactNo, contactNoUser);
                            setTextIfNotNull(editCountry, countryUser);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure if needed
                    }
                });
    }

    /**
     * Sets the text for a TextView if the given text is not null.
     * @param textView The TextView to set text on
     * @param text The text to set (if not null)
     */
    private void setTextIfNotNull(TextView textView, String text) {
        if (text != null) {
            textView.setText(text);
        }
    }

    /**
     * Launches an activity for result to select an image.
     * Handles the result and updates the profile picture using Glide library.
     */
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK){
                Intent data = result.getData();
                if(data != null && data.getData() != null){
                    imageUri = data.getData();

                    // Load the selected image into the profile picture ImageView using Glide
                    Glide.with(getApplicationContext()).load(imageUri).into(profilePic);
                } else {
                    Toast.makeText(EditProfileAdmin.this, "Image selection failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    });

    /**
     * Loads the latest profile image from Firebase Storage and displays it using Glide.
     * Handles success and failure cases, including logging errors.
     */
    private void loadImage() {
        // Reference to the images folder in Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("profilePictures")
                .child("admin")
                .child(userID)
                .child("images");

        // List all items (images) in the folder
        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                if (!listResult.getItems().isEmpty()) {
                    // Find the latest image reference
                    StorageReference latestImageRef = findLatestImage(listResult.getItems());
                    // Get the download URL of the latest image
                    latestImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Update the original image URL and load the image into ImageView using Glide
                            originalImageUrl = uri.toString();
                            Glide.with(getApplicationContext())
                                    .load(originalImageUrl)
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
     * Helper method to find the latest image reference from a list of StorageReferences.
     * Assumes that the list is not empty.
     * @param items List of StorageReferences representing images in Firebase Storage
     * @return The StorageReference of the latest image
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
     * Checks if the camera permission is granted. If granted, opens the camera; otherwise, requests permission.
     */
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Camera permission is granted, proceed to open the camera
            openCamera();
        } else {
            // Camera permission is not granted, request permission
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_IMAGE_CAPTURE
            );
        }
    }

    /**
     * Opens the device's camera to capture an image.
     * If the camera is not available, shows a toast message.
     */
    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create a file to save the captured image
            File photoFile = createImageFile();
            if (photoFile != null) {
                // Get a content URI for the file using FileProvider
                Uri photoUri = FileProvider.getUriForFile(
                        this,
                        "com.example.greenStep.fileprovider",
                        photoFile
                );

                // Set the output URI for the camera intent and start the camera
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else {
            Toast.makeText(this, "Camera is not available", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Creates a unique image file for storing the captured photo.
     * Uses a timestamp to generate a unique filename in the app's external pictures directory.
     * @return The created File object representing the image file.
     */
    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            // Create a temporary image file with a unique filename
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            currentPhotoPath = image.getAbsolutePath();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Handles the result of the camera intent.
     * If the image capture is successful, updates the imageUri field with the captured photo URI
     * and loads the photo into the profilePic ImageView using Glide.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Get the File object representing the captured photo
            File photoFile = new File(currentPhotoPath);

            // Use FileProvider to create a content URI
            Uri photoUri = FileProvider.getUriForFile(
                    this,
                    "com.example.greenStep.fileprovider",
                    photoFile
            );

            // Update the imageUri field with the captured photo URI
            imageUri = photoUri;

            Glide.with(getApplicationContext()).load(photoUri).transform(new RoundedCornersTransformation(60)).into(profilePic);
        }

    }
}                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
