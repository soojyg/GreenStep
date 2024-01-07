package com.example.greenstep;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdapterDeleteEduAlternatives extends RecyclerView.Adapter<AdapterDeleteEduAlternatives.MyViewHolder> {
    private ArrayList<AlternativesDataClass> dataList;
    private Context context;
    private FirebaseFirestore firestoreDbRef;
    private String collectionPath;
    public AdapterDeleteEduAlternatives(ArrayList<AlternativesDataClass> dataList, Context context, FirebaseFirestore firestoreDbRef, String collectionPath) {
        this.dataList = dataList;
        this.context = context;
        this.firestoreDbRef = firestoreDbRef;
        this.collectionPath = collectionPath;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView avoidTxt;
        TextView title;
        AppCompatButton startBtn;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            avoidTxt = itemView.findViewById(R.id.avoidTxt);
            title = itemView.findViewById(R.id.titleAlt);
            startBtn = itemView.findViewById(R.id.btnStart);
        }
    }

    @NonNull
    @Override
    public AdapterDeleteEduAlternatives.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.alternatives_item,parent,false);
        return new AdapterDeleteEduAlternatives.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDeleteEduAlternatives.MyViewHolder holder, int position){
        AlternativesDataClass currentItem = dataList.get(position);

        // Set the title
        holder.title.setText(currentItem.getTitle());

        // Modify the text inside the button to delete
        holder.startBtn.setText("Delete");

        // Delete the record when the button is clicked
        holder.startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // Get the document ID associated with the item
                String documentId = currentItem.getDocumentId();
                // Show a confirmation dialog
                showDeleteDialog(context, documentId); // Pass the position of the item
            }
        });
    }

    private void showDeleteDialog(Context c, String documentId){
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
                deleteTip(documentId);
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

    private void deleteTip(String documentId){
        // Remove the item from the datalist
        removeItemFromDataList(documentId);
        // Notify the adapter that the dataset has changed
        notifyDataSetChanged();

        // Delete the corresponding record from the Firestore database
//        String documentID = getDocumentIdFromDataList(position);
        deleteRecordFromFirestore(documentId);
    }

    private void removeItemFromDataList(String documentId) {
        for (AlternativesDataClass item : dataList) {
            if (item.getDocumentId().equals(documentId)) {
                dataList.remove(item);
                return; // exit the loop once the item is removed
            }
        }
    }

    private String getDocumentIdFromDataList(int position){
        return dataList.get(position).getDocumentId();
    }

    private void deleteRecordFromFirestore(String documentId){
        firestoreDbRef.collection(collectionPath)
                .document(documentId)
                .delete()
                .addOnSuccessListener(aVoid ->{
                    Toast.makeText(context, "Tips deleted successfully from Firestore.",Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->{
                    Toast.makeText(context, "Failed to delete tips from Firestore.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
