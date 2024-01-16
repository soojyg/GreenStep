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

          /**
         * Constructor for the EventAdapter.
         *
         * @param context               Context of the application.
         * @param dataList              List of event data.
         * @param manager               EditAvailabilityManager for controlling edit availability.
         * @param navController         NavController for handling fragment navigation.
         */
    public EventAdapter(Context context, List<DataClass> dataList,EditAvailabilityManager manager, NavController navController) {
            this.context = context;
            this.dataList = dataList;
            this.editAvailabilityManager = manager;
            this.navController = navController;

            }

        /**
         * Inflates the layout for each item in the RecyclerView and creates a new EventHolder to hold the views.
         *
         * @param parent   The parent ViewGroup.
         * @param viewType The type of the new View.
         * @return A new EventHolder that holds the views for each item in the RecyclerView.
         */
        @NonNull
        @Override
        public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item_event, parent, false);
            return new EventHolder(view, dataList, context);
        }

        /**
         * Binds the data to the views in each item of the RecyclerView.
         *
         * @param holder   The EventHolder containing views for a single item.
         * @param position The position of the item within the RecyclerView.
         */
        @Override
        public void onBindViewHolder(@NonNull EventHolder holder, int position) {
            // Binding data to the views in each item of the RecyclerView
            Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);
            holder.recTitle.setText(dataList.get(position).getDataTitle());
            holder.recDesc.setText(dataList.get(position).getDataDesc());
            String eventLink = dataList.get(position).getDataLang();

            // Handling item click event
            holder.recCard.setOnClickListener(new View.OnClickListener() {

    @Override
    public void onClick(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("Image", dataList.get(holder.getAdapterPosition()).getDataImage());
        bundle.putString("Description", dataList.get(holder.getAdapterPosition()).getDataDesc());
        bundle.putString("Title", dataList.get(holder.getAdapterPosition()).getDataTitle());
        bundle.putString("Key",dataList.get(holder.getAdapterPosition()).getKey());
        bundle.putString("Language", dataList.get(holder.getAdapterPosition()).getDataLang());
        Boolean isEditAvailable = editAvailabilityManager.isEditAvailable();

            if(isEditAvailable) {
            Log.d("EditStatus GET FROM MANAGER", "isEditAvailable GET FROM MANAGER TRUE: " + isEditAvailable);
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

        /**
         * Sets a new list of data for the adapter and notifies the changes.
         *
         * @param searchList The new list of data.
         */
            public void searchDataList(ArrayList<DataClass> searchList){
            dataList = searchList;
            notifyDataSetChanged();}


        // Helper method to find the position of an item based on its key

        private int findPositionByKey(String key) {
            for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getKey().equals(key)) {
            return i;
            }
            }
            return -1;
            }



            }

/**
 * ViewHolder class for holding the views of each item in the event RecyclerView.
 */
    class EventHolder extends RecyclerView.ViewHolder{
        ImageView recImage;
        TextView recTitle, recDesc;
        CardView recCard;
        Button btnJoin;
        private List<DataClass> dataList;
        private Context context;


    /**
     * Constructor for the EventHolder.
     *
     * @param itemView View representing each item in the RecyclerView.
     * @param dataList List of event data.
     * @param context  Context of the application.
     */
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
