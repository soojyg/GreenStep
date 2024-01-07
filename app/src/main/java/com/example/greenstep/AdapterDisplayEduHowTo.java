package com.example.greenstep;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterDisplayEduHowTo extends RecyclerView.Adapter<AdapterDisplayEduHowTo.MyViewHolder> {

    private ArrayList<HowToDataClass> dataList;
    private Context context;
    private NavController navController;
    LayoutInflater layoutInflater;
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView gridImage;
        AppCompatButton btnEduTitle;
        ImageView btnShare;
        CardView gridItem;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            gridImage = itemView.findViewById(R.id.gridImage);
            btnEduTitle = itemView.findViewById(R.id.btnTitle);
            btnShare = itemView.findViewById(R.id.imageShare);
            gridItem = itemView.findViewById(R.id.gridItem);
        }
    }

    @NonNull
    @Override
    public AdapterDisplayEduHowTo.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.how_to_grid_item,parent,false);
        return new AdapterDisplayEduHowTo.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDisplayEduHowTo.MyViewHolder holder, int position) {
        HowToDataClass currentItem = dataList.get(position);
        holder.btnEduTitle.setText(currentItem.getTitle());
        Glide.with(context)
                .load(currentItem.getImageURL())
                .placeholder(R.drawable.logo_app)
                .error(R.drawable.image_not_found)
                .into(holder.gridImage);

        holder.btnShare.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);

                String sharedText = "<b>Green Step:</b> "+ currentItem.getTitle() +"<br/>Learn more at: <a href='"+ currentItem.getSourceRef()+"'>"+ currentItem.getSourceRef()+"</a>";
                intent.putExtra(Intent.EXTRA_TEXT,android.text.Html.fromHtml(sharedText));

                intent.setType("*/*");

                if(intent.resolveActivity(context.getPackageManager())!=null){
                    context.startActivity(Intent.createChooser(intent,"Share with"));
                } else{
                    Toast.makeText(context, "No app can handle this action.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.gridItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                goToUrl(currentItem.getSourceRef());
            }
        });

    }

    public AdapterDisplayEduHowTo(ArrayList<HowToDataClass> dataList, Context context,NavController navController) {
        this.dataList = dataList;
        this.context = context;
        this.navController = navController;
    }


    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

//    @Override
//    public View getView(int i, View view, ViewGroup viewGroup) {
//        if (layoutInflater == null) {
//            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        }
//        if (view == null) {
//            view = layoutInflater.inflate(R.layout.how_to_grid_item, null);
//        }
//        ImageView gridImage = view.findViewById(R.id.gridImage);
//        AppCompatButton btnEduTitle = view.findViewById(R.id.btnTitle);
////        ToggleButton btnFavorite = view.findViewById(R.id.imageFavorite);
//        ImageView btnShare = view.findViewById(R.id.imageShare);
//        CardView gridItem = view.findViewById(R.id.gridItem);
//
//        // Load the image
//        Glide.with(context)
//                .load(dataList.get(i).getImageURL())
//                .placeholder(R.drawable.logo_app)
//                .error(R.drawable.image_not_found)
//                .into(gridImage);
//
//        // Set the title of the educational content
//        btnEduTitle.setText(dataList.get(i).getTitle());
//
//
////        btnFavorite.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                isFavorite = !isFavorite;
////                btnFavorite.setChecked(isFavorite);
////            }
////        });
//
//        // Set onClickListener for the share button
//        btnShare.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_SEND);
//
//                String sharedText = "<b>Green Step:</b> "+ dataList.get(i).getTitle() +"<br/>Learn more at: <a href='"+ dataList.get(i).getSourceRef()+"'>"+ dataList.get(i).getSourceRef()+"</a>";
//                intent.putExtra(Intent.EXTRA_TEXT,android.text.Html.fromHtml(sharedText));
//
//                intent.setType("*/*");
//
//                if(intent.resolveActivity(context.getPackageManager())!=null){
//                    context.startActivity(Intent.createChooser(intent,"Share with"));
//                } else{
//                    Toast.makeText(context, "No app can handle this action.",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        gridItem.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                goToUrl(dataList.get(i).getSourceRef());
//            }
//        });
//        return view;
//    }

    private void goToUrl(String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        context.startActivity(intent);
    }
}



