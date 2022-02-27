package com.l3si.bookingapp.Authentification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.l3si.bookingapp.Dashboard.DashboardUserActivity;
import com.l3si.bookingapp.Location.LocationActivity;
import com.l3si.bookingapp.Model.ModelRegestration;
import com.l3si.bookingapp.activity.HotelDetailActivity;
import com.l3si.bookingapp.databinding.ActivityRegisterBinding;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    //view binding
    private ActivityRegisterBinding binding;
    //firebase auth
    private FirebaseAuth firebaseAuth;
    //progress dialog
    private ProgressDialog progressDialog;
    String loc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        //handle click  , go back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        //handle loginBtn click , start login screen
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        validateData();

            }
        });


    }
    private String name ="",email ="",password ="",location="";
    private void validateData() {
    // Before creating account , lets do some data validation

    //get Data
    name = binding.nameEt.getText().toString().trim();
    email = binding.emailEt.getText().toString().trim();
    location = binding.locationEt.getText().toString().trim();
    password = binding.passwordEt.getText().toString().trim();
    String cPassword = binding.cpasswordEt.getText().toString().trim();

    //validation data
        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "Enter your name...", Toast.LENGTH_SHORT).show();
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Invalid email pattern...!", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Enter your password...!", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(location)){
            Toast.makeText(this, "Enter your location...!", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(cPassword)){
            Toast.makeText(this, "Confirm password...!", Toast.LENGTH_SHORT).show();
        }else if (!password.equals(cPassword)){
            Toast.makeText(this, "password doesn't match...!", Toast.LENGTH_SHORT).show();
        }else {
            createUserAccount();
        }
    }

    private void createUserAccount() {
        //show progress
        progressDialog.setMessage("Creating account ...");
        progressDialog.show();
        //create user in firevbase auth
        firebaseAuth.createUserWithEmailAndPassword(email , password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // account creation success, new add in firebase realtime database
                updateUserInfo();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // account creation failed
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserInfo() {
        progressDialog.setMessage("Saving user info ...");
        // timestamp
        long timestamp = System.currentTimeMillis();
        //get current user uid , since user is registred so we can get now
        String uid = firebaseAuth.getUid();
        ModelRegestration modelRegestration = new ModelRegestration();
        loc = modelRegestration.getLocation();
        //setup data to add in db
        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("uid",uid);
        hashMap.put("email",email);
        hashMap.put("location",location);
        hashMap.put("name",name);
        hashMap.put("profileImage","");// add empty , will do later

        hashMap.put("userType","admin");// possible values are user admin , will make admiin manually in firebase realtime database by changing this valuse
        hashMap.put("timestamp",timestamp);
        // set data to db
        Intent intent = new Intent(RegisterActivity.this, HotelDetailActivity.class);
        intent.putExtra("lat2",loc);
        //intent.putExtra("long2",long2);
        startActivity(intent);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // data added to db
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Account created...", Toast.LENGTH_SHORT).show();
                        //since user account is createsd to start dashborad of user
                        startActivity(new Intent(RegisterActivity.this, DashboardUserActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // data failed adding to db
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void openintent(){

    }
}