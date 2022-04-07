
package com.dlc.nana.ui.requests;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dlc.nana.MainActivity;
import com.dlc.nana.R;
import com.dlc.nana.Request;
import com.dlc.nana.SelectedServiceActivity;
import com.dlc.nana.SelectedServices;
import com.dlc.nana.Service;
import com.dlc.nana.adapters.RequestRecyclerAdapter;
import com.dlc.nana.adapters.SelectedServicesAdapter;
import com.dlc.nana.databinding.UserRequestsRecyclerItemBinding;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RaveUiManager;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestsFragment extends Fragment implements RequestRecyclerAdapter.MyClickListener{

    private static final String TAG = "Selecting Services";
    private FirebaseFirestore db;
    protected String[] mDataset;
    private ProgressBar progressBar;
    private ArrayList<Request> requestsList;
    private String title;
    private RequestRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private Query mQuery;
    private UserRequestsRecyclerItemBinding mBinding;
    private SharedPreferences sharedPreferences;
    public static RequestsFragment newInstance() {
        return new RequestsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("User_Prefs", Context.MODE_PRIVATE);
        if(!sharedPreferences.getString("account_type", "N/A").equals("user")){
            initProviderDataset();
        }else{
            initUserDateset();
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.requests_fragment, container, false);

        sharedPreferences = getActivity().getSharedPreferences("User_Prefs", Context.MODE_PRIVATE);
        BottomNavigationView navView2 = getActivity().findViewById(R.id.nav_view2);
        BottomNavigationView navView = getActivity().findViewById(R.id.nav_view);
        progressBar = root.findViewById(R.id.requestsProgressBar);
        ImageView profilePic = root.findViewById(R.id.user_requests_profile_pic);
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

        recyclerView = root.findViewById(R.id.recycler_requests);
        adapter = new RequestRecyclerAdapter(requestsList, sharedPreferences.getString("account_type", ""), this);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return root;
    }

    private void initProviderDataset() {
        requestsList = new ArrayList();
        db = FirebaseFirestore.getInstance();
        db.collection("requests").whereEqualTo("request_provider_email", sharedPreferences.getString("email", "")).orderBy("request_id", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            progressBar.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                Log.d(TAG, "onSuccess: Key = " + d.getId() + " and value is " + d.getData());
                                Request request = d.toObject(Request.class);

                                requestsList.add(request);
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
                Toast.makeText(getActivity(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initUserDateset(){
        requestsList = new ArrayList();
        db = FirebaseFirestore.getInstance();
        db.collection("requests").whereEqualTo("request_seeker_email", sharedPreferences.getString("email", "")).orderBy("request_id", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            progressBar.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                Log.d(TAG, "onSuccess: Key = " + d.getId() + " and value is " + d.getData());
                                Request request = d.toObject(Request.class);

                                requestsList.add(request);
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
                Toast.makeText(getActivity(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAccept(Request request, int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Accept Request");
        alert.setMessage("Are you sure you want to accept this request?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.collection("requests").whereEqualTo("request_id", request.getRequest_id()).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                    for (DocumentSnapshot d : list) {
                                        //Log.d(TAG, "onSuccess: Key = " + d.getId() + " and value is " + d.getData());
                                        db.collection("requests").document(d.getId()).update("request_status", "Accepted", "request_notification_token", request.getRequest_seeker_token());
                                        Map<String, Object> note = new HashMap<>();
                                        note.put("for_email", request.getRequest_seeker_email());
                                        note.put("from_email", request.getRequest_provider_email());
                                        note.put("from_name", request.getRequest_provider_name());
                                        note.put("from_photo", request.getRequest_provider_photo());
                                        note.put("msg", request.getRequest_provider_name()+" has accepted to offer you "+request.getRequest_service()+" service. Please proceed to make your payment.");
                                        note.put("status", "unread");
                                        note.put("request", request.getRequest_id());
                                        note.put("time_sent", FieldValue.serverTimestamp());
                                        db.collection("notification").add(note);
                                        Toast.makeText(getActivity(), "Request Accepted", Toast.LENGTH_SHORT).show();
                                        if(!sharedPreferences.getString("account_type", "").equals("user")){
                                            initProviderDataset();
                                        }else{
                                            initUserDateset();
                                        }
                                        adapter.notifyItemChanged(position);
                                    }
                                } else {

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

                dialog.dismiss();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    @Override
    public void onReject(Request request, int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("'Reject Request'");
        alert.setMessage("Are you sure you are not interested in this request?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.collection("requests").whereEqualTo("request_id", request.getRequest_id()).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                    for (DocumentSnapshot d : list) {
                                        //Log.d(TAG, "onSuccess: Key = " + d.getId() + " and value is " + d.getData());
                                        db.collection("requests").document(d.getId()).update("request_status", "Rejected", "request_notification_token", request.getRequest_seeker_token());
                                        Map<String, Object> note = new HashMap<>();
                                        note.put("for_email", request.getRequest_seeker_email());
                                        note.put("from_email", request.getRequest_provider_email());
                                        note.put("from_name", request.getRequest_provider_name());
                                        note.put("from_photo", request.getRequest_provider_photo());
                                        note.put("msg", "Unfortunately "+request.getRequest_provider_name()+" is not in position to provide you services at the moment. Please find another service provider.");
                                        note.put("status", "unread");
                                        note.put("request", request.getRequest_id());
                                        note.put("time_sent", FieldValue.serverTimestamp());
                                        db.collection("notification").add(note);
                                        Toast.makeText(getActivity(), "Request Rejected", Toast.LENGTH_SHORT).show();
                                        if(!sharedPreferences.getString("account_type", "").equals("user")){
                                            initProviderDataset();
                                        }else{
                                            initUserDateset();
                                        }
                                        adapter.notifyItemChanged(position);
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

                dialog.dismiss();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    @Override
    public void onWithdraw(Request request, int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("Delete");
        alert.setMessage("Are you sure you want to withdraw your request?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.collection("requests").whereEqualTo("request_id", request.getRequest_id()).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                    for (DocumentSnapshot d : list) {
                                        //Log.d(TAG, "onSuccess: Key = " + d.getId() + " and value is " + d.getData());
                                        db.collection("requests").document(d.getId()).delete();
                                        Toast.makeText(getActivity(), "Your request has been withdrawn", Toast.LENGTH_SHORT).show();
                                        adapter.notifyItemRemoved(position);
                                    }
                                } else {

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

                dialog.dismiss();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    @Override
    public void onSearch(Request request, int position) {
        db.collection("requests").whereEqualTo("request_id", request.getRequest_id()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                //Log.d(TAG, "onSuccess: Key = " + d.getId() + " and value is " + d.getData());
                                db.collection("requests").document(d.getId()).delete();
                                Toast.makeText(getActivity(), "Your request has been withdrawn", Toast.LENGTH_SHORT).show();
                                adapter.notifyItemRemoved(position);
                            }
                        } else {

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

        Intent intent = new Intent(getActivity(), SelectedServices.class);
        intent.putExtra("Service", request.getRequest_service());
        startActivity(intent);
    }

    private Request selected_request;
    private int paid_position;
    @Override
    public void onPay(Request request, int position) {
        selected_request = request;
        paid_position = position;
        String now = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        double bill = Double.parseDouble(request.getRequest_bill());
        String txRef = "TxRef_"+now;
        new RaveUiManager(this).setAmount(bill)
                .setCurrency("Ugx")
                .setEmail(request.getRequest_seeker_email())
                .setfName(request.getRequest_seeker_name())
                .setlName(request.getRequest_seeker_name())
                .setNarration("Payment for "+request.getRequest_service()+" Service.")
                .setPublicKey("FLWPUBK-86a599ebaa32ce174b741a4cfb9d0ee1-X")
                .setEncryptionKey("697bf125e3e415cc44f1e637")
                .setTxRef(txRef)
                .setPhoneNumber("", true)
                    .acceptAccountPayments(true)
                    .acceptCardPayments(true)
                    .acceptUgMobileMoneyPayments(true)
                    .allowSaveCardFeature(true)
                    .onStagingEnv(false)
                    .withTheme(R.style.AppTheme_SignedIn)
                    .shouldDisplayFee(true)
                    .initialize();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*
         *  We advise you to do a further verification of transaction's details on your server to be
         *  sure everything checks out before providing service or goods.
         */
        Log.d(TAG, "onActivityResult: requestCode = "+requestCode+" and resultCode = "+resultCode+" and finally data is "+data+"/////////////////////////////////////////////////////////////////////////////////////////////////____________________________________________________________________________________________________________________________________________________________________________________________________________________________________________");
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            Log.d(TAG, "onActivityResult: "+message);
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                Map<String, Object> payment = new HashMap<>();
                payment.put("payment_id", data.getStringExtra("txRef"));
                payment.put("payment_amount", data.getStringExtra("amount"));
                payment.put("payment_request_id", selected_request.getRequest_id());
                payment.put("payment_seeker", selected_request.getRequest_seeker_email());
                payment.put("payment_provider", selected_request.getRequest_provider_email());
                payment.put("payment_status", data.getStringExtra("status"));

                db.collection("payments")
                        .add(payment)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                //Add to firebase authentication
                                db.collection("requests").whereEqualTo("request_id", selected_request.getRequest_id()).get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                if (!queryDocumentSnapshots.isEmpty()) {
                                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                                    for (DocumentSnapshot d : list) {
                                                        //Log.d(TAG, "onSuccess: Key = " + d.getId() + " and value is " + d.getData());
                                                        db.collection("requests").document(d.getId()).update("request_status", "Paid", "request_notification_token", selected_request.getRequest_provider_token());
                                                        Map<String, Object> note = new HashMap<>();
                                                        note.put("for_email", selected_request.getRequest_provider_email());
                                                        note.put("from_email", selected_request.getRequest_seeker_email());
                                                        note.put("from_name", selected_request.getRequest_seeker_name());
                                                        note.put("from_photo", selected_request.getRequest_seeker_photo());
                                                        note.put("msg", selected_request.getRequest_seeker_name()+" has made payment for your "+selected_request.getRequest_service()+" services and is waiting on you on "+selected_request.getRequest_date()+" "+selected_request.getRequest_time()+". Please get in touch soon.");
                                                        note.put("status", "unread");
                                                        note.put("request", selected_request.getRequest_id());
                                                        note.put("time_sent", FieldValue.serverTimestamp());
                                                        db.collection("notification").add(note);
                                                        if(!sharedPreferences.getString("account_type", "").equals("user")){
                                                            initProviderDataset();
                                                        }else{
                                                            initUserDateset();
                                                        }
                                                        adapter.notifyItemChanged(paid_position);
                                                    }
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });

                                Toast.makeText(getActivity(), "Payment Successfully received. ", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });

            }
            else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(getActivity(), "There was a problem making the payment. Please try again. ", Toast.LENGTH_LONG).show();
            }
            else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(getActivity(), "You cancelled the payment ", Toast.LENGTH_LONG).show();
            }
        }
    }
}