package com.example.greenstep;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;

public class EduHowToDelete extends Fragment {
    RecyclerView recyclerView;
    ArrayList<HowToDataClass> dataList;
    AdapterDeleteEduHowTo adapter;
    ImageView closeBtn;
    BottomNavigationView bottomNavigationView;


    final private FirebaseFirestore firestoreDbRef = FirebaseFirestore.getInstance();
    final private String collectionPath = "Edu Content Posts";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @NonNull Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.edu_howto_delete,container, false);
        return rootView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerView);
        dataList = new ArrayList<>();
        adapter = new AdapterDeleteEduHowTo(dataList, requireContext(), firestoreDbRef, collectionPath);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(),2));
        bottomNavigationView = getActivity().findViewById(R.id.bottom_nav_view);
        closeBtn = view.findViewById(R.id.close);

        // Hide bottom navigation view
        bottomNavigationView.setVisibility(View.GONE);

        // Load title, image url, and image from firestore database
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
                        }
                        adapter.notifyDataSetChanged();

                    } else{
                        Toast.makeText(requireContext(),"Failed to load image.",Toast.LENGTH_SHORT).show();
                    }
                });

        // Set onClickListener for the close button
        closeBtn.setOnClickListener(v ->{
            bottomNavigationView.setVisibility(View.VISIBLE);
            Navigation.findNavController(view).popBackStack();
        });
    }
}
