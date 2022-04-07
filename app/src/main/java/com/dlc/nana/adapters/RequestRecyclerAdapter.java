package com.dlc.nana.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dlc.nana.R;
import com.dlc.nana.Request;
import com.dlc.nana.SelectedServices;
import com.dlc.nana.Service;
import com.dlc.nana.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.security.AccessController.getContext;

public class RequestRecyclerAdapter extends RecyclerView.Adapter<RequestRecyclerAdapter.ViewHolder> {

    private static ArrayList<Request> localDataSet;
    private static TextView request_name, request_location,request_status, request_bill, request_service, request_rating, request_time, status_msg, rating_text, status_msg2;
    private static ImageView request_profile_pic;
    private static String title, selected_service_service, selected_service_price;
    private static Button reject_request_btn, accept_request_btn, withdraw_btn, search_new_btn, make_payment_btn;
    private static LinearLayout provider_buttons;
    private static ConstraintLayout user_buttons;
    private  static String account_type;
    private static FirebaseFirestore db;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */

    public interface MyClickListener {
        void onAccept(Request request, int position);
        void onReject(Request request, int position);
        void onWithdraw(Request request, int position);
        void onSearch(Request request, int position);
        void onPay(Request request, int position);
    }

    private MyClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        MyClickListener listener;
        public ViewHolder(View view, MyClickListener myClickListener) {
            super(view);

            // Define click listener for the ViewHolder's View
            request_name = view.findViewById(R.id.user_requests_service_name);
            request_location = view.findViewById(R.id.user_requests_service_location);
            request_rating = view.findViewById(R.id.user_requests_service_rating_value);
            rating_text = view.findViewById(R.id.user_requests_service_rating);
            request_status = view.findViewById(R.id.user_requests_service_status);
            request_profile_pic = view.findViewById(R.id.user_requests_service_pic);
            request_bill = view.findViewById(R.id.user_requests_service_price);
            user_buttons = view.findViewById(R.id.user_request_card_user_butttons);
            provider_buttons = view.findViewById(R.id.user_request_card_provider_butttons);
            reject_request_btn = view.findViewById(R.id.user_request_provider_reject);
            accept_request_btn = view.findViewById(R.id.user_request_provider_accept);
            withdraw_btn = view.findViewById(R.id.user_request_withdraw_btn);
            search_new_btn = view.findViewById(R.id.user_request_search_new_provider);
            make_payment_btn = view.findViewById(R.id.user_request_make_payment);
            request_service = view.findViewById(R.id.user_requests_service_service);
            request_time = view.findViewById(R.id.user_requests_service_date);
            status_msg = view.findViewById(R.id.user_request_provider_status_msg);
            status_msg2 = view.findViewById(R.id.user_request_seeker_status_msg);

            this.listener = myClickListener;
            reject_request_btn.setOnClickListener(this);
            accept_request_btn.setOnClickListener(this);
            withdraw_btn.setOnClickListener(this);
            search_new_btn.setOnClickListener(this);
            make_payment_btn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.user_request_provider_reject:
                    Log.d("checking request:", "onClick: This is the request "+localDataSet.get(getAdapterPosition())+"/////////////////////////////////////////////");
                    listener.onReject(localDataSet.get(getAdapterPosition()), getAdapterPosition());
                    break;
                case R.id.user_request_provider_accept:
                    listener.onAccept(localDataSet.get(getAdapterPosition()), getAdapterPosition());
                    break;
                case R.id.user_request_withdraw_btn:
                    listener.onWithdraw(localDataSet.get(getAdapterPosition()), getAdapterPosition());
                    break;
                case R.id.user_request_search_new_provider:
                    listener.onSearch(localDataSet.get(getAdapterPosition()), getAdapterPosition());
                    break;
                case R.id.user_request_make_payment:
                    listener.onPay(localDataSet.get(getAdapterPosition()), getAdapterPosition());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public RequestRecyclerAdapter(ArrayList<Request> dataSet, String account_type, MyClickListener myClickListener) {
        localDataSet = dataSet;
        this.account_type = account_type;
        this.listener = myClickListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RequestRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_requests_recycler_item, viewGroup, false);

        return new ViewHolder(view, this.listener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RequestRecyclerAdapter.ViewHolder viewHolder, final int position) {
        Request request = localDataSet.get(position);
        if(account_type.equals("user")){
            Glide.with(request_profile_pic.getContext())
                    .load(request.getRequest_provider_photo())
                    .into(request_profile_pic);
            request_name.setText(request.getRequest_provider_name());
            request_location.setText(request.getRequest_provider_location());
            request_status.setText(request.getRequest_status());
            request_bill.setText("Ugx "+request.getRequest_bill()+"/=");
            request_rating.setText(request.getRequest_rating());
            request_rating.setVisibility(View.GONE);
            rating_text.setVisibility(View.GONE);
            request_service.setVisibility(View.VISIBLE);
            request_service.setText(request.getRequest_service());
            request_time.setVisibility(View.GONE);
            user_buttons.setVisibility(View.VISIBLE);
            provider_buttons.setVisibility(View.GONE);
            if(request.getRequest_status().equals("Pending")){
                withdraw_btn.setVisibility(View.VISIBLE);
                make_payment_btn.setVisibility(View.GONE);
                search_new_btn.setVisibility(View.GONE);
                status_msg2.setVisibility(View.GONE);
            }else if(request.getRequest_status().equals("Rejected")){
                request_status.setTextColor(request_status.getContext().getResources().getColor(R.color.colorPrimaryDark));
                withdraw_btn.setVisibility(View.GONE);
                make_payment_btn.setVisibility(View.GONE);
                search_new_btn.setVisibility(View.VISIBLE);
                status_msg2.setVisibility(View.GONE);
            }else if(request.getRequest_status().equals("Accepted")){
                request_status.setTextColor(request_status.getContext().getResources().getColor(R.color.colorPrimaryDark));
                withdraw_btn.setVisibility(View.GONE);
                make_payment_btn.setVisibility(View.VISIBLE);
                search_new_btn.setVisibility(View.GONE);
                status_msg2.setVisibility(View.GONE);
            }else if(request.getRequest_status().equals("Paid")){
                request_status.setTextColor(Color.parseColor("#2E7D32"));
                withdraw_btn.setVisibility(View.GONE);
                make_payment_btn.setVisibility(View.GONE);
                search_new_btn.setVisibility(View.GONE);
                status_msg2.setVisibility(View.VISIBLE);
                status_msg2.setText("Your Service in on the way!");
            }
        }else if(!account_type.equals("user") && !request.getRequest_status().equals("Rejected")){
            Glide.with(request_profile_pic.getContext())
                    .load(request.getRequest_seeker_photo())
                    .into(request_profile_pic);

            request_name.setText(request.getRequest_seeker_name());
            request_location.setText(request.getRequest_seeker_location());
            request_location.setVisibility(View.VISIBLE);
            request_service.setVisibility(View.VISIBLE);
            request_service.setText(request.getRequest_service());
            request_bill.setText("Ugx "+request.getRequest_bill()+"/=");
            request_time.setText(request.getRequest_time()+" "+request.getRequest_date());
            request_time.setVisibility(View.VISIBLE);
            request_rating.setVisibility(View.GONE);
            rating_text.setVisibility(View.GONE);
            user_buttons.setVisibility(View.GONE);
            provider_buttons.setVisibility(View.VISIBLE);
            request_status.setText(request.getRequest_status());
            if(request.getRequest_status().equals("Pending")){
                reject_request_btn.setVisibility(View.VISIBLE);
                accept_request_btn.setVisibility(View.VISIBLE);
                status_msg.setVisibility(View.GONE);
            }else if(request.getRequest_status().equals("Accepted")){
                reject_request_btn.setVisibility(View.GONE);
                accept_request_btn.setVisibility(View.GONE);
                status_msg.setVisibility(View.VISIBLE);
                status_msg.setText("Pending Payment");
            }else if(request.getRequest_status().equals("Paid")){
                reject_request_btn.setVisibility(View.GONE);
                accept_request_btn.setVisibility(View.GONE);
                status_msg.setVisibility(View.VISIBLE);
                status_msg.setText("Payment has been made!");
            }
        }
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        Log.d("Service Adapter size: ", "Service: Constructor called: Service adapter size is "+localDataSet.size()+"///////////////////////////////////////////////////////////////////////");

        return localDataSet.size();
    }
}
