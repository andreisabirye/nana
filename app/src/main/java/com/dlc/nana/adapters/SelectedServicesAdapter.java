package com.dlc.nana.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dlc.nana.R;
import com.dlc.nana.databinding.UserRequestsRecyclerItemBinding;
import com.dlc.nana.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectedServicesAdapter extends RecyclerView.Adapter<SelectedServicesAdapter.ViewHolder> {

    private static ArrayList<Service> localDataSet;
    private static TextView service_name, service_location, service_status, service_price, service_rating;
    private static ImageView service_profile_pic;
    private static String title, selected_service_service, selected_service_price;
    private static Map<String, Object> selected_service_price_cars;
    private UserRequestsRecyclerItemBinding binding;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */

    public interface OnServiceSelectedListener {
        void onServiceSelected(Service service);
    }

    private OnServiceSelectedListener mListener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnServiceSelectedListener mListener;
        public ViewHolder(View view, OnServiceSelectedListener mListener) {
            super(view);
            // Define click listener for the ViewHolder's View

            service_name = view.findViewById(R.id.user_requests_service_name);
            service_location = view.findViewById(R.id.user_requests_service_location);
            service_rating = view.findViewById(R.id.user_requests_service_rating_value);
            service_status = view.findViewById(R.id.user_requests_service_status);
            service_profile_pic = view.findViewById(R.id.user_requests_service_pic);
            service_price = view.findViewById(R.id.user_requests_service_price);
            view.setOnClickListener(this);
            this.mListener = mListener;
            // Click listener
        }
        @Override
        public void onClick(View v) {
            Service service = localDataSet.get(getAdapterPosition());
            String name, email, description, location, id, photo, service_price, service_service, number, type, gender, dob, token;
            double rating;
            name = service.getName();
            email = service.getEmail();
            description = service.getDescription();
            location = service.getLocation();
            rating = service.getAvgRating();
            id = service.getId();
            photo = service.getPhoto();
            number = service.getService_number();
            type = service.getService_type();
            gender = service.getGender();
            dob = service.getDob();
            token = service.getToken();

            if(title.equals("Car Wash")){
                for(Map.Entry<String, Map<String, Object>> providerServiceSet: service.getServices().entrySet()){
                    Map<String, Object> servicesMap = providerServiceSet.getValue();
                    if(servicesMap.containsValue(title)){
                        Log.d("TAG", "onClick: this service's prices are here "+servicesMap.get("price_"+title));
                        selected_service_price_cars = (Map<String, Object>) servicesMap.get("price_"+title);
                        selected_service_service = title;
                    }
                }
            }else{
                for(Map.Entry<String, Map<String, Object>> providerServiceSet: service.getServices().entrySet()){
                    Map<String, Object> servicesMap = providerServiceSet.getValue();
                    if(servicesMap.containsValue(title)){
                        selected_service_price = servicesMap.get("price_"+title).toString();
                        selected_service_service = title;
                    }
                }
            }

            if(title.equals("Car Wash")){
                this.mListener.onServiceSelected(new Service(type,name, email, description, location, rating, id, photo, selected_service_price_cars, selected_service_service, number, gender, dob, token));
            }else{
                this.mListener.onServiceSelected(new Service(type, name, email, description, location, rating, id, photo, selected_service_price, selected_service_service, number, gender, dob, token));
            }

        }

    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public SelectedServicesAdapter(ArrayList<Service> dataSet, String title, OnServiceSelectedListener mListener) {
        localDataSet = dataSet;
        this.title = title;
        this.mListener = mListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SelectedServicesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_requests_recycler_item, viewGroup, false);

        return new ViewHolder(view, this.mListener);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(SelectedServicesAdapter.ViewHolder viewHolder, final int position) {

        Service service = localDataSet.get(position);
        String rating = ""+(int)service.getAvgRating();
        service_name.setText(service.getName());
        service_location.setText(service.getLocation());
        service_rating.setText(rating);
        Glide.with(service_profile_pic.getContext())
                .load(service.getPhoto())
                .into(service_profile_pic);
        service_status.setVisibility(View.GONE);
        for(Map.Entry<String, Map<String, Object>> providerServiceSet: service.getServices().entrySet()){
            Map<String, Object> servicesMap = providerServiceSet.getValue();
            if(servicesMap.containsValue(title)){
                if(title.equals("Car Wash")){
                    Map<String, Object> prices = new HashMap<>();
                    Log.d("TAG", "onBindViewHolder: This is the price list"+servicesMap.get("price_"+title)+"///////////////////////////////////////////////////////");
                    prices = (Map<String, Object>) servicesMap.get("price_"+title);
                    service_price.setText("UGx "+prices.get("salon")+"/pc");
                }else{
                    service_price.setText("UGx "+servicesMap.get("price_"+title)+"/pc");
                }

            }
        }


        /*HashMap services = service.getServices();
        String price = services.get("price_"+title).toString();
        service_price.setText(price);*/
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        Log.d("Service Adapter size: ", "Service: Constructor called: Service adapter size is "+localDataSet.size()+"///////////////////////////////////////////////////////////////////////");

        return localDataSet.size();
    }
}


