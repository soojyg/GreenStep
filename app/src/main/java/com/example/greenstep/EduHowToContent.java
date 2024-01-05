package com.example.greenstep;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;

public class EduHowToContent extends Fragment {
    private GridView gridView;
    private ArrayList<HowToDataClass> dataList;
    private AdapterDisplayEduHowTo adapter;


    final private FirebaseFirestore firestoreDbRef = FirebaseFirestore.getInstance();
    final private String collectionPath = "Edu Content Posts";


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @NonNull Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.edu_howto_content, container, false);

        gridView = rootView.findViewById(R.id.eduHowToPost);
        dataList = new ArrayList<>();
        adapter = new AdapterDisplayEduHowTo(dataList, requireContext());
        gridView.setAdapter(adapter);

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
        return rootView;
    }
}
