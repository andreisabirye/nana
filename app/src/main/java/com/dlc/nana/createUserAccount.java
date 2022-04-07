package com.dlc.nana;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class createUserAccount extends AppCompatActivity {

    private static final String TAG = "Creating User Account";
    private FirebaseAuth mAuth;
    private EditText dob, pass, name, email, surname;
    private ProgressBar createUserProgress;
    final Calendar myCalendar= Calendar.getInstance();
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user_account);

        db = FirebaseFirestore.getInstance();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.create_user_email);
        pass = findViewById(R.id.create_user_password);
        EditText confirm_pass = findViewById(R.id.create_user_password_confirm);
        name = findViewById(R.id.create_user_name);
        surname = findViewById(R.id.create_user_surname);
        createUserProgress = findViewById(R.id.createUserProgress);
        dob = findViewById(R.id.create_user_dob);
        Spinner city = findViewById(R.id.create_user_city_spinner);
        final String[] selected_city = new String[1];
        Button user_signup = findViewById(R.id.create_user_submit);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_cities,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city.setAdapter(adapter);
        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_city[0] = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ImageView back_btn = findViewById(R.id.create_user_account_back);
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
                updateLabel();
            }
        };
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(createUserAccount.this, R.style.AppTheme_DialogTheme,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        user_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().equals("") || name.getText().toString().equals("") || surname.getText().toString().equals("") || dob.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Please fill out all the fields!", Toast.LENGTH_LONG).show();
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
                    Toast.makeText(getApplicationContext(), "Please provide a valid Email address!", Toast.LENGTH_LONG).show();
                }
                else if(!(pass.getText().toString().equals(confirm_pass.getText().toString()))){
                    Toast.makeText(getApplicationContext(), "The Passwords are not the same!", Toast.LENGTH_LONG).show();
                }
                else{

                    Map<String, Object> user = new HashMap<>();
                    user.put("user_email", email.getText().toString());
                    user.put("user_name", name.getText().toString());
                    user.put("user_surname", surname.getText().toString());
                    user.put("user_dob", dob.getText().toString());
                    user.put("user_city", selected_city[0]);
                    user.put("user_photo", "");
                    user.put("user_gender", "");
                    user.put("user_number", "");
                    user.put("token", MyFirebaseMessagingService.getToken(getApplicationContext()));

                    // Add a new document with a generated ID
                    createUser(user, email.getText().toString(), pass.getText().toString());
                }
            }
        });
    }
    private void updateLabel(){
        String myFormat="MM/dd/yy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        dob.setText(dateFormat.format(myCalendar.getTime()));
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //reload();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void createUser(Map<String, Object> user, String email, String password){
        SharedPreferences sharedPreferences = getSharedPreferences("User_Prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        createUserProgress.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            db.collection("users")
                                    .add(user)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            addNotification();
                                            //Add to firebase authentication
                                            editor.putString("account_type", "user");
                                            editor.putString("name", user.get("user_name")+" "+user.get("user_surname"));
                                            editor.putString("photo", user.get("user_photo").toString());
                                            editor.putString("email", user.get("user_email").toString());
                                            editor.putString("location", user.get("user_city").toString());
                                            editor.commit();

                                            createUserProgress.setVisibility(View.GONE);
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.putExtra("account_type", "user");
                                            intent.putExtra("email", user.get("user_email").toString());
                                            startActivity(intent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            createUserProgress.setVisibility(View.GONE);
                                            Log.w(TAG, "Error adding document", e);
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                Toast.makeText(createUserAccount.this, "Registration Failed! Set a stronger password.",
                                        Toast.LENGTH_LONG).show();
                                pass.requestFocus();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(createUserAccount.this, "Please provide a valid email address",
                                        Toast.LENGTH_LONG).show();
                            } catch(FirebaseAuthUserCollisionException e) {
                                Toast.makeText(createUserAccount.this, "This user already exists. Try logging in!",
                                        Toast.LENGTH_LONG).show();
                            } catch(Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                            createUserProgress.setVisibility(View.GONE);
                            Toast.makeText(createUserAccount.this, "Registration failed. Please try again",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addNotification(){
        Map<String, Object> note = new HashMap<>();
        note.put("for_email", email.getText().toString());
        note.put("from_email", "");
        note.put("from_name", "");
        note.put("from_photo", "");
        note.put("msg", "Hello "+name.getText().toString()+" "+surname.getText().toString()+". Welcome to Nana! Your ultimate house help assistant.");
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