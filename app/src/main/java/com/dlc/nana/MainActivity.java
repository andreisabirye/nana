package com.dlc.nana;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("User_Prefs", MODE_PRIVATE);
        String account_type = sharedPreferences.getString("account_type", "user");
        BottomNavigationView navView = findViewById(R.id.nav_view);
        BottomNavigationView navView2 = findViewById(R.id.nav_view2);
        Sync sync = new Sync(call,60*1000);
        db = FirebaseFirestore.getInstance();

        Log.d("Main menu:", "onCreate: the current account is a "+account_type+" ///////////////////////////////////////////////////");

        if(account_type.equals("user")){
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            navView.setVisibility(View.VISIBLE);
            navView2.setVisibility(View.GONE);
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.navigation_home, R.id.navigation_requests, R.id.navigation_notifications, R.id.navigation_profile)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

            //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);
        }else if(account_type.equals("service") || account_type.equals("business")){
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            Log.d("Main menu:", "onCreate: the current account is a service provider ///////////////////////////////////////////////////");
            navView.setVisibility(View.GONE);
            navView2.setVisibility(View.VISIBLE);
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.provider_navigation_home, R.id.provider_navigation_requests, R.id.provider_navigation_notifications, R.id.provider_navigation_profile)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            //NavGraph navGraph = navController.getNavInflater().inflate();
            navController.setGraph(R.navigation.mobile_navigation_provider);

            //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView2, navController);
            Log.d("Main menu:", "onCreate: the current account is a service provider ///////////////////////////////////////////////////");
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String message = data.getStringExtra("response");
        Log.d("TAG", "Main Activity onActivityResult: "+message);

        Log.d("TAG", "Main Activity onActivityResult: requestCode = "+requestCode+" and resultCode = "+resultCode+" and finally data is "+data+"/////////////////////////////////////////////////////////////////////////////////////////////////____________________________________________________________________________________________________________________________________________________________________________________________________________________________________________");
    }

    final private Runnable call = new Runnable() {
        public void run() {

            db = FirebaseFirestore.getInstance();
            db.collection("notification").whereEqualTo("status", "unread").whereEqualTo("for_email", sharedPreferences.getString("email", "")).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                Notification notification =
                                        new Notification.Builder(MainActivity.this)
                                                .setContentTitle("Notifications")
                                                .setContentText("You have new notifications.")
                                                .setSmallIcon(R.drawable.logo)
                                                .build();
                                notificationManager.notify(101, notification);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("TAG", "onFailure: Failed to get new notifications");
                }
            });

            //This is where my sync code will be, but for testing purposes I only have a Log statement
            Log.v("test","this will run every minute");
            handler.postDelayed(call,60*1000);
        }
    };
    public final Handler handler = new Handler();
    public class Sync {


        Runnable task;

        public Sync(Runnable task, long time) {
            this.task = task;
            handler.removeCallbacks(task);
            handler.postDelayed(task, time);
        }
    }
}