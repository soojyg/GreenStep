package com.example.greenstep;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.nullness.qual.Nullable;

public class EventUpdate extends Fragment {
    ImageView updateImage,cancel;
    Button btnUpdate;
    AlertDialog dialog;

    EditText updateDesc, updateTitle, updateLang;
    String title, desc, lang;
    String imageUrl;
    String key, oldImageURL;
    Uri uri;
    String eventId;
    FirebaseFirestore db;
    DocumentReference documentReference;
    StorageReference storageReference;
    BottomNavigationView bottomNavigationView;
    //    private UpdateListener updateListener;

    @Override
    public View onCreateView(@org.checkerframework.checker.nullness.qual.NonNull LayoutInflater inflater, @Nullable ViewGroup container, @org.checkerframework.checker.nullness.qual.NonNull Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.event_update,container, false);
        return rootView;

    }

    @Override
    public void onViewCreated(@org.checkerframework.checker.nullness.qual.NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnUpdate = view.findViewById(R.id.btnUpdate);
        updateDesc = view.findViewById(R.id.updateCompanyOrgani);
        updateImage = view.findViewById(R.id.updateEventImage);
        updateLang = view.findViewById(R.id.updateSourceRef);
        updateTitle = view.findViewById(R.id.updateEventName);
        cancel = view.findViewById(R.id.cancel);
        bottomNavigationView = getActivity().findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setVisibility(View.GONE);
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            updateImage.setImageURI(uri);
                        } else {
                            Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        Bundle args = getArguments();
        if (args != null){
            Glide.with(EventUpdate.this).load(args.getString("Image")).into(updateImage);
            updateTitle.setText(args.getString("Title"));
            updateDesc.setText(args.getString("Description"));
            updateLang.setText(args.getString("Language"));
            key = args.getString("Key");
            oldImageURL = args.getString("Image");
        }

        db = FirebaseFirestore.getInstance();


        // Set the reference to the Firestore document based on the document ID
        documentReference = db.collection("Events").document(key);

        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(view);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Update incomplete", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(EventUpdate.this, EventAdminView.class);
//                startActivity(intent);
                Navigation.findNavController(view).popBackStack();
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });


    }
    public void saveData(View view) {
        if (uri != null) {
            // User selected a new photo, upload it
            storageReference = FirebaseStorage.getInstance().getReference()
                    .child("eventPictures").child("eventPic").child(uri.getLastPathSegment());

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setCancelable(false);
            builder.setView(R.layout.progress_layout);
            dialog = builder.create();
            dialog.show();

            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    uriTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri urlImage = task.getResult();
                                imageUrl = urlImage.toString();
                                updateData(view);
                                dialog.dismiss(); // Dismiss the dialog on failure

                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss(); // Dismiss the dialog on failure
                    // Handle the failure, e.g., show an error message
                    Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // User didn't select a new photo, continue using the old photo URL
            imageUrl = oldImageURL;
            updateData(view);
        }
    }

    public void updateData(View view) {
        title = updateTitle.getText().toString().trim();
        desc = updateDesc.getText().toString().trim();
        lang = updateLang.getText().toString();

        DataClass dataClass = new DataClass(title, desc, lang, imageUrl);

        // Update the Firestore document
        documentReference.set(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL);
                    reference.delete();
                    Toast.makeText(requireContext(), "Updated", Toast.LENGTH_SHORT).show();
//                    finish();
//                    Intent intent = new Intent(requireContext(), EventAdminView.class);
//                    startActivity(intent);
                    Navigation.findNavController(view).popBackStack();
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }
//                Intent intent = new Intent(EventUpdate.this, EventAdminView.class);
//                startActivity(intent);
                Navigation.findNavController(view).popBackStack();
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss(); // Dismiss the dialog on failure
                Toast.makeText(requireContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}