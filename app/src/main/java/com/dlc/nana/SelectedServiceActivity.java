package com.dlc.nana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dlc.nana.databinding.ActivitySelectedServiceBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.Distribution;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class SelectedServiceActivity extends AppCompatActivity {

    private static Intent intent;
    private TextView selected_service_name, selected_service_service, selected_service_rating, selected_service_price, selected_service_description, selected_service_location, selected_service_qty_text, salon_price, suv_price, bus_price, lorry_price;
    private Service service;
    private ImageView selected_service_photo;
    private Button request_service_btn;
    private ConstraintLayout user_request_modal, user_request_details, requestSent;
    private LinearLayout carwash_prices;
    private ProgressBar progressDialog;
    EditText request_date;
    EditText request_time;
    TextView service_selected;
    EditText quantity;
    ImageButton qty_minus;
    ImageButton qty_plus;
    Button make_request_btn;
    TextView request_car_text;
    Spinner request_car_type;
    String[] selected_car;
    int salon_price_selected, suv_price_selected, lorry_price_selected, bus_price_selected;

    private FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;
    Handler handler;
    final Calendar myCalendar= Calendar.getInstance();
    int service_price = 0;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_service);
        intent = getIntent();
        sharedPreferences = getSharedPreferences("User_Prefs", MODE_PRIVATE);
        db= FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        String service_name = intent.getStringExtra("service_name");
        String service_service = intent.getStringExtra("service_service");


        selected_service_name = findViewById(R.id.selected_service_name);
        selected_service_service = findViewById(R.id.selected_service_service_name);
        selected_service_rating = findViewById(R.id.selected_service_rating_value);
        selected_service_description = findViewById(R.id.selected_service_description);
        selected_service_location = findViewById(R.id.selected_service_location);
        selected_service_price = findViewById(R.id.selected_service_price);
        selected_service_photo = findViewById(R.id.thisSelectedServicePhoto);
        request_service_btn = findViewById(R.id.selected_service_request_button);
        user_request_modal = findViewById(R.id.user_make_request_modal);
        user_request_details = findViewById(R.id.user_make_request_modal_details);
        requestSent = findViewById(R.id.user_request_sent);
        progressDialog = findViewById(R.id.idProgressBarSendRequest);
        selected_service_qty_text = findViewById(R.id.request_modal_quantity_text);
        carwash_prices = findViewById(R.id.carwash_prices);
        salon_price = findViewById(R.id.salon_price);
        suv_price = findViewById(R.id.suv_price);
        bus_price = findViewById(R.id.bus_price);
        lorry_price = findViewById(R.id.lorry_price);

        ImageView back_btn = findViewById(R.id.selected_service_back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        selected_service_name.setText(intent.getStringExtra("service_name"));
        selected_service_service.setText(intent.getStringExtra("service_service"));
        String rating = ""+intent.getDoubleExtra("service_rating", 0.0);
        selected_service_rating.setText(rating);
        selected_service_description.setText(intent.getStringExtra("service_description"));
        selected_service_location.setText(intent.getStringExtra("service_location"));

        String photo_location = intent.getStringExtra("service_photo");
        Log.d("Selected Service", "onCreate: this is the photo location "+intent.getStringExtra("service_photo"));
        Glide.with(this)
                .load(photo_location)
                .into(selected_service_photo);

        request_service_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRequestServiceDialog();
            }
        });

        request_date = findViewById(R.id.request_modal_date_value);
        request_time = findViewById(R.id.request_modal_time_value);
        service_selected = findViewById(R.id.request_modal_select_service);
        quantity = findViewById(R.id.request_modal_quantity);
        qty_minus = findViewById(R.id.request_modal_quantity_dec);
        qty_plus = findViewById(R.id.request_modal_quantity_inc);
        make_request_btn = findViewById(R.id.request_modal_service_btn);
        request_car_text = findViewById(R.id.request_modal_cartype_text);
        request_car_type = findViewById(R.id.request_modal_cartype);
        selected_car = new String[1];
        selected_car[0] = "Salon Car";
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_cartypes,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        request_car_type.setAdapter(adapter);
        request_car_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_car[0] = parent.getItemAtPosition(position).toString();
                setCarPrice(selected_car[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        request_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "onClick: Time clicked................/////////////////////////////////////////////////////");
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SelectedServiceActivity.this, R.style.AppTheme_DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        request_time.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                String myFormat="MM/dd/yy";
                SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
                request_date.setText(dateFormat.format(myCalendar.getTime()));
            }
        };
        request_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "onClick: Date clicked................/////////////////////////////////////////////////////");
                new DatePickerDialog(SelectedServiceActivity.this, R.style.AppTheme_DialogTheme,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        Log.d("TAG", "Before Switch: This is the service selected"+service_name+"................/////////////////////////////////////////////////////");
        switch (service_service){
            case "Laundry":
                Log.d("TAG", "onClick: This is the laundry service selected................/////////////////////////////////////////////////////");
                selected_service_price.setText(intent.getStringExtra("service_price"));
                request_car_text.setVisibility(View.GONE);
                request_car_type.setVisibility(View.GONE);
                selected_service_qty_text.setText("Number of Cloths");
                service_price = Integer.parseInt(Objects.requireNonNull(intent.getStringExtra("service_price")));
                break;
            case "Cleaning":
                Log.d("TAG", "onClick: Checking cleaning service................/////////////////////////////////////////////////////");
                Log.d("TAG", "onClick: This is the service price "+intent.getStringExtra("service_price")+"................/////////////////////////////////////////////////////");
                selected_service_price.setText(intent.getStringExtra("service_price"));
                request_car_text.setVisibility(View.GONE);
                request_car_type.setVisibility(View.GONE);
                selected_service_qty_text.setText("Number of Rooms");
                service_price = Integer.parseInt(Objects.requireNonNull(intent.getStringExtra("service_price")));
                break;
            case "Compound":
                selected_service_price.setText(intent.getStringExtra("service_price"));
                request_car_text.setVisibility(View.GONE);
                request_car_type.setVisibility(View.GONE);
                selected_service_qty_text.setText("Compound Size (Sq Metres)");
                service_price = Integer.parseInt(Objects.requireNonNull(intent.getStringExtra("service_price")));
                break;
            case "Cooking":
                selected_service_price.setText(intent.getStringExtra("service_price"));
                request_car_text.setVisibility(View.GONE);
                request_car_type.setVisibility(View.GONE);
                selected_service_qty_text.setText("Number of People");
                service_price = Integer.parseInt(Objects.requireNonNull(intent.getStringExtra("service_price")));
                break;
            case "Car Wash":
                carwash_prices.setVisibility(View.VISIBLE);
                Log.d("TAG", "onCreate: this is the list of prices "+intent.getSerializableExtra("service_price"));
                Map<String, Object> prices_map = (Map<String, Object>) intent.getSerializableExtra("service_price");
                Log.d("TAG", "onCreate: prices map = "+prices_map);
                salon_price.setText("Salon - Ugx "+prices_map.get("salon")+"/=");
                suv_price.setText("SUV - Ugx "+prices_map.get("suv")+"/=");
                bus_price.setText("Buses - Ugx "+prices_map.get("buses")+"/=");
                lorry_price.setText("Lorries - Ugx "+prices_map.get("lorries")+"/=");
                selected_service_price.setVisibility(View.GONE);
                request_car_text.setVisibility(View.VISIBLE);
                request_car_type.setVisibility(View.VISIBLE);
                salon_price_selected = Integer.parseInt(prices_map.get("salon").toString());
                suv_price_selected = Integer.parseInt(prices_map.get("suv").toString());
                lorry_price_selected = Integer.parseInt(prices_map.get("lorries").toString());
                bus_price_selected = Integer.parseInt(prices_map.get("buses").toString());

                break;
        }

        TextView total_bill_view = findViewById(R.id.request_modal_total_bill_value);
        service_selected.setText(intent.getStringExtra("service_service"));

        final int[] total_bill = new int[1];

        qty_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(quantity.getText().toString());
                qty = qty - 1;
                total_bill[0] = qty* service_price;
                quantity.setText(String.valueOf(qty));
                total_bill_view.setText(""+total_bill[0]);
            }
        });

        qty_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(quantity.getText().toString());
                qty = qty + 1;
                total_bill[0] = qty*service_price;
                quantity.setText(String.valueOf(qty));
                total_bill_view.setText(""+total_bill[0]);
            }
        });

        make_request_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity.getText().toString().equals("0")){
                    Toast.makeText(getApplicationContext(), "Please add a quantity.", Toast.LENGTH_SHORT).show();
                }else if(request_date.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Please select a date.", Toast.LENGTH_SHORT).show();
                }else {
                    Map<String, String> request = new HashMap<>();
                    request.put("request_id", new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
                    request.put("request_seeker_email", sharedPreferences.getString("email", ""));
                    request.put("request_seeker_name", sharedPreferences.getString("name", ""));
                    request.put("request_provider_email", intent.getStringExtra("service_email"));
                    request.put("request_provider_name", intent.getStringExtra("service_name"));
                    request.put("request_service", intent.getStringExtra("service_service"));
                    request.put("request_date", request_date.getText().toString());
                    request.put("request_time", request_time.getText().toString());
                    request.put("request_bill", "" + total_bill[0]);
                    request.put("request_quantity", quantity.getText().toString());
                    request.put("request_rate", "" + service_price);
                    request.put("request_seeker_location", sharedPreferences.getString("location", ""));
                    request.put("request_provider_location", intent.getStringExtra("service_location"));
                    request.put("request_status", "Pending");
                    request.put("request_rating", intent.getStringExtra("service_rating"));
                    request.put("request_provider_photo", intent.getStringExtra("service_photo"));
                    request.put("request_seeker_photo", sharedPreferences.getString("photo", ""));
                    request.put("request_provider_token", intent.getStringExtra("service_token"));
                    request.put("request_seeker_token", MyFirebaseMessagingService.getToken(getApplicationContext()));
                    request.put("request_notification_token", intent.getStringExtra("service_token"));

                    sendRequest(request);
                }
            }
        });

    }

    private void setCarPrice(String car_type){
        Log.d("TAG", "setCarPrice: Calling set new price for "+car_type+"__________________________________________________________________________________________");
        switch (car_type){
            case "Salon Car":
                service_price = salon_price_selected;
                Log.d("TAG", "setCarPrice: Calling set new price for "+car_type+"---"+service_price+"__________________________________________________________________________________________");
                break;
            case "SUV Car":
                service_price = suv_price_selected;
                Log.d("TAG", "setCarPrice: Calling set new price for "+car_type+"---"+service_price+"__________________________________________________________________________________________");
                break;
            case "Bus":
                service_price = bus_price_selected;
                break;
            case "Lorry":
                service_price = lorry_price_selected;
                break;
        }
    }

    private void showRequestServiceDialog(){
        user_request_modal.setVisibility(View.VISIBLE);
        user_request_details.setVisibility(View.VISIBLE);
    }

    private void sendRequest(Map<String, String> request){
        progressDialog.setVisibility(View.VISIBLE);
        db.collection("requests")
                .add(request)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        addNotification();
                        //Add to firebase authentication
                        Log.d("adding request", "DocumentSnapshot added with ID: ");
                        Toast.makeText(getApplicationContext(), "Request Successfully sent!", Toast.LENGTH_SHORT).show();

                        progressDialog.setVisibility(View.GONE);
                        user_request_details.setVisibility(View.GONE);
                        requestSent.setVisibility(View.VISIBLE);
                        hideSuccessPopup();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Sending Request", "Error adding document", e);
                    }
                });
    }

    private void hideSuccessPopup(){
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                requestSent.setVisibility(View.GONE);
                user_request_modal.setVisibility(View.GONE);
            }
        }, 3000);
    }

    private void addNotification(){
        Map<String, Object> note = new HashMap<>();
        note.put("for_email", intent.getStringExtra("service_email"));
        note.put("from_email", sharedPreferences.getString("email", ""));
        note.put("from_name", sharedPreferences.getString("name", ""));
        note.put("from_photo", sharedPreferences.getString("photo", ""));
        note.put("msg", sharedPreferences.getString("name", "")+" needs your "+intent.getStringExtra("service_service")+" services on "+request_date.getText().toString()+" "+request_time.getText().toString()+" and is waiting for your response.");
        note.put("status", "unread");
        note.put("time_sent", FieldValue.serverTimestamp());

        db.collection("notification")
                .add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //Add to firebase authentication
                        Log.d("TAG", "Notification added.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding notification", e);
                    }
                });
    }
}