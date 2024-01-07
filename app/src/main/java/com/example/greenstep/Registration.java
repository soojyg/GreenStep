package com.example.greenstep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class Registration extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextName;
    Button buttonSignUp, buttonLogin;
    Spinner spinnerUserType;
    FirebaseAuth mAuth;

    // To check if the user is already logged in
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);
        mAuth = FirebaseAuth.getInstance();
        editTextName = findViewById(R.id.editName);
        editTextEmail = findViewById(R.id.editEmail);
        editTextPassword = findViewById(R.id.editPassword);
        buttonSignUp = findViewById(R.id.button_signup);
        buttonLogin = findViewById(R.id.button_login);
        spinnerUserType = findViewById(R.id.spinnerUserType);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.user_types,
                android.R.layout.simple_spinner_item
        );

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinnerUserType.setAdapter(adapter);

        buttonLogin.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        buttonSignUp.setOnClickListener(v -> {
            String name, email, password;
            name = String.valueOf(editTextName.getText());
            email = String.valueOf(editTextEmail.getText());
            password = String.valueOf(editTextPassword.getText());

            // to check if the email and password is empty or not
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(Registration.this, "Enter name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(Registration.this, "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(Registration.this, "Enter password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check password strength
            if (!isStrongPassword(password)) {
                // Display an error message
                editTextPassword.setError("Weak password. Follow the criteria below.");
                return;
            }

            // To determine the user type
            String userType = spinnerUserType.getSelectedItem().toString();

            // Check the selected user type
            if ("Admin".equals(userType)) {
                // If the user selected "Admin", check if the email is in the Firestore database
                isAdminEmail(email).addOnCompleteListener(new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            boolean isEmailInAdminList = task.getResult();
                            if (isEmailInAdminList) {
                                // If the email is in the admin list, create an admin account
                                createAdminAccount(email, password,name);
                            } else {
                                // If the email is not in the admin list, display an error message
                                Toast.makeText(Registration.this, "You are not authorized to register as an admin.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Handle errors
                            Toast.makeText(Registration.this, "Error checking admin email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                // If the user selected "Normal User", create a normal user account
                createNormalUserAccount(email, password,name);
            }
        });
    }

    // Function to check password strength
    private boolean isStrongPassword(String password) {
        // Criteria for a strong password: Minimum 8 characters, including uppercase, lowercase, a special character, and a number
        return password.length() >= 8 &&
                containsUppercase(password) &&
                containsLowercase(password) &&
                containsSpecialCharacter(password) &&
                containsNumber(password);
    }

    // Helper functions to check specific criteria
    private boolean containsUppercase(String password) {
        return !password.equals(password.toLowerCase());
    }

    private boolean containsLowercase(String password) {
        return !password.equals(password.toUpperCase());
    }

    private boolean containsNumber(String password) {
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsSpecialCharacter(String password) {
        for (char c : password.toCharArray()) {
            if (!Character.isDigit(c) && !Character.isLetter(c) && !Character.isWhitespace(c)) {
                return true;
            }
        }
        return false;
    }

    // Function to check if the email is in the admin list in Firestore database
    private Task<Boolean> isAdminEmail(String email) {
        FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
        CollectionReference adminsCollection = firestoreDB.collection("Admin");

        return adminsCollection
                .whereEqualTo("Email address", email)
                .get()
                .continueWith(task -> {
                    try {
                        if (task.isSuccessful()) {
                            // Check if there is any document with the given email
                            for (DocumentSnapshot document : task.getResult()) {
                                // If a document is found, the email is in the admin list
                                return true;
                            }
                        }
                        // If no document is found, the email is not in the admin list
                        return false;
                    } catch (Exception e) {
                        // Handle the exception (e.g., log it or throw a runtime exception)
                        e.printStackTrace();
                        return false;
                    }
                });
    }
    private void createAdminAccount(String email, String password, String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            ReadWriteUserDetails writeUserDetails =new ReadWriteUserDetails();
                            writeUserDetails.setName(name);
                            writeUserDetails.setPointsCollected(0);

                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            // Create a new user document in the "users" collection
                            DocumentReference userRef = db.collection("User Info").document(mAuth.getUid());
                            userRef.set(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(Registration.this, "Account created.",
                                            Toast.LENGTH_SHORT).show();                                        }
                            });
                            Toast.makeText(Registration.this, "Admin account created.", Toast.LENGTH_SHORT).show();
                            // Save the user information to the "Users" collection
                            String userId = mAuth.getCurrentUser().getUid();
                            saveToFirestoreDB(userId, email,"Admin");
                        } else {
                            // Handle authentication failure
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(Registration.this, "Email is already in use.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Registration.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    // Function to create a normal user account
    private void createNormalUserAccount(String email, String password,String name) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            ReadWriteUserDetails writeUserDetails =new ReadWriteUserDetails();
                            writeUserDetails.setName(name);
                            writeUserDetails.setPointsCollected(0);

                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            // Create a new user document in the "users" collection
                            DocumentReference userRef = db.collection("User Info").document(mAuth.getUid());
                            userRef.set(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(Registration.this, "Account created.",
                                            Toast.LENGTH_SHORT).show();                                        }
                            });
                            Toast.makeText(Registration.this, "Normal user account created.", Toast.LENGTH_SHORT).show();
                            // Save the user information to the "Users" collection
                            String userId = mAuth.getCurrentUser().getUid();
                            saveToFirestoreDB(userId, email,"Normal User");

                        } else {
                            // Handle authentication failure
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(Registration.this, "Email is already in use.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Registration.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
//    // Function to create an Admin account
//    private void createAdminAccount(String email, String password) {
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(Registration.this, "Admin account created.", Toast.LENGTH_SHORT).show();
//                            // Save the user information to the "Users" collection
//                            String userId = mAuth.getCurrentUser().getUid();
//                            saveToFirestoreDB(userId, email,"Admin");
//                        } else {
//                            // Handle authentication failure
//                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
//                                Toast.makeText(Registration.this, "Email is already in use.", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(Registration.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                });
//    }
//
//    // Function to create a normal user account
//    private void createNormalUserAccount(String email, String password) {
//        mAuth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(Registration.this, "Normal user account created.", Toast.LENGTH_SHORT).show();
//                            // Save the user information to the "Users" collection
//                            String userId = mAuth.getCurrentUser().getUid();
//                            saveToFirestoreDB(userId, email,"Normal User");
//                        } else {
//                            // Handle authentication failure
//                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
//                                Toast.makeText(Registration.this, "Email is already in use.", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(Registration.this, "Authentication failed", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                });
//    }

    // Function to save the user info to firestore database
    private void saveToFirestoreDB(String userId, String email, String userType){
        FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = firestoreDB.collection("Users");

        Map<String, Object> data = new HashMap<>();
        data.put("UserId",userId);
        data.put("Email",email);
        data.put("User type",userType);

        usersCollection.document(userId)
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Registration.this, "User information saved to Firestore database.",Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->{
                    Toast.makeText(Registration.this,"Error saving user information to Firestore.",Toast.LENGTH_SHORT).show();
                });
    }
}
