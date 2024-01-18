package com.example.greenstep;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdapterDeleteEduHowTo extends RecyclerView.Adapter<AdapterDeleteEduHowTo.MyViewHolder> {
    private ArrayList<HowToDataClass> dataList;
    private Context context;
    private FirebaseFirestore firestoreDbRef;
    private String collectionPath;

    public AdapterDeleteEduHowTo(ArrayList<HowToDataClass> dataList, Context context, FirebaseFirestore firestoreDbRef, String collectionPath) {
        this.dataList = dataList;
        this.context = context;
        this.firestoreDbRef = firestoreDbRef;
        this.collectionPath = collectionPath;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView gridImage;
        AppCompatButton btnEduTitle;
        ImageView deleteBtn;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            gridImage = itemView.findViewById(R.id.gridImage);
            btnEduTitle = itemView.findViewById(R.id.btnTitle);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }

    @NonNull
    @Override
    public AdapterDeleteEduHowTo.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.how_to_grid_item,parent,false);
        return new AdapterDeleteEduHowTo.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDeleteEduHowTo.MyViewHolder holder, int position) {
        HowToDataClass currentItem = dataList.get(position);
        // Load the image
        Glide.with(context)
                .load(currentItem.getImageURL())
                .placeholder(R.drawable.logo_app)
                .error(R.drawable.image_not_found)
                .into(holder.gridImage);

        // Set the title of the educational content
        holder.btnEduTitle.setText(currentItem.getTitle());


        // Set the visibility of the delete icon
        holder.deleteBtn.setVisibility(View.VISIBLE);

        // Set onClickListener for deleteBtn
        holder.deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Get the document ID associated with the item
                String documentId = currentItem.getDocumentId();
                // Show a confirmation dialog
                showDeleteDialog(context, documentId); // Pass the position of the item
            }
        });
    }


    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    private void showDeleteDialog(Context c, String documentId){
        // Instantiate the custom dialog
        Dialog dialog = new Dialog(c);
        dialog.setContentView(R.layout.dialog_delete);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.delete_dialog_background);

        // Initialize positive and negative buttons
        AppCompatButton positiveBtn = dialog.findViewById(R.id.positiveBtn);
        AppCompatButton negativeBtn = dialog.findViewById(R.id.negativeBtn);

        // Set onClickListener for the positive button
        positiveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // Call the deletePost method with the specified documentId
                deletePost(documentId);
                // Dismiss the dialog after deletion
                dialog.dismiss();
            }
        });

        // Set onClickListener for the negative button
        negativeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // Dismiss the dialog without performing any action
                dialog.dismiss();
            }
        });
        // Show the dialog
        dialog.show();
    }

    private void deletePost(String documentId){
        // Remove the item from the datalist
        removeItemFromDataList(documentId);

        // Notify the adapter that the dataset has changed
        notifyDataSetChanged();

        // Delete the corresponding record from the Firestore database
        deleteRecordFromFirestore(documentId);

    }

    private void removeItemFromDataList(String documentId) {
        for (HowToDataClass item : dataList) {
            if (item.getDocumentId().equals(documentId)) {
                dataList.remove(item);
                return; // exit the loop once the item is removed
            }
        }
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
