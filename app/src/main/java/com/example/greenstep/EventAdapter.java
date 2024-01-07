package com.example.greenstep;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

    public class EventAdapter extends RecyclerView.Adapter<EventHolder> implements NewsDetail.OnItemDeletedListener, Serializable {
    private Context context;
        EventAdminView user = new EventAdminView();
    private List<DataClass> dataList;

    private EditAvailabilityManager editAvailabilityManager;
        private NavController navController;
    private final int REQUEST_CODE_DETAIL = 1;

    public EventAdapter(Context context, List<DataClass> dataList,EditAvailabilityManager manager, NavController navController) {
            this.context = context;
            this.dataList = dataList;
            this.editAvailabilityManager = manager;
            this.navController = navController;

            }


        @NonNull
        @Override
        public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item_event, parent, false);
            return new EventHolder(view, dataList, context);
        }

        @Override
    public void onBindViewHolder(@NonNull EventHolder holder, int position) {
            Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);
            holder.recTitle.setText(dataList.get(position).getDataTitle());
            holder.recDesc.setText(dataList.get(position).getDataDesc());
            String eventLink = dataList.get(position).getDataLang();


            holder.recCard.setOnClickListener(new View.OnClickListener() {

    @Override
    public void onClick(View view) {
//            Intent intent = new Intent(context, EventDetail.class);
//            intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getDataImage());
//            intent.putExtra("Description", dataList.get(holder.getAdapterPosition()).getDataDesc());
//            intent.putExtra("Title", dataList.get(holder.getAdapterPosition()).getDataTitle());
//            intent.putExtra("Key",dataList.get(holder.getAdapterPosition()).getKey());
//            intent.putExtra("Language", dataList.get(holder.getAdapterPosition()).getDataLang());
//            Boolean isEditAvailable = editAvailabilityManager.isEditAvailable();
        Bundle bundle = new Bundle();
        bundle.putString("Image", dataList.get(holder.getAdapterPosition()).getDataImage());
        bundle.putString("Description", dataList.get(holder.getAdapterPosition()).getDataDesc());
        bundle.putString("Title", dataList.get(holder.getAdapterPosition()).getDataTitle());
        bundle.putString("Key",dataList.get(holder.getAdapterPosition()).getKey());
        bundle.putString("Language", dataList.get(holder.getAdapterPosition()).getDataLang());
        Boolean isEditAvailable = editAvailabilityManager.isEditAvailable();

            if(isEditAvailable) {
            Log.d("EditStatus GET FROM MANAGER", "isEditAvailable GET FROM MANAGER TRUE: " + isEditAvailable);
//                ((Activity) context).startActivityForResult(intent, REQUEST_CODE_DETAIL);
//            context.startActivity(intent);
                navController.navigate(R.id.action_to_eventDetail, bundle);

            }else {
                Log.d("EditStatus GET FROM MANAGER", "isEditAvailable GET FROM MANAGER FALSE: " + isEditAvailable);

            }
                }
            });



            }

    @Override
    public int getItemCount() {
            return dataList.size();
            }

    @Override
    public void onItemDeleted(String key) {
            int position = findPositionByKey(key);
            if (position != -1) {
            dataList.remove(position);
            notifyItemRemoved(position);

            }

            }

        public void searchDataList(ArrayList<DataClass> searchList){
            dataList = searchList;
            notifyDataSetChanged();}


    private int findPositionByKey(String key) {
            for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getKey().equals(key)) {
            return i;
            }
            }
            return -1;
            }



            }

    class EventHolder extends RecyclerView.ViewHolder{
        ImageView recImage;
        TextView recTitle, recDesc;
        CardView recCard;
        Button btnJoin;
        private List<DataClass> dataList;
        private Context context;


        public EventHolder(@NonNull View itemView, List<DataClass> dataList, Context context) {
            super(itemView);
            this.dataList = dataList;
            this.context = context;

            recImage = itemView.findViewById(R.id.recImage);
            recCard = itemView.findViewById(R.id.recCardEventParent);
            recDesc = itemView.findViewById(R.id.recDesc);
            recTitle = itemView.findViewById(R.id.recTitle);
            btnJoin = itemView.findViewById(R.id.btnJoin);


            btnJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Get the event link associated with this item
                    String eventLink = dataList.get(getAdapterPosition()).getDataLang();

                    // Check if the link is not empty
                    if (eventLink != null && !eventLink.isEmpty()) {
                        // Create an intent to open the link in a web browser
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(eventLink));
                        context.startActivity(browserIntent);
                    } else {
                        // Handle the case where the link is not available
                        Toast.makeText(context, "Link not available for this event", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }


    }
