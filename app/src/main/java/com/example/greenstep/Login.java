package com.example.greenstep;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    TextView forgotPW;
    Button buttonLogin, buttonSignUp;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.editEmail);
        editTextPassword = findViewById(R.id.editPassword);
        buttonLogin = findViewById(R.id.button_login);
        buttonSignUp = findViewById(R.id.button_signup);
        forgotPW = findViewById(R.id.forgotPW);
        buttonSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Registration.class);
            startActivity(intent);
            finish();
        });

        buttonLogin.setOnClickListener(this::onClick); //when the login button is clicked, it will call the onClick method of Login class

        forgotPW.setOnClickListener(view ->{
            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot_pw,null);
            // getLayoutInflater().inflate(...) -> used to convert the XML layout file into a View object.
            EditText emailBox = dialogView.findViewById(R.id.emailBox);

            // sets the previously inflated dialogView as the view for the AlertDialog.Builder.
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();

            dialogView.findViewById(R.id.button_reset).setOnClickListener(v -> {
                String userEmail = emailBox.getText().toString();

                if(TextUtils.isEmpty(userEmail) && !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()){
                    Toast.makeText(Login.this, "Enter your registered email", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(Login.this,"Check your email",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }else{
                        Toast.makeText(Login.this,"Failed, unable to send.",Toast.LENGTH_SHORT).show();
                    }
                });
            });
            dialogView.findViewById(R.id.button_cancel).setOnClickListener(v -> dialog.dismiss());
            if(dialog.getWindow() != null){
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            dialog.show();
        });
    }

    // to check if the user is already logged in
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void onClick(View view) {

        String email, password;
        email = String.valueOf(editTextEmail.getText());
        password = String.valueOf(editTextPassword.getText());

        // to check if the email and password is empty or not
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(Login.this, "Enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(Login.this, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(Login.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}