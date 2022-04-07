package com.dlc.nana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dlc.nana.adapters.SelectedServicesAdapter;
import com.dlc.nana.databinding.UserRequestsRecyclerItemBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SelectedServices extends AppCompatActivity implements SelectedServicesAdapter.OnServiceSelectedListener{

    private static final String TAG = "Selecting Services";
    private FirebaseFirestore db;
    protected String[] mDataset;
    private ProgressBar progressBar;
    private ArrayList<Service> selectedServicesList;
    private String title;
    private SelectedServicesAdapter adapter;
    private Query mQuery;
    private UserRequestsRecyclerItemBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_services);

        TextView selectedService = findViewById(R.id.selected_services_header);
        progressBar = findViewById(R.id.idProgressBarSelectedServices);

        ImageView back_btn = findViewById(R.id.selected_services_back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        title = intent.getStringExtra("Service");
        selectedService.setText(title);

        initDataset();

        RecyclerView recyclerView = findViewById(R.id.recycler_selected_services);
        adapter = new SelectedServicesAdapter(selectedServicesList, title, this);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SelectedServices.this));
        recyclerView.setAdapter(adapter);

    }

    private void initDataset() {
        selectedServicesList = new ArrayList();
        db = FirebaseFirestore.getInstance();
        db.collection("services").get()
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
                                Service svc = d.toObject(Service.class);

                                Log.d(TAG, "onSuccess: Service = " + svc+"/////////////////////////////////////////////////////////////////");
                                Log.d(TAG, "onSuccess: Key = " + d.getId() + " and value is " + d.getData() + ". These are all the services");

                                Map<String, Map<String, Object>> servicesList = svc.getServices();

                                Log.d(TAG, "onSuccess: These Services "+title+" = " + servicesList.containsValue(title) + " and value is " + servicesList.toString() + ". This service has been selected");

                                for(Map.Entry<String, Map<String, Object>> serviceEntrySet: servicesList.entrySet()){
                                    Map<String, Object> servicesMap = serviceEntrySet.getValue();
                                    Log.d(TAG, "onSuccess: This Service Map = " + servicesMap+"/////////////////////////////////////////////////////////////////");
                                    if(servicesMap.containsValue(title)){
                                        selectedServicesList.add(svc);
                                        Log.d(TAG, "onSuccess: This is a selected service provider = " + d.getId() + " and value is " + d.getData() + ". This service has been selected");
                                        Log.d(TAG, "onSuccess: Service = " + svc+"/////////////////////////////////////////////////////////////////");
                                    }
                                }
                            }
                            // after adding the data to recycler view.
                            // we are calling recycler view notifuDataSetChanged
                            // method to notify that data has been changed in recycler view.
                            adapter.notifyDataSetChanged();
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            Toast.makeText(SelectedServices.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // if we do not get any data or any error we are displaying
                // a toast message that we do not get any data
                Toast.makeText(SelectedServices.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onServiceSelected(Service service) {
        Log.d(TAG, "onServiceSelected: This is the car wash price "+ service.getService_name()+"/////////////////////////////////////////////////");
        Log.d(TAG, "onServiceSelected: This is the car wash price "+ service.getService_price_cars()+"/////////////////////////////////////////////////");
        Intent intent = new Intent(SelectedServices.this, SelectedServiceActivity.class);
        if(service.getService_name().equals("Car Wash")){
            Log.d(TAG, "onServiceSelected: This is the car wash price "+ service.getService_price_cars()+"/////////////////////////////////////////////////");

            intent.putExtra("service_name", service.getName())
                    .putExtra("service_email", service.getEmail())
                    .putExtra("service_location", service.getLocation())
                    .putExtra("service_description", service.getDescription())
                    .putExtra("service_id", service.getId())
                    .putExtra("service_photo", service.getPhoto())
                    .putExtra("service_rating", service.getAvgRating())
                    .putExtra("service_service", service.getService_name())
                    .putExtra("service_price", (Serializable) service.getService_price_cars())
                    .putExtra("service_token", service.getToken());
        }else{
            intent.putExtra("service_name", service.getName())
                    .putExtra("service_email", service.getEmail())
                    .putExtra("service_location", service.getLocation())
                    .putExtra("service_description", service.getDescription())
                    .putExtra("service_id", service.getId())
                    .putExtra("service_photo", service.getPhoto())
                    .putExtra("service_rating", service.getAvgRating())
                    .putExtra("service_service", service.getService_name())
                    .putExtra("service_price", service.getService_price().toString())
                    .putExtra("service_token", service.getToken());
        }
        startActivity(intent);
    }
}