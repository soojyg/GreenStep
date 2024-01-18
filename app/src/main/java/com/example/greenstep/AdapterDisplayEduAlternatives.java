package com.example.greenstep;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterDisplayEduAlternatives extends RecyclerView.Adapter<AdapterDisplayEduAlternatives.MyViewHolder> {
    private ArrayList<AlternativesDataClass> dataList;
    private Context context;
    private NavController navController;
    public AdapterDisplayEduAlternatives(ArrayList<AlternativesDataClass> dataList, Context context, NavController navController) {
        this.dataList = dataList;
        this.context = context;
        this.navController = navController;
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
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.alternatives_item,parent,false);
        return new MyViewHolder(view);
    }

    // Bind data to the ViewHolder
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position){
        AlternativesDataClass currentItem = dataList.get(position);

        // Set the title
        holder.title.setText(currentItem.getTitle());

        // Set onClickListener for the "Start" button to navigate to alternative details
        holder.startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if(adapterPosition != RecyclerView.NO_POSITION){
                    // Prepare data bundle for the details fragment
                    Bundle bundle = new Bundle();
                    bundle.putString("Title",dataList.get(adapterPosition).getTitle());
                    bundle.putString("Tip 1",dataList.get(adapterPosition).getTip1());
                    bundle.putString("Tip 2",dataList.get(adapterPosition).getTip2());
                    bundle.putString("imageURL",dataList.get(adapterPosition).getImageURL());

                    // Navigate to the details fragment with the prepared bundle
                    navController.navigate(R.id.action_to_eduAlternativesDetails, bundle);
                }

            }
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

    // Method to update the adapter with a filtered data list
    public void searchDataList(ArrayList<AlternativesDataClass> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }
}
