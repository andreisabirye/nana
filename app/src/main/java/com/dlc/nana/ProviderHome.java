package com.dlc.nana;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ProviderHome extends AppCompatActivity {
    private TextView settings, notifications, profile, requests, logout, provider_name, provider_location;
    private ImageView provider_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_home);

        settings = findViewById(R.id.provider_home_settings_link);
        notifications = findViewById(R.id.provider_home_notifications_link);
        profile = findViewById(R.id.provider_home_profile_link);
        requests = findViewById(R.id.provider_home_requests_link);
        logout = findViewById(R.id.provider_home_logout_link);
        provider_name = findViewById(R.id.provider_home_name);
        provider_location = findViewById(R.id.provider_home_location);
        provider_photo = findViewById(R.id.provider_home_profile_pic);
    }

    public void providerSettings(View v){

    }
}