package com.dlc.nana.ui.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dlc.nana.Notification;
import com.dlc.nana.R;
import com.dlc.nana.Request;
import com.dlc.nana.adapters.NotificationsRecyclerAdapter;
import com.dlc.nana.adapters.RequestRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment implements NotificationsRecyclerAdapter.MyClickListener {

    protected ArrayList notificationsList;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private NotificationsRecyclerAdapter adapter;
    private RecyclerView recyclerView;

    private NotificationsViewModel notificationsViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getActivity().getSharedPreferences("User_Prefs", Context.MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.
        initDataset();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        BottomNavigationView navView2 = getActivity().findViewById(R.id.nav_view2);
        BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
        ImageView profilePic = root.findViewById(R.id.user_notification_profile_pic);
        progressBar = root.findViewById(R.id.notificationProgressBar);
        Glide.with(this)
                .load(sharedPreferences.getString("photo", ""))
                .into(profilePic);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sharedPreferences.getString("account_type", "N/A").equals("user")){
                    navView2.setSelectedItemId(R.id.provider_navigation_profile);
                }else{
                    navView.setSelectedItemId(R.id.navigation_profile);
                }
            }
        });


        if(!sharedPreferences.getString("account_type", "N/A").equals("user")){
            navView2.setVisibility(View.VISIBLE);
            View fragmentContainer = getActivity().findViewById(R.id.nav_host_fragment);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(0, 0,0, 56);
            fragmentContainer.setLayoutParams(params);
        }else{
            navView2.setVisibility(View.GONE);
        }

        recyclerView = root.findViewById(R.id.recycler_notifications);
        adapter = new NotificationsRecyclerAdapter(notificationsList, sharedPreferences.getString("account_type", ""), this);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return root;
    }

    private void initDataset() {
        notificationsList = new ArrayList();
        db.collection("notification").whereEqualTo("for_email", sharedPreferences.getString("email", "")).orderBy("time_sent", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            progressBar.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                Log.d("TAG", "onSuccess: Key = " + d.getId() + " and value is " + d.getData());
                                Notification notification = d.toObject(Notification.class);
                                db.collection("notification").document(d.getId()).update("status", "read");
                                notificationsList.add(notification);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
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

    @Override
    public void onNotificationClick(Notification notification, int position) {

    }
}