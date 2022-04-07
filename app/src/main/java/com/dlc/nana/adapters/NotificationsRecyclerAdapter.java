package com.dlc.nana.adapters;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dlc.nana.Notification;
import com.dlc.nana.R;
import com.dlc.nana.Request;
import com.google.apphosting.datastore.testing.DatastoreTestTrace;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NotificationsRecyclerAdapter extends RecyclerView.Adapter<NotificationsRecyclerAdapter.ViewHolder> {

    private static ArrayList<Notification> localDataSet;
    private static TextView notification_msg, time_sent;
    private static ImageView notification_profile_pic;
    private static String title, selected_service_service, selected_service_price;
    private  static String account_type;
    private static FirebaseFirestore db;

    public interface MyClickListener {
        void onNotificationClick(Notification notification, int position);
    }

    private NotificationsRecyclerAdapter.MyClickListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        NotificationsRecyclerAdapter.MyClickListener listener;
        public ViewHolder(View view, NotificationsRecyclerAdapter.MyClickListener myClickListener) {
            super(view);

            // Define click listener for the ViewHolder's View
            notification_profile_pic = view.findViewById(R.id.user_notification_service_pic);
            notification_msg = view.findViewById(R.id.user_notifications_msg);
            time_sent = view.findViewById(R.id.user_notifications_timestamp);

            this.listener = myClickListener;
        }

        @Override
        public void onClick(View v) {
            listener.onNotificationClick(localDataSet.get(getAdapterPosition()), getAdapterPosition());
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public NotificationsRecyclerAdapter(ArrayList<Notification> dataSet, String account_type, NotificationsRecyclerAdapter.MyClickListener myClickListener) {
        localDataSet = dataSet;
        this.account_type = account_type;
        this.listener = myClickListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NotificationsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.notification_recycler_item, viewGroup, false);

        return new NotificationsRecyclerAdapter.ViewHolder(view, this.listener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(NotificationsRecyclerAdapter.ViewHolder viewHolder, final int position) {
        Notification notification = localDataSet.get(position);
        notification_msg.setText(notification.getMsg());
        Glide.with(notification_profile_pic.getContext()).load(notification.getFrom_photo()).into(notification_profile_pic);
        Timestamp timestamp = notification.getTime_sent();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String this_timestamp = sdf.format(timestamp.toDate());
        time_sent.setText(this_timestamp);
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        Log.d("Service Adapter size: ", "Service: Constructor called: Service adapter size is "+localDataSet.size()+"///////////////////////////////////////////////////////////////////////");

        return localDataSet.size();
    }
}

