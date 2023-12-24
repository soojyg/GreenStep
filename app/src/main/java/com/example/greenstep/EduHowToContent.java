package com.example.greenstep;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;

public class EduHowToContent extends Fragment {
    GridView gridView;
//    Button btnAdd;
    ArrayList<DataClass> dataList;
    MyAdapter adapter;


    final private FirebaseFirestore firestoreDbRef = FirebaseFirestore.getInstance();
    final private String collectionPath = "Edu Content Posts";


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @NonNull Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.edu_howto_content, container, false);

        gridView = rootView.findViewById(R.id.eduHowToPost);
        dataList = new ArrayList<>();
        adapter = new MyAdapter(dataList, requireContext());
        gridView.setAdapter(adapter);

        // Load title, image url, and image from firestore database
        firestoreDbRef.collection(collectionPath).get()
                .addOnCompleteListener(task ->{
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot document : task.getResult()){
                            DataClass dataClass = new DataClass(
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
        return rootView;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.edu_howto_content);
//
////        gridView = findViewById(R.id.gridView);
////        btnAdd = findViewById(R.id.btnAdd);
//
//        editTxt= findViewById(R.id.editTxt);
//        uploadTxt = findViewById(R.id.uploadTxt);
//
//        uploadTxt.setPaintFlags(editTxt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//        editTxt.setPaintFlags(editTxt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//
//
//
//
//        uploadTxt.setOnClickListener(view ->{
//            Intent intent = new Intent(getApplicationContext(), UploadHowTo.class);
//            startActivity(intent);
//            finish();
//        });
//    }


    // Method to open a webpage using a browser intent
//    private void openWebPage(String url) {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(Uri.parse(url));
//        startActivity(intent);
//    }
}
