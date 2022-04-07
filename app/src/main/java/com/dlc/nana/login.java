package com.dlc.nana;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class login extends AppCompatActivity {
    private static final String TAG = "Login User";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ConstraintLayout login_area, loginProgress;
    SharedPreferences sharedPreferences;
    private User thisUser;
    private Service thisService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("User_Prefs", Context.MODE_PRIVATE);

        EditText username = findViewById(R.id.login_email);
        EditText pass =findViewById(R.id.login_password);
        Button submit = findViewById(R.id.btn_signin);
        TextView register = findViewById(R.id.text_start_registration);
        login_area = findViewById(R.id.login_area);
        loginProgress = findViewById(R.id.loginProgress);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegistration();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser(username.getText().toString(), pass.getText().toString());
            }
        });
    }

    public void startRegistration(){
        Intent intent = new Intent(login.this, chooseAccount.class);
        startActivity(intent);
    }

    public void signInUser(String email, String password){
        if(!(email.equals("") || password.equals(""))) {
            loginProgress.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                db.collection("users").whereEqualTo("user_email", user.getEmail()).get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                if (!queryDocumentSnapshots.isEmpty()) {

                                                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                                    for (DocumentSnapshot d : list) {
                                                        //Log.d(TAG, "onSuccess: Key = " + d.getId() + " and value is " + d.getData());
                                                        thisUser = d.toObject(User.class);
                                                        Log.d(TAG, "onSuccess: This user's details " + d.getData() + "//////////////////////////////////////////////");
                                                        editor.putString("account_type", "user");
                                                        editor.putString("name", thisUser.getUser_name() + " " + thisUser.getUser_surname());
                                                        editor.putString("photo", thisUser.getUser_photo());
                                                        editor.putString("email", user.getEmail());
                                                        editor.putString("location", thisUser.getUser_city());
                                                        editor.commit();

                                                        db.collection("users").document(d.getId()).update("token", MyFirebaseMessagingService.getToken(getApplicationContext()));
                                                    }
                                                    loginProgress.setVisibility(View.GONE);
                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                    intent.putExtra("account_type", "user");
                                                    intent.putExtra("email", user.getEmail());
                                                    startActivity(intent);
                                                } else {
                                                    db.collection("services").whereEqualTo("email", user.getEmail()).get()
                                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                    if (!queryDocumentSnapshots.isEmpty()) {

                                                                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                                                        for (DocumentSnapshot d : list) {
                                                                            //Log.d(TAG, "onSuccess: Key = " + d.getId() + " and value is " + d.getData());
                                                                            thisService = d.toObject(Service.class);
                                                                            editor.putString("account_type", "service");
                                                                            editor.putString("name", thisService.getName());
                                                                            editor.putString("photo", thisService.getPhoto());
                                                                            editor.putString("email", user.getEmail());
                                                                            editor.putString("location", thisService.getLocation());
                                                                            editor.commit();

                                                                            db.collection("services").document(d.getId()).update("token", MyFirebaseMessagingService.getToken(getApplicationContext()));
                                                                        }
                                                                        loginProgress.setVisibility(View.GONE);
                                                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                                        intent.putExtra("account_type", "service");
                                                                        intent.putExtra("email", user.getEmail());
                                                                        startActivity(intent);
                                                                    } else {
                                                                        db.collection("business").whereEqualTo("email", user.getEmail()).get()
                                                                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                                    @Override
                                                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                                        if (!queryDocumentSnapshots.isEmpty()) {

                                                                                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                                                                                            for (DocumentSnapshot d : list) {
                                                                                                //Log.d(TAG, "onSuccess: Key = " + d.getId() + " and value is " + d.getData());
                                                                                                thisService = d.toObject(Service.class);
                                                                                                editor.putString("account_type", "business");
                                                                                                editor.putString("name", thisService.getName());
                                                                                                editor.putString("photo", thisService.getPhoto());
                                                                                                editor.putString("email", user.getEmail());
                                                                                                editor.putString("location", thisService.getLocation());
                                                                                                editor.commit();

                                                                                                db.collection("business").document(d.getId()).update("token", MyFirebaseMessagingService.getToken(getApplicationContext()));
                                                                                            }
                                                                                            loginProgress.setVisibility(View.GONE);
                                                                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                                                            intent.putExtra("account_type", "business");
                                                                                            intent.putExtra("email", user.getEmail());
                                                                                            startActivity(intent);
                                                                                        } else {
                                                                                            // if the snapshot is empty we are displaying a toast message.
                                                                                            Snackbar.make(login_area, "No data found in Database. Please try logging in again!", Snackbar.LENGTH_LONG);
                                                                                            Toast.makeText(getApplicationContext(), "No data found in Database. Please try logging in again!", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                // if we do not get any data or any error we are displaying
                                                                                // a toast message that we do not get any data
                                                                                Toast.makeText(getApplicationContext(), "Fail to get business data.", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // if we do not get any data or any error we are displaying
                                                            // a toast message that we do not get any data
                                                            Toast.makeText(getApplicationContext(), "Fail to get Service data.", Toast.LENGTH_SHORT).show();
                                                            loginProgress.setVisibility(View.GONE);
                                                        }
                                                    });
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // if we do not get any data or any error we are displaying
                                        // a toast message that we do not get any data
                                        Toast.makeText(getApplicationContext(), "Fail to get User data.", Toast.LENGTH_SHORT).show();
                                        loginProgress.setVisibility(View.GONE);
                                    }
                                });
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    Toast.makeText(getApplicationContext(), "This user does not exist. Please Sign in instead.", Toast.LENGTH_LONG).show();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    Toast.makeText(getApplicationContext(), "Incorrect password. Please try a different one.", Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }
                                loginProgress.setVisibility(View.GONE);
                                //updateUI(null);
                            }
                        }
                    });
        }else{
            Toast.makeText(getApplicationContext(), "Please provide an email and password!", Toast.LENGTH_LONG).show();
        }
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
}