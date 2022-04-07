package com.dlc.nana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import io.grpc.okhttp.internal.Platform;

public class Settings extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private EditText fname, sname, provider_name, number, dob, description;
    private RadioGroup gender;
    private String gender_selected;
    private TextView email, save_changes;
    private Button change_password;
    private ImageView profile_pic;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    final Calendar myCalendar= Calendar.getInstance();
    private ProgressBar progressBar;
    private User thisUser;
    private Service thisService;
    private Uri imagePath, uploadPath;
    private StorageReference storageReference;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("User_Prefs", MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        if(sharedPreferences.getString("account_type", "").equals("user")){
            setContentView(R.layout.activity_settings_user);
            fname = findViewById(R.id.user_settings_fname);
            sname = findViewById(R.id.user_settings_sname);
            number = findViewById(R.id.user_settings_number);
            gender = findViewById(R.id.user_settings_gender_group);
            RadioButton r_male = findViewById(R.id.user_settings_gender_male);
            RadioButton r_female = findViewById(R.id.user_settings_gender_female);
            dob = findViewById(R.id.user_settings_dob);
            email = findViewById(R.id.user_settings_email);
            profile_pic = findViewById(R.id.user_settings_pic);
            progressBar = findViewById(R.id.settings_progress);
            save_changes = findViewById(R.id.user_settings_save);

            ImageView back_btn = findViewById(R.id.settings_back);
            back_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
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
                    dob.setText(dateFormat.format(myCalendar.getTime()));
                }
            };
            dob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("TAG", "onClick: Date clicked................/////////////////////////////////////////////////////");
                    new DatePickerDialog(Settings.this, R.style.AppTheme_DialogTheme,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            db.collection("users").whereEqualTo("user_email", sharedPreferences.getString("email", "")).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                progressBar.setVisibility(View.GONE);
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot d : list) {
                                    Log.d("TAG", "onSuccess: Key = " + d.getId() + " and value is " + d.getData());
                                    thisUser = d.toObject(User.class);

                                    fname.setText(thisUser.getUser_name());
                                    sname.setText(thisUser.getUser_surname());
                                    number.setText(thisUser.getUser_number());
                                    if(thisUser.getUser_photo() == null||thisUser.getUser_photo().equals("")){
                                        Glide.with(Settings.this).load(R.drawable.ic_baseline_person_400).into(profile_pic);
                                    }else{
                                        Glide.with(Settings.this).load(thisUser.getUser_photo()).into(profile_pic);
                                    }
                                    email.setText(thisUser.getUser_email());
                                    dob.setText(thisUser.getUser_dob());
                                    if(thisUser.getUser_gender().equals("Male")){
                                        r_male.setSelected(true);
                                    }else if(thisUser.getUser_gender().equals("Female")){
                                        r_female.setSelected(true);
                                    }
                                }
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(Settings.this, "No data found. Try Logging in again.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Settings.this, "Failed to load data. Please try again", Toast.LENGTH_SHORT).show();
                }
            });

            gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if(checkedId == R.id.user_settings_gender_male){
                        gender_selected = "Male";
                    }else if(checkedId == R.id.user_settings_gender_female){
                        gender_selected = "Female";
                    }
                }
            });

            profile_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage();
                }
            });

            save_changes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadImage("users");
                }
            });

        }else if(!sharedPreferences.getString("account_type", "").equals("user")){
            setContentView(R.layout.activity_settings);
            provider_name = findViewById(R.id.provider_settings_name);
            number = findViewById(R.id.provider_settings_number);
            gender = findViewById(R.id.provider_settings_gender_group);
            profile_pic = findViewById(R.id.provider_settings_pic);
            dob = findViewById(R.id.provider_settings_dob);
            progressBar = findViewById(R.id.settings_progress);
            email = findViewById(R.id.provider_settings_email);
            save_changes = findViewById(R.id.provider_settings_save);
            description = findViewById(R.id.provider_settings_description);
            RadioButton r_male = findViewById(R.id.provider_settings_gender_male);
            RadioButton r_female = findViewById(R.id.provider_settings_gender_female);

            DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH,month);
                    myCalendar.set(Calendar.DAY_OF_MONTH,day);
                    String myFormat="MM/dd/yy";
                    SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
                    dob.setText(dateFormat.format(myCalendar.getTime()));
                }
            };
            dob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("TAG", "onClick: Date clicked................/////////////////////////////////////////////////////");
                    new DatePickerDialog(Settings.this, R.style.AppTheme_DialogTheme,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            Log.d("TAG", "Checking preferences = " + sharedPreferences.getString("email", "") + "//////////////////////////////////////////////");
            db.collection("services").whereEqualTo("email", sharedPreferences.getString("email", "")).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {

                                progressBar.setVisibility(View.GONE);
                                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                for (DocumentSnapshot d : list) {
                                    Log.d("TAG", "onSuccess: Key = " + d.getId() + " and value is " + d.getData());
                                    thisService = d.toObject(Service.class);

                                    provider_name.setText(thisService.getName());
                                    number.setText(thisService.getService_number());
                                    if( thisService.getPhoto() == null||thisService.getPhoto().equals("")){
                                        Glide.with(Settings.this).load(R.drawable.ic_baseline_person_400).into(profile_pic);
                                    }else{
                                        Glide.with(Settings.this).load(thisService.getPhoto()).into(profile_pic);
                                    }
                                    email.setText(thisService.getEmail());
                                    dob.setText(thisService.getDob());
                                    description.setText(thisService.getDescription());
                                    if(thisService.getGender().equals("Male")){
                                        r_male.toggle();
                                    }else if(thisService.getGender().equals("Female")){
                                        r_female.toggle();
                                    }
                                }
                            } else {
                                // if the snapshot is empty we are displaying a toast message.
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(Settings.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // if we do not get any data or any error we are displaying
                    // a toast message that we do not get any data
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Settings.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                }
            });

            gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    Log.d("TAG", "onCheckedID: "+checkedId+" ///////////////////////////////////////////////////////////////////////////////////////");
                    if(checkedId == R.id.provider_settings_gender_male){
                        Log.d("TAG", "onCheckedChanged: Male selected///////////////////////////////////////////////////////////////////////////////////////");
                        gender_selected = "Male";
                    }else if(checkedId == R.id.provider_settings_gender_female){
                        Log.d("TAG", "onCheckedChanged: Female selected///////////////////////////////////////////////////////////////////////////////////////");
                        gender_selected = "Female";
                    }
                }
            });

            profile_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage();
                }
            });

            save_changes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadImage("services");
                }
            });
        }
    }

    public void selectImage(){
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
                Log.d("TAG", "createImageFile: This is the current photo URI...."+ photoURI+"////////////////////////////////////////");
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 22);
            }
        }
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
        Log.d("TAG", "createImageFile: This is the current photo path...."+ currentPhotoPath+"////////////////////////////////////////");
        imagePath = Uri.fromFile(image);
        Log.d("TAG", "createImageFile: This is the current image path...."+ imagePath+"////////////////////////////////////////");
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 22 && resultCode == RESULT_OK) {
            // Get the Uri of data
            //imagePath = data.getData();
            Log.d("TAG", "createImageFile: This is the current image path...."+ imagePath+"////////////////////////////////////////");
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                profile_pic.setImageBitmap(bitmap);
                profile_pic.setVisibility(View.VISIBLE);
                profile_pic.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    // UploadImage method
    private void uploadImage(String account_type) {
        progressBar.setVisibility(View.VISIBLE);
        if (imagePath != null) {
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
                        Log.w("TAG", "The Download url is: " + uploadPath);
                        saveChanges(sharedPreferences.getString("account_type", ""), uploadPath.toString());
                    } else {
                        // Handle failures
                        Toast.makeText(getApplicationContext(), "Image upload failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {if(account_type.equals("services")){
            saveChanges(sharedPreferences.getString("account_type", ""), thisService.getPhoto());
        }else if(account_type.equals("users")){
            saveChanges(sharedPreferences.getString("account_type", ""), thisUser.getUser_photo());
        }}
    }

    private void saveChanges(String type, String photoLink){
            if(type.equals("user")){
                db.collection("users").whereEqualTo("user_email", sharedPreferences.getString("email", "")).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                    for (DocumentSnapshot d : list) {
                                        //Log.d(TAG, "onSuccess: Key = " + d.getId() + " and value is " + d.getData());
                                        db.collection("users").document(d.getId()).update("user_fname", fname.getText().toString(), "user_surname", sname.getText().toString(), "user_number", number.getText().toString(), "user_gender", gender_selected, "user_dob", dob.getText().toString(), "user_photo", photoLink);
                                        Toast.makeText(Settings.this, "Changes Saved successfully", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }else if(type.equals("service")){

                db.collection("services").whereEqualTo("email", sharedPreferences.getString("email", "")).get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                    for (DocumentSnapshot d : list) {
                                        //Log.d(TAG, "onSuccess: Key = " + d.getId() + " and value is " + d.getData());
                                        db.collection("services").document(d.getId()).update("name", provider_name.getText().toString(), "service_number", number.getText().toString(), "dob", dob.getText().toString(), "description", description.getText().toString(), "gender", gender_selected, "photo", photoLink).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(Settings.this, "Changes Saved successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("TAG", "onFailure: "+e);
                                            }
                                        });
                                    }
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            }
    }
}