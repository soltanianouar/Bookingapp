package com.l3si.bookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.l3si.bookingapp.Dashboard.DashboardAdminActivity;
import com.l3si.bookingapp.Dashboard.DashboardUserActivity;

public class SplashActivity extends AppCompatActivity {
    //firebase auth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        //start main screen after 2 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                checkUser();

            }
        },2000);//2000 means 2 seconds
    }

    private void checkUser() {
        //
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null ){
            startActivity(new Intent(SplashActivity.this,MainActivity.class));
            finish();
        }else {
            //check in db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    // get user type
                    String userType = ""+snapshot.child("userType").getValue();
                    //cheack user type
                    if (userType.equals("user")){
                        //this is simple user , open usre dashborad
                        startActivity(new Intent(SplashActivity.this, DashboardUserActivity.class));
                        finish();
                    }else if (userType.equals("admin")){
                        //this is admin , open admin dashborad*
                        startActivity(new Intent(SplashActivity.this, DashboardAdminActivity.class));
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }
}