package com.example.greenstep;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EduAlternativesDetails extends Fragment {
    ImageView img;
    TextView defaultTxt, title, txtBack;
    AppCompatButton tip1, tip2;
    BottomNavigationView bottomNavigationView;
    NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @NonNull Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.edu_alternatives_details, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        img = view.findViewById(R.id.alterImg);
        defaultTxt = view.findViewById(R.id.defaultTxt);
        title = view.findViewById(R.id.titleAlt);
        tip1 = view.findViewById(R.id.btnTip1);
        tip2 = view.findViewById(R.id.btnTip2);
        txtBack = view.findViewById(R.id.txtBack);
        bottomNavigationView = getActivity().findViewById(R.id.bottom_nav_view);

        // Hide the BottomNavigationView in this fragment
        bottomNavigationView.setVisibility(View.GONE);

        // Retrieve data from the arguments bundle
        Bundle args = getArguments();
        if (args != null) {
            String titleValue = args.getString("Title", "");
            String tip1Value = args.getString("Tip 1", "");
            String tip2Value = args.getString("Tip 2", "");
            String imageUrl = args.getString("imageURL", "");
            // Set the title
            title.setText(titleValue);
            // Load the image
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.logo_app)
                    .error(R.drawable.image_not_found)
                    .into(img);
            // Set tip1 and tip2
            tip1.setText(tip1Value);
            if (tip2Value.length() != 0) {
                tip2.setText(tip2Value);
            } else {
                tip2.setVisibility(View.GONE);
            }
        }

        // Navigate back to the alternatives content fragment and make the BottomNavigationView visible
        txtBack.setOnClickListener(v ->{
            navController.navigate(R.id.navigate_to_eduAlternativesContent);
            bottomNavigationView.setVisibility(View.VISIBLE);
        });
    }

}
