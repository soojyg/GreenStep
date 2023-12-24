package com.example.greenstep;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    TextView tv;
    ImageView backBtn;
    Button logoutBtn;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        tv = findViewById(R.id.txtTemp);
        backBtn = findViewById(R.id.backButton);
        logoutBtn = findViewById(R.id.logOutBtn);

        backBtn.setOnClickListener(v ->{
           onBackPressed();
        });

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        logoutBtn.setOnClickListener(view -> {
            auth.signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });
    }

    public void onBackPressed(){
        super.onBackPressed();
    }




//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @NonNull Bundle savedInstanceState){
//        View rootView = inflater.inflate(R.layout.profile_fragment,container,false);
//        return rootView;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
//        super.onViewCreated(view, savedInstanceState);
//        tv = view.findViewById(R.id.txtTemp);
//    }
}
