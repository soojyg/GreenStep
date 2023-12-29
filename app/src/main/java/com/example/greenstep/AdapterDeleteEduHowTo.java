package com.example.greenstep;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdapterDeleteEduHowTo extends BaseAdapter {
    private ArrayList<DataClass> dataList;
    private Context context;
    private FirebaseFirestore firestoreDbRef;
    private String collectionPath;
    LayoutInflater layoutInflater;

    public AdapterDeleteEduHowTo(ArrayList<DataClass> dataList, Context context, FirebaseFirestore firestoreDbRef, String collectionPath) {
        this.dataList = dataList;
        this.context = context;
        this.firestoreDbRef = firestoreDbRef;
        this.collectionPath = collectionPath;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null) {
            view = layoutInflater.inflate(R.layout.grid_item, null);
        }
        ImageView gridImage = view.findViewById(R.id.gridImage);
        AppCompatButton btnEduTitle = view.findViewById(R.id.btnEdu);
        ImageView deleteBtn = view.findViewById(R.id.deleteBtn);

        // Load the image
        Glide.with(context)
                .load(dataList.get(i).getImageURL())
                .placeholder(R.drawable.logo_app)
                .error(R.drawable.image_not_found)
                .into(gridImage);

        // Set the title of the educational content
        btnEduTitle.setText(dataList.get(i).getTitle());


        // Set the visibility of the delete icon
        deleteBtn.setVisibility(View.VISIBLE);

        // Set onClickListener for deleteBtn
        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Show a confirmation dialog
                showDeleteDialog(context, i); // Pass the position of the item
            }
        });

        return view;
    }

    private void showDeleteDialog(Context c, int position){
        // Instantiate the custom dialog
        Dialog dialog = new Dialog(c);
        dialog.setContentView(R.layout.dialog_delete);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.delete_dialog_background);


        AppCompatButton positiveBtn = dialog.findViewById(R.id.positiveBtn);
        AppCompatButton negativeBtn = dialog.findViewById(R.id.negativeBtn);

        positiveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                deletePost(position);
                dialog.dismiss();
            }
        });

        negativeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void deletePost(int position){
        // Remove the item from the datalist
        dataList.remove(position);
        // Notify the adapter that the dataset has changed
        notifyDataSetChanged();

        // Delete the corresponding record from the Firestore database
        String documentID = getDocumentIdFromDataList(position);
        deleteRecordFromFirestore(documentID);

    }

    // Method to get the documentID of the edu post
    private String getDocumentIdFromDataList(int position){
        return dataList.get(position).getDocumentId();
    }

    // Method to delete the record from Firestore Database
    private void deleteRecordFromFirestore(String documentId){
        firestoreDbRef.collection(collectionPath)
                .document(documentId)
                .delete()
                .addOnSuccessListener(aVoid ->{
                    Toast.makeText(context, "Post deleted successfully from Firestore.",Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->{
                    Toast.makeText(context, "Failed to delete post from Firestore.", Toast.LENGTH_SHORT).show();
                });
    }
}
