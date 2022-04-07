package com.dlc.nana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class createServiceAccount extends AppCompatActivity {

    private Map<String, Object> selected_services = new HashMap<>();
    private EditText name, email, description, id, price_2, price_1_1, price_1_2, price_1_3, price_1_4, price_3, price_4, price_5, price_6;
    private TextInputEditText password;
    private Spinner location;
    private final String[] selected_location = new String[1];
    private Spinner services;
    private ConstraintLayout cleaner_price_area, compound_price_area, laundry_price_area, shopping_price_area, carwash_price_area, cooking_price_area, prices;
    private TextView cooking, cleaner, compound, carwash, laundry, shopping;
    private Uri imagePath, uploadPath;
    private ImageView photo;
    private final int PICK_IMAGE_REQUEST = 22;
    String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE"};
    //requestPermissions(permissions, RC_PERMISSIONS);

    private static final String TAG = "Creating User Account";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference storageReference;
    String set_name, set_photo, set_email, set_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_service_account);

        db= FirebaseFirestore.getInstance();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        name = findViewById(R.id.create_service_name);
        email  = findViewById(R.id.create_service_mail);
        password = (TextInputEditText) findViewById(R.id.create_service_password);
        description = findViewById(R.id.create_service_description);
        id = findViewById(R.id.create_service_id);
        location = findViewById(R.id.create_service_location);
        services = findViewById(R.id.create_service_offered);
        price_2 = findViewById(R.id.cleaner_price);
        cleaner_price_area = findViewById(R.id.cleaner_price_area);
        price_3 = findViewById(R.id.compound_price);
        compound_price_area = findViewById(R.id.compound_price_area);
        price_5 = findViewById(R.id.laundry_price);
        laundry_price_area = findViewById(R.id.laundry_price_area);
        price_6 = findViewById(R.id.shopping_price);
        shopping_price_area = findViewById(R.id.shopping_price_area);
        price_1_1 = findViewById(R.id.carwash_price_salon);
        price_1_2 = findViewById(R.id.carwash_price_suv);
        price_1_3 = findViewById(R.id.carwash_price_bus);
        price_1_4 = findViewById(R.id.carwash_price_lorries);
        carwash_price_area = findViewById(R.id.carwash_price_area);
        price_4 = findViewById(R.id.cooking_price);
        cooking_price_area = findViewById(R.id.cooking_price_area);
        cooking = findViewById(R.id.service_offered_cooking);
        cleaner = findViewById(R.id.service_offered_cleaner);
        compound = findViewById(R.id.service_offered_compound);
        carwash = findViewById(R.id.service_offered_carwash);
        laundry = findViewById(R.id.service_offered_laundry);
        shopping = findViewById(R.id.service_offered_shopping);
        prices = findViewById(R.id.create_service_offered_prices);
        photo = findViewById(R.id.photo_view);

        ImageView back_btn = findViewById(R.id.create_service_account_back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    validatePassword(password.getText().toString());
                }
            }
        });

        ArrayAdapter<CharSequence> adapterLocation = ArrayAdapter.createFromResource(this, R.array.spinner_cities,
                android.R.layout.simple_spinner_item);
        adapterLocation.setDropDownViewResource(R.layout.simple_spinner_item);
        location.setAdapter(adapterLocation);
        location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_location[0] = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapterServices = ArrayAdapter.createFromResource(this, R.array.spinner_services,
                android.R.layout.simple_spinner_item);
        adapterServices.setDropDownViewResource(R.layout.simple_spinner_item);
        services.setAdapter(adapterServices);
        services.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!parent.getItemAtPosition(position).toString().equals("Pick Services")){
                    prices.setVisibility(View.VISIBLE);
                }
                if(parent.getItemAtPosition(position).toString().equals("Car Wash")){
                    selected_services.put("service_1", "Car Wash");
                    carwash.setVisibility(View.VISIBLE);
                    carwash_price_area.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onItemSelected: Car Wash selected");
                }
                if(parent.getItemAtPosition(position).toString().equals("Cleaner")){
                    selected_services.put("service_2", "Cleaning");
                    cleaner.setVisibility(View.VISIBLE);
                    cleaner_price_area.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onItemSelected: Cleaner added");
                }
                if(parent.getItemAtPosition(position).toString().equals("Compound")){
                    selected_services.put("service_3", "Compound");
                    compound.setVisibility(View.VISIBLE);
                    compound_price_area.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onItemSelected: Compound added");
                }
                if(parent.getItemAtPosition(position).toString().equals("Cooking")){
                    selected_services.put("service_4", "Cooking");
                    cooking.setVisibility(View.VISIBLE);
                    cooking_price_area.setVisibility(View.VISIBLE);
                }
                if(parent.getItemAtPosition(position).toString().equals("Laundry")){
                    selected_services.put("service_5", "Laundry");
                    laundry.setVisibility(View.VISIBLE);
                    laundry_price_area.setVisibility(View.VISIBLE);
                }
                if(parent.getItemAtPosition(position).toString().equals("Shopping")) {
                    selected_services.put("service_6", "Shopping");
                    shopping.setVisibility(View.VISIBLE);
                    shopping_price_area.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        carwash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeService("service_1", carwash, carwash_price_area);
            }
        });

        cleaner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeService("service_2", cleaner, cleaner_price_area);
            }
        });
        compound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeService("service_3", compound, compound_price_area);
            }
        });
        cooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeService("service_4", cooking, cooking_price_area);
            }
        });
        laundry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeService("service_5", laundry, laundry_price_area);
            }
        });
        shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeService("service_6", shopping, shopping_price_area);
            }
        });


    }

    public void removeService(String service_id, TextView view, ConstraintLayout price){
        selected_services.remove(service_id);
        view.setVisibility(View.GONE);
        price.setVisibility(View.GONE);
    }

    public void createServiceAccount(View v){
        if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty() || description.getText().toString().isEmpty() || id.getText().toString().isEmpty() || selected_services.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please fill in all the fields!", Toast.LENGTH_LONG).show();
        }
        else if(!validatePassword(password.getText().toString())){
            Toast.makeText(getApplicationContext(), "Your password is very weak!", Toast.LENGTH_LONG).show();
            password.requestFocus();
            TextView tip = findViewById(R.id.password_tip);
            tip.setVisibility(View.VISIBLE);
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
            email.requestFocus();
            Toast.makeText(getApplicationContext(), "Please provide a valid Email address!", Toast.LENGTH_LONG).show();
        }
        else if (imagePath.equals("")) {
            Toast.makeText(getApplicationContext(), "Select a profile picture", Toast.LENGTH_LONG).show();
        }
        else{
            uploadImage();
        }
    }

    public boolean validatePassword(String password){
        if(password.length()>= 8 && password.matches("(.*[A-Z].*)")){
            return true;
        }
        return false;
    }

    public void selectImage(View v){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.dlc.nana.fileprovider",
                        photoFile);
                Log.d(TAG, "createImageFile: This is the current photo URI...."+ photoURI+"////////////////////////////////////////");
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, PICK_IMAGE_REQUEST);
            }
        }
        /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);*/
    }

    String currentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  //* prefix *//*
                ".jpg",         //* suffix *//*
                storageDir      //* directory *//*
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        Log.d(TAG, "createImageFile: This is the current photo path...."+ currentPhotoPath+"////////////////////////////////////////");
        imagePath = Uri.fromFile(image);
        Log.d(TAG, "createImageFile: This is the current image path...."+ imagePath+"////////////////////////////////////////");
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            // Get the Uri of data
            //imagePath = data.getData();
            Log.d(TAG, "createImageFile: This is the current image path...."+ imagePath+"////////////////////////////////////////");
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                photo.setImageBitmap(bitmap);
                photo.setVisibility(View.VISIBLE);
                photo.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    // UploadImage method
    private void uploadImage() {
        SharedPreferences sharedPreferences = getSharedPreferences("User_Prefs", Context.MODE_PRIVATE);
        if (imagePath != null) {
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Creating Service Account...");
            progressDialog.setProgressStyle(R.style.AppTheme_DialogTheme);
            progressDialog.show();

            StorageReference ref = storageReference.child("profile_pics/" + UUID.randomUUID().toString());
            UploadTask uploadTask = ref.putFile(imagePath);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (task.isSuccessful()) {
                        //here the upload of the image finish
                    }

                    // Continue the task to get a download url
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        uploadPath = task.getResult(); //this is the download url that you need to pass to your database
                        //Pass the url to your reference
                        Log.w(TAG, "The Download url is: " + uploadPath);

                        Map<String, Object> all_services = new HashMap<>();
                        String price1_1 = "0";String price1_2 = "0";String price1_3 = "0";String price1_4 = "0";String price2 = "0", price3 = "0", price4="0", price5="0", price6="0";
                        if(!price_1_1.getText().toString().equals("")){
                            price1_1 = price_1_1.getText().toString();
                            price1_2 = price_1_2.getText().toString();
                            price1_3 = price_1_3.getText().toString();
                            price1_4 = price_1_4.getText().toString();
                        }
                        if(!price_2.getText().toString().equals("")){
                            price2 = price_2.getText().toString();
                        }
                        if(!price_3.getText().toString().equals("")){
                            price3 = price_3.getText().toString();
                        }
                        if(!price_4.getText().toString().equals("")){
                            price4 = price_4.getText().toString();
                        }
                        if(!price_5.getText().toString().equals("")){
                            price5 = price_5.getText().toString();
                        }
                        if(!price_6.getText().toString().equals("")){
                            price6 = price_6.getText().toString();
                        }
                        String[] prices = {price1_1,price2,price3,price4,price5,price6} ;
                        for(int i=0; i<6; i++){
                            int j= i+1;
                            if(selected_services.containsKey("service_"+j)){
                                Map<String, Object> thisService = new HashMap<>();
                                if(selected_services.get("service_"+j).equals("Car Wash")){
                                    Map<String, Object> carwash_types = new HashMap<>();
                                    carwash_types.put("salon", price1_1);
                                    carwash_types.put("suv", price1_2);
                                    carwash_types.put("buses", price1_3);
                                    carwash_types.put("lorries", price1_4);
                                    thisService.put("service_"+j, selected_services.get("service_"+j));
                                    thisService.put("price_"+selected_services.get("service_"+j), carwash_types);
                                }else{
                                    thisService.put("service_"+j, selected_services.get("service_"+j));
                                    thisService.put("price_"+selected_services.get("service_"+j), prices[i]);
                                }

                                all_services.put("service_"+j, thisService);
                            }
                        }

                        Map<String, Object> userService = new HashMap<>();
                        userService.put("service_type", "ordinary");
                        userService.put("name", name.getText().toString());
                        userService.put("email", email.getText().toString());
                        userService.put("description", description.getText().toString());
                        userService.put("id", id.getText().toString());
                        userService.put("photo", uploadPath.toString());
                        userService.put("location", selected_location[0]);
                        userService.put("services", all_services);
                        userService.put("avgRating", 0);
                        userService.put("service_number", "");
                        userService.put("gender", "");
                        userService.put("dob", "");
                        userService.put("token", MyFirebaseMessagingService.getToken(getApplicationContext()));

                        set_email = email.getText().toString();
                        set_name = name.getText().toString();
                        set_photo = uploadPath.toString();
                        set_location = selected_location[0];

                        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                                .addOnCompleteListener(createServiceAccount.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            // Add a new document with a generated ID
                                            db.collection("services")
                                                    .add(userService)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            addNotification();
                                                            //Add to firebase authentication
                                                            Log.d(TAG, "DocumentSnapshot added with ID: ");
                                                            Toast.makeText(getApplicationContext(), "You have been registered successfully ", Toast.LENGTH_SHORT).show();
                                                            FirebaseUser user = mAuth.getCurrentUser();
                                                            progressDialog.dismiss();

                                                            editor.putString("account_type", "service");
                                                            editor.putString("name", set_name);
                                                            editor.putString("photo", set_photo);
                                                            editor.putString("email", set_email);
                                                            editor.putString("location", set_location);
                                                            editor.commit();
                                                            //updateUI(user);
                                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                            intent.putExtra("user", user);
                                                            startActivity(intent);
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            progressDialog.dismiss();
                                                            Log.w(TAG, "Error adding document", e);
                                                        }
                                                    });

                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                            try {
                                                throw task.getException();
                                            } catch(FirebaseAuthWeakPasswordException e) {
                                                Toast.makeText(createServiceAccount.this, "Registration Failed! Set a stronger password.",
                                                        Toast.LENGTH_LONG).show();
                                                password.requestFocus();
                                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                                Toast.makeText(createServiceAccount.this, "Please provide a valid email address",
                                                        Toast.LENGTH_LONG).show();
                                            } catch(FirebaseAuthUserCollisionException e) {
                                                Toast.makeText(createServiceAccount.this, "This user already exists. Try logging in!",
                                                        Toast.LENGTH_LONG).show();
                                            } catch(Exception e) {
                                                Log.e(TAG, e.getMessage());
                                            }
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Registration failed. Please try again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    } else {
                    // Handle failures
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Image upload failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void addNotification(){
        Map<String, Object> note = new HashMap<>();
        note.put("for_email", email.getText().toString());
        note.put("from_email", "");
        note.put("from_name", "");
        note.put("from_photo", set_photo);
        note.put("msg", "Hello "+name.getText().toString()+". Welcome to Nana! Your ultimate house help assistant.");
        note.put("status", "unread");
        note.put("request", "");
        note.put("time_sent", FieldValue.serverTimestamp());

        db.collection("notification")
                .add(note)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //Add to firebase authentication
                        Log.d(TAG, "Notification added.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding notification", e);
                    }
                });
    }
}