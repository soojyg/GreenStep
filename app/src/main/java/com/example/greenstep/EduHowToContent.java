package com.example.greenstep;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

//import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;

public class EduHowToContent extends Fragment {
    private GridView gridView;
    private RecyclerView recyclerView;
    private LinearLayout editLayout;
    TextView txtEdit, tabHowto, tabAlter;
    FloatingActionButton uploadBtn;
    private ArrayList<HowToDataClass> dataList;
    private AdapterDisplayEduHowTo adapter;
    private NavController navController;


    final private FirebaseFirestore firestoreDbRef = FirebaseFirestore.getInstance();
    final private String collectionPath = "Edu Content Posts";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @NonNull Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.edu_howto_content,container,false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        recyclerView = view.findViewById(R.id.recyclerView);
        editLayout = view.findViewById(R.id.linearRowedit);
        txtEdit = view.findViewById(R.id.txtEdit);
        tabHowto = view.findViewById(R.id.tabHowTo);
        tabAlter = view.findViewById(R.id.tabAlternatives);
        tabHowto.setTextColor(getResources().getColor(R.color.black));
        tabHowto.setTypeface(null, Typeface.BOLD);
        uploadBtn = view.findViewById(R.id.uploadBtn);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(),2));

        dataList = new ArrayList<>();
        adapter = new AdapterDisplayEduHowTo(dataList, requireContext(), navController);
        recyclerView.setAdapter(adapter);
        // Set the visibility of the "edit" and "upload" button
        FirebaseUtils.getUserType(new FirebaseUtils.UserTypeCallback() {
            @Override
            public void onCallback(String userType) {
                if(userType.equals("Admin")){
                    editLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(),"Error: Unable to determine user type.",Toast.LENGTH_SHORT).show();
            }
        });

        // Load documentID, title, image url, and image from firestore database
        firestoreDbRef.collection(collectionPath).get()
                .addOnCompleteListener(task ->{
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            HowToDataClass howToDataClass = new HowToDataClass(
                                    document.getId(),
                                    (String) document.get(FieldPath.of("Title")),
                                    (String) document.get(FieldPath.of("Source/ Reference")),
                                    document.getString("ImageUrl")
                            );
                            dataList.add(howToDataClass);
//                            Log.d("ImageURL", "Image URL: " + dataClass.getImageURL());

                        }
                        adapter.notifyDataSetChanged();

                    } else{
                        Toast.makeText(requireContext(),"Failed to load image.",Toast.LENGTH_SHORT).show();
                    }
                });

        txtEdit.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_to_editHowToPage);
        });

        uploadBtn.setOnClickListener(v ->{
            Navigation.findNavController(view).navigate(R.id.action_to_howtoUploadPage);
        });
        tabAlter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(NewsAdminView.this,EventAdminView.class);
//                startActivity(intent);
                Navigation.findNavController(view).navigate(R.id.navigate_to_alternatives);
            }
        });

    }
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @NonNull Bundle savedInstanceState){
//        View rootView = inflater.inflate(R.layout.edu_howto_content, container, false);
//        editLayout = rootView.findViewById(R.id.linearRowedit);
//        txtEdit = rootView.findViewById(R.id.txtEdit);
//        uploadBtn = rootView.findViewById(R.id.uploadBtn);
////        gridView = rootView.findViewById(R.id.eduHowToPost);
//        recyclerView = rootView.findViewById(R.id.recyclerView);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//        dataList = new ArrayList<>();
//        adapter = new AdapterDisplayEduHowTo(dataList, requireContext());
//        gridView.setAdapter(adapter);
//
//        // Load documentID, title, image url, and image from firestore database
//        firestoreDbRef.collection(collectionPath).get()
//                .addOnCompleteListener(task ->{
//                    if(task.isSuccessful()){
//                        for(QueryDocumentSnapshot document : task.getResult()){
//                            HowToDataClass howToDataClass = new HowToDataClass(
//                                    document.getId(),
//                                    (String) document.get(FieldPath.of("Title")),
//                                    (String) document.get(FieldPath.of("Source/ Reference")),
//                                    document.getString("ImageUrl")
//                            );
//                            dataList.add(howToDataClass);
////                            Log.d("ImageURL", "Image URL: " + dataClass.getImageURL());
//
//                        }
//                        adapter.notifyDataSetChanged();
//
//                    } else{
//                        Toast.makeText(requireContext(),"Failed to load image.",Toast.LENGTH_SHORT).show();
//                    }
//                });
//        return rootView;
//    }
}
