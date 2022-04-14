package com.l3si.bookingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.l3si.bookingapp.Authentification.LoginActivity;
import com.l3si.bookingapp.Dashboard.DashboardUserActivity;
import com.l3si.bookingapp.databinding.ActivityMainBinding;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
        //view binding
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    //handle loginBtn click , start login screen
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));

            }
        });
        //handle skipBtn click , start continue without login screen
        binding.skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DashboardUserActivity.class));
            }
        });
    }

}