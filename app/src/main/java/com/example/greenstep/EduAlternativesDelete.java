package com.example.greenstep;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;

public class EduAlternativesDelete extends Fragment {
    ScrollView scrollView;
    RecyclerView recyclerView;
    ImageView closeBtn;
    BottomNavigationView bottomNavigationView;
    ArrayList<AlternativesDataClass> dataList;
    AdapterDeleteEduAlternatives adapter;
    NavController navController;

    final private FirebaseFirestore firestoreDbRef = FirebaseFirestore.getInstance();
    final private String collectionPath = "Edu Content Tips";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @NonNull Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.edu_alternatives_delete,container,false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AdapterDeleteEduAlternatives(dataList, requireContext(), firestoreDbRef, collectionPath);
        recyclerView.setAdapter(adapter);
        navController = Navigation.findNavController(view);
        scrollView = view.findViewById(R.id.scrollView);
        bottomNavigationView = getActivity().findViewById(R.id.bottom_nav_view);
        closeBtn = view.findViewById(R.id.close);

        // Set the visibility of bottom navigation view to GONE
        bottomNavigationView.setVisibility(View.GONE);

        // Load documentID, title, tip1, tip2, image url, and image from firestore database
        firestoreDbRef.collection(collectionPath).get()
                .addOnCompleteListener(task ->{
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            AlternativesDataClass alternativesDataClass = new AlternativesDataClass(
                                    document.getId(),
                                    (String) document.get(FieldPath.of("Title")),
                                    (String) document.get(FieldPath.of("Tip 1")),
                                    (String) document.get(FieldPath.of("Tip 2")),
                                    document.getString("ImageUrl")
                            );
                            dataList.add(alternativesDataClass);

                        }
                        adapter.notifyDataSetChanged();

                    } else{
                        Toast.makeText(requireContext(),"Failed to load image.",Toast.LENGTH_SHORT).show();
                    }
                });

        closeBtn.setOnClickListener(v ->{
            bottomNavigationView.setVisibility(View.VISIBLE);
            navController.navigate(R.id.navigate_to_eduAlternativesContent);
        });

    }

}
