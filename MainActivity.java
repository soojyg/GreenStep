package com.example.greenstep;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    ImageView profileIcon;
    FragmentContainerView mainFragmentContainer;
    BottomNavigationView bottomBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        profileIcon = findViewById(R.id.profile);
        user = auth.getCurrentUser();
        mainFragmentContainer = findViewById(R.id.mainFragmentContainer);
        bottomBar = findViewById(R.id.bottom_nav_view);


        profileIcon.setOnClickListener(view ->{
//            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
//            startActivity(intent);
            FirebaseUtils.getUserType(new FirebaseUtils.UserTypeCallback() {
                @Override
                public void onCallback(String userType) {
                    if(userType.equals("Admin")){
                        Intent adminIntent = new Intent(getApplicationContext(), AdminProfileGreenStep.class);
                        startActivity(adminIntent);
                    } else{ //userType = Normal User
                        Intent userIntent = new Intent(getApplicationContext(), ProfileGreenStep.class);
                        startActivity(userIntent);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(MainActivity.this,"Error: Unable to determine user type.",Toast.LENGTH_SHORT).show();
                }
            });

//            showFragment(new ProfileFragment());

        });


//        AppBarLayout appBarLayout = findViewById(R.id.toolbarLayout);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.mainFragmentContainer);
        NavController navController = host.getNavController();

//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        if (user == null) { // if user is null, we have to open the login activity and close the main activity
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else{ // if user is not null, indicating that the user is signed in
            String userId = user.getUid();

            // Obtain user type and set up bottom navigation bar based on user type
//            getUserTypeFromFirestore(userId, userType -> {
//                if(userType != null){
//                    String retrievedUserType = userType;
//                    if(userType.equals("Admin")){
//                        setupBottomNavMenuAdmin(navController);
//                    } else{
//                        setupBottomNavMenuNormUser(navController);
//                    }
//                } else{
//                    Toast.makeText(MainActivity.this,"Error: Unable to determine user type.",Toast.LENGTH_SHORT).show();
//                }
//            });
            FirebaseUtils.getUserType(new FirebaseUtils.UserTypeCallback() {
                @Override
                public void onCallback(String userType) {
                    if(userType.equals("Admin")){
                        setupBottomNavMenuAdmin(navController);
                        navController.setGraph(R.navigation.nav_admin);
                    } else{ //userType = Normal User
                        setupBottomNavMenuNormUser(navController);
                        navController.setGraph(R.navigation.nav_user);
                    }
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(MainActivity.this,"Error: Unable to determine user type.",Toast.LENGTH_SHORT).show();
                }
            });

            scheduleJob();
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.mainFragmentContainer, new ChallengeScreen())
//                    .addToBackStack(null)
//                    .commit();
        }

    }

    private void setupBottomNavMenuNormUser(NavController navController) {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);
        bottomNav.getMenu().clear();  // Clear existing menu items
        bottomNav.inflateMenu(R.menu.menu_bottom_user);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }

    private void setupBottomNavMenuAdmin(NavController navController) {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_view);
        bottomNav.getMenu().clear();  // Clear existing menu items
        bottomNav.inflateMenu(R.menu.menu_bottom_admin);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }



    private void scheduleJob() {
        ComponentName componentName = new ComponentName(this, ProgressUpdateJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(1, componentName)
                .setPeriodic(TimeUnit.HOURS.toMillis(1)) // Adjust the interval as needed
                .setPersisted(true) // Keep the job even after device reboots
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int resultCode = jobScheduler.schedule(jobInfo);


    }


}