package com.example.greenstep;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.net.Uri;
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

public class NewsAdapter extends RecyclerView.Adapter<NewsHolder> implements NewsDetail.OnItemDeletedListener, Serializable {
    private Context context;
    private List<NewsDataClass> dataList;

    private EditAvailabilityManager editAvailabilityManager;
    boolean isEditAvailable;
    private final int REQUEST_CODE_DETAIL = 1;
    private NavController navController;


    public NewsAdapter(Context context, List<NewsDataClass> dataList,EditAvailabilityManager manager,NavController navController) {
        this.context = context;
        this.dataList = dataList;
        this.editAvailabilityManager = manager;
        this.navController = navController;

    }

    @NonNull
    @Override
    public NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item_news, parent, false);
        return new NewsHolder(view, dataList, context);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);
        holder.recAuthor.setText(dataList.get(position).getDataAuthor());
        holder.recTitle.setText(dataList.get(position).getDataTitle());
        holder.recDate.setText(dataList.get(position).getDataDate());
        String eventLink = dataList.get(position).getDataLang();
        Log.d("EditStatus GET FROM MANAGER", "isEditAvailable GET FROM MANAGER ORI ORI: " + isEditAvailable);


        holder.recCard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("Image", dataList.get(holder.getAdapterPosition()).getDataImage());
                bundle.putString("Title", dataList.get(holder.getAdapterPosition()).getDataTitle());
                bundle.putString("Author", dataList.get(holder.getAdapterPosition()).getDataAuthor());
                bundle.putString("Key",dataList.get(holder.getAdapterPosition()).getKey());
                bundle.putString("Language", dataList.get(holder.getAdapterPosition()).getDataLang());
                bundle.putString("Date", dataList.get(holder.getAdapterPosition()).getDataDate());

//                Intent intent = new Intent(context, NewsDetail.class);
//                intent.putExtra("Image", dataList.get(holder.getAdapterPosition()).getDataImage());
//                intent.putExtra("Title", dataList.get(holder.getAdapterPosition()).getDataTitle());
//                intent.putExtra("Author", dataList.get(holder.getAdapterPosition()).getDataAuthor());
//                intent.putExtra("Key",dataList.get(holder.getAdapterPosition()).getKey());
//                intent.putExtra("Language", dataList.get(holder.getAdapterPosition()).getDataLang());
//                intent.putExtra("Date", dataList.get(holder.getAdapterPosition()).getDataDate());
                boolean isEditAvailable = editAvailabilityManager.isEditAvailable();
                Log.d("Author get", "author: " + dataList.get(holder.getAdapterPosition()).getDataAuthor());



                if(isEditAvailable) {
                    Log.d("EditStatus GET FROM MANAGER", "isEditAvailable GET FROM MANAGER TRUE: " + isEditAvailable);
//                    ((Activity) context).startActivityForResult(intent, REQUEST_CODE_DETAIL);
//
//                    context.startActivity(intent);
                    navController.navigate(R.id.action_to_newsDetail, bundle);

                }else {
                    Log.d("EditStatus GET FROM MANAGER", "isEditAvailable GET FROM MANAGER FALSE: " + isEditAvailable);

                    if (eventLink != null && !eventLink.isEmpty()) {
                        // Create an intent to open the link in a web browser
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(eventLink));
                        context.startActivity(browserIntent);

                    } else {
                        // Handle the case where the link is not available
                        Toast.makeText(context, "Link not available for this event", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<NewsDataClass> searchList){
        dataList = searchList;
        notifyDataSetChanged();}

    @Override
    public void onItemDeleted(String key) {
        int position = findPositionByKey(key);
        if (position != -1) {
            dataList.remove(position);
            notifyItemRemoved(position);

        }

    }

    private int findPositionByKey(String key) {
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).getKey().equals(key)) {
                return i;
            }
        }
        return -1;
    }






}

class NewsHolder extends RecyclerView.ViewHolder{
    ImageView recImage;
    TextView recAuthor, recTitle, recDate;
    CardView recCard;
    private List<NewsDataClass> dataList;
    private Context context;

    public NewsHolder(@NonNull View itemView, List<NewsDataClass> dataList, Context context) {
        super(itemView);
        this.dataList = dataList;
        this.context = context;

        recImage = itemView.findViewById(R.id.recNewsImage);
        recCard = itemView.findViewById(R.id.recCardNews);
        recTitle = itemView.findViewById(R.id.recTitleNews);
        recAuthor = itemView.findViewById(R.id.recAuthor);
        recDate = itemView.findViewById(R.id.recDate);



    }


}
