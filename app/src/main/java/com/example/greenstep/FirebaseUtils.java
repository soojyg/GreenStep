package com.example.greenstep;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtils {
    public interface UserTypeCallback{
        void onCallback(String userType);
        void onError(String errorMessage);
    }
    public static void getUserType(UserTypeCallback callback){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if(user != null){
            String userId = user.getUid();
            getUserTypeFromFirestore(userId, callback);
        } else{
            callback.onError("User not authenticated.");
        }
    }

    private static void getUserTypeFromFirestore(String userId, UserTypeCallback callback){
        FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
        CollectionReference usersCollection = firestoreDB.collection("Users");

        // Query to get the document for the current user
        usersCollection.document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        // The document exists, extract the user type
                        String userType = documentSnapshot.getString("User type");
                        callback.onCallback(userType);
                    } else{
                        callback.onError("User not found.");
                    }
                })
                .addOnFailureListener(e ->{
                    e.printStackTrace();
                    callback.onError("Error fetching user type.");
                });
    }
}
