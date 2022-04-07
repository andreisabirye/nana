package com.dlc.nana.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.dlc.nana.ProviderHomeFragment;
import com.dlc.nana.R;
import com.dlc.nana.SelectedServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeFragment extends Fragment {
    EditText search;
    ImageView laundry_btn, cleaning_btn, compound_btn, shopping_btn, cooking_btn, carwash_btn;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("User_Prefs", Context.MODE_PRIVATE);
        search = root.findViewById(R.id.user_search_service);
        laundry_btn = root.findViewById(R.id.user_home_laundry_btn);
        cleaning_btn = root.findViewById(R.id.user_home_cleaning_btn);
        compound_btn = root.findViewById(R.id.user_home_compound_btn);
        shopping_btn = root.findViewById(R.id.user_home_shopping_btn);
        cooking_btn = root.findViewById(R.id.user_home_cooking_btn);
        carwash_btn = root.findViewById(R.id.user_home_carwash_btn);
        ImageView profilePic = root.findViewById(R.id.user_home_profile_pic);
        TextView location = root.findViewById(R.id.user_home_location);
        location.setText(sharedPreferences.getString("location", ""));
        BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
        if(sharedPreferences.getString("photo", "").equals("")){
            Glide.with(this)
                    .load(R.drawable.ic_baseline_person_24)
                    .into(profilePic);
        }else{
            Glide.with(this)
                    .load(sharedPreferences.getString("photo", ""))
                    .into(profilePic);
        }


        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navView.setSelectedItemId(R.id.navigation_profile);
            }
        });

        laundry_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectedServices.class);
                intent.putExtra("Service", "Laundry");
                startActivity(intent);
            }
        });

        cleaning_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectedServices.class);
                intent.putExtra("Service", "Cleaning");
                startActivity(intent);
            }
        });

        compound_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectedServices.class);
                intent.putExtra("Service", "Compound");
                startActivity(intent);
            }
        });

        shopping_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(getActivity(), SelectedServices.class);
                intent.putExtra("Service", "Shopping");
                startActivity(intent);*/
            }
        });

        cooking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectedServices.class);
                intent.putExtra("Service", "Cooking");
                startActivity(intent);
            }
        });

        carwash_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectedServices.class);
                intent.putExtra("Service", "Car Wash");
                startActivity(intent);
            }
        });

        return root;
    }

    public void searchServices(View v){

    }
}