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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;

public class EduHowToDelete extends Fragment {
    GridView gridView;
    ArrayList<DataClass> dataList;
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

        gridView = view.findViewById(R.id.eduHowToPost);
        dataList = new ArrayList<>();
        adapter = new AdapterDeleteEduHowTo(dataList, requireContext(), firestoreDbRef, collectionPath);
        gridView.setAdapter(adapter);
        bottomNavigationView = getActivity().findViewById(R.id.bottom_nav_view);
        closeBtn = view.findViewById(R.id.close);

        bottomNavigationView.setVisibility(View.GONE);

        // Load title, image url, and image from firestore database
        firestoreDbRef.collection(collectionPath).get()
                .addOnCompleteListener(task ->{
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            DataClass dataClass = new DataClass(
                                    document.getId(),
                                    (String) document.get(FieldPath.of("Title")),
                                    (String) document.get(FieldPath.of("Source/ Reference")),
                                    document.getString("ImageUrl")
                            );
                            dataList.add(dataClass);
//                            Log.d("ImageURL", "Image URL: " + dataClass.getImageURL());

                        }
                        adapter.notifyDataSetChanged();

                    } else{
                        Toast.makeText(requireContext(),"Failed to load image.",Toast.LENGTH_SHORT).show();
                    }
                });

        closeBtn.setOnClickListener(v ->{
            bottomNavigationView.setVisibility(View.VISIBLE);
            Navigation.findNavController(view).popBackStack();
        });
    }
}
