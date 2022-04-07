package com.dlc.nana;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class chooseAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_account);
        ImageButton user = findViewById(R.id.account_type_btn1);
        ImageButton service = findViewById(R.id.account_type_btn2);
        ImageButton premium = findViewById(R.id.account_type_btn3);

        ImageView back_btn = findViewById(R.id.choose_account_back);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_user_reg();
            }
        });

        service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_service_reg();
            }
        });

        premium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start_premium_reg();
            }
        });
    }

    public void start_user_reg(){
        Intent intent = new Intent(chooseAccount.this, createUserAccount.class);
        startActivity(intent);
    }

    public void start_service_reg(){
        Intent intent = new Intent(chooseAccount.this, createServiceAccount.class);
        startActivity(intent);
    }

    public void start_premium_reg(){
        Intent intent = new Intent(chooseAccount.this, create_premium_account.class);
        startActivity(intent);
    }
}