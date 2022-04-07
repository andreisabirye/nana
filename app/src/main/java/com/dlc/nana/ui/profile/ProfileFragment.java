package com.dlc.nana.ui.profile;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dlc.nana.R;
import com.dlc.nana.SelectedServices;
import com.dlc.nana.Service;
import com.dlc.nana.Settings;
import com.dlc.nana.User;
import com.dlc.nana.login;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageReference;;
    private ProgressBar progressBar;
    private Service thisService;
    private User thisUser;
    private ImageView myProfilePic;
    private TextView myName, myLocation, logout, settings;
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("User_Prefs", Context.MODE_PRIVATE);
        BottomNavigationView navView = getActivity().findViewById(R.id.nav_view2);

        if(!sharedPreferences.getString("account_type", "N/A").equals("user")){
            navView.setVisibility(View.VISIBLE);
            View fragmentContainer = getActivity().findViewById(R.id.nav_host_fragment);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(0, 0,0, 56);
            fragmentContainer.setLayoutParams(params);
        }else{
            navView.setVisibility(View.GONE);
        }

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        String userEmail = user.getEmail().toString();
        Log.d("Checking User", "onCreateView: "+user+"-------------"+userEmail+" ////////////////////////////////////////////////////////////");
        myName = root.findViewById(R.id.my_profile_name);
        myProfilePic = root.findViewById(R.id.my_profile_photo);
        myLocation = root.findViewById(R.id.profile_location_btn);
        logout = root.findViewById(R.id.profile_logout_btn);
        settings = root.findViewById(R.id.profile_settings_btn);
        progressBar = root.findViewById(R.id.idProgressBarProfile);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Settings.class);
                startActivity(intent);
            }
        });

        db = FirebaseFirestore.getInstance();
        if(!sharedPreferences.getString("account_type", "").equals("user")){
            logout.setVisibility(View.GONE);
            db.collection("services").whereEqualTo("email", userEmail).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            // after getting the data we are calling on success method
                            // and inside this method we are checking if the received
                            // query snapshot is empty or not.
                            if (!queryDocumentSnapshots.isEmpty()) {
                                // if the snapshot is not empty we are
                                // hiding our progress bar and adding
                                // our data in a list.

                                progressBar.setVisibility(View.GONE);
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot d : list) {
                                    //Log.d(TAG, "onSuccess: Key = " + d.getId() + " and value is " + d.getData());
                                    thisService = d.toObject(Service.class);

                                    myName.setText(thisService.getName());
                                    myLocation.setText("  "+thisService.getLocation());
                                    Glide.with(myProfilePic.getContext()).load(thisService.getPhoto()).into(myProfilePic);
                                }
                            } else {
                                // if the snapshot is empty we are displaying a toast message.
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), "No data found in Database", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // if we do not get any data or any error we are displaying
                    // a toast message that we do not get any data
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            db.collection("users").whereEqualTo("user_email", userEmail).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {

                                progressBar.setVisibility(View.GONE);
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot d : list) {
                                    //Log.d(TAG, "onSuccess: Key = " + d.getId() + " and value is " + d.getData());
                                    thisUser = d.toObject(User.class);

                                    myName.setText(thisUser.getUser_name()+" "+thisUser.getUser_surname());
                                    myLocation.setText("  "+thisUser.getUser_city());
                                    Glide.with(myProfilePic.getContext()).load(thisUser.getUser_photo()).into(myProfilePic);
                                }
                            } else {
                                // if the snapshot is empty we are displaying a toast message.
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), "No data found in Database", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // if we do not get any data or any error we are displaying
                    // a toast message that we do not get any data
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
                }
            });
        }



        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel
    }

}