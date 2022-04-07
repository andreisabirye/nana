package com.dlc.nana;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProviderHomeFragment extends Fragment {
    private TextView settings, notifications, profile, requests, logout, provider_name, provider_location;
    private ImageView provider_photo;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_provider_home, container, false);
        settings = root.findViewById(R.id.provider_home_settings_link);
        notifications = root.findViewById(R.id.provider_home_notifications_link);
        profile = root.findViewById(R.id.provider_home_profile_link);
        requests = root.findViewById(R.id.provider_home_requests_link);
        logout = root.findViewById(R.id.provider_home_logout_link);
        provider_name = root.findViewById(R.id.provider_home_name);
        provider_location = root.findViewById(R.id.provider_home_location);
        provider_photo = root.findViewById(R.id.provider_home_profile_pic);

        BottomNavigationView navView = getActivity().findViewById(R.id.nav_view2);
        navView.setVisibility(View.GONE);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("User_Prefs", Context.MODE_PRIVATE);

        provider_name.setText(sharedPreferences.getString("name", "N/A"));
        provider_location.setText(sharedPreferences.getString("location", "N/A"));
        Glide.with(getActivity()).load(sharedPreferences.getString("photo", "")).into(provider_photo);

        View fragmentContainer = getActivity().findViewById(R.id.nav_host_fragment);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(0, 0,0, 0);
        fragmentContainer.setLayoutParams(params);

        requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //NavHostFragment.findNavController(ProviderHomeFragment.this).navigate(R.id.action_providerHome_to_requests);
                navView.setSelectedItemId(R.id.provider_navigation_requests);
            }
        });

        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navView.setSelectedItemId(R.id.provider_navigation_notifications);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navView.setSelectedItemId(R.id.provider_navigation_profile);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Settings.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Log.d("Signing Out", "onClick: User successfully signed out/////////////////////////////////////////////////");
            }
        });

        return root;
    }
}