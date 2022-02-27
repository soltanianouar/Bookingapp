package com.l3si.bookingapp.AddActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.l3si.bookingapp.databinding.ActivityCategoryAddBinding;

import java.util.HashMap;

public class CategoryAddActivity extends AppCompatActivity {
    //view binding
    private ActivityCategoryAddBinding binding;
    //firebase auth
    private FirebaseAuth firebaseAuth;
    //progress dialog
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryAddBinding.inflate(getLayoutInflater());
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

        //handle click  , begin upload category
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();

            }
        });

    }
    private String category = "";
    private void validateData() {
        // before adding validate data

        // get data
        category = binding.categoryEt.getText().toString().trim();
        //validation data
        if (TextUtils.isEmpty(category)){
            Toast.makeText(this, "Please enter category...!", Toast.LENGTH_SHORT).show();
        }else {
            addCategoryFirebase();
        }
    }
    private void addCategoryFirebase() {
        progressDialog.setMessage("Adding category ...");
        progressDialog.show();
        //get timestamp
        long timestamp = System.currentTimeMillis();
        //setup data to add in db
        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("id",""+timestamp);
        hashMap.put("category",""+category);
        hashMap.put("timestamp",timestamp);
        hashMap.put("uid",""+firebaseAuth.getUid());
        // add to firebase db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // data added to db
                        progressDialog.dismiss();
                        Toast.makeText(CategoryAddActivity.this, "Category Added successfully...", Toast.LENGTH_SHORT).show();
                        //since user account is createsd to start dashborad of user
                        //startActivity(new Intent(RegisterActivity.this,DashboardUserActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // data failed adding to db
                progressDialog.dismiss();
                Toast.makeText(CategoryAddActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}