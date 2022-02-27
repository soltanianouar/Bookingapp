package com.l3si.bookingapp.Dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.l3si.bookingapp.Adapter.AdapterCategory;
import com.l3si.bookingapp.AddActivity.CategoryAddActivity;
import com.l3si.bookingapp.AddActivity.HotelAddActivity;
import com.l3si.bookingapp.MainActivity;
import com.l3si.bookingapp.Model.ModelCategory;
import com.l3si.bookingapp.ProfileActivity;
import com.l3si.bookingapp.databinding.ActivityDashboardAdminBinding;

import java.util.ArrayList;

public class DashboardAdminActivity extends AppCompatActivity {
    //view binding
    private ActivityDashboardAdminBinding binding;
    //firebase auth
    private FirebaseAuth firebaseAuth;
    // arraylist to stare category
    private ArrayList<ModelCategory> categoryArrayList;
    //addapter
    private AdapterCategory adapterCategory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadCategoies();
        binding.profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardAdminActivity.this, ProfileActivity.class));
            }
        });
        //edit text change listern sreach
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // called as and when , user each letter
                try {
                    adapterCategory.getFilter().filter(s);
                }catch (Exception e ){

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //handle click , lougout
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();

            }
        });
        //handle click , start category add scrann
        binding.addCategoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardAdminActivity.this, CategoryAddActivity.class));

            }
        });
        // handle click , start hotel add +
        binding.addhotelFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardAdminActivity.this, HotelAddActivity.class));

            }
        });
    }

    private void loadCategoies() {
        // init arraylist
        categoryArrayList = new ArrayList<>();
        // get all category from db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            // clear arrayliste from db
                for (DataSnapshot ds: snapshot.getChildren() ){
                    //get data
                    ModelCategory model = ds.getValue(ModelCategory.class);
                    //add to arraylist
                    categoryArrayList.add(model);
                }
                // setup adapter
                adapterCategory = new AdapterCategory(DashboardAdminActivity.this,categoryArrayList);
                binding.categoriesRv.setAdapter(adapterCategory);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkUser() {
        // get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null ){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else {
            //logged in get user info
           String email = firebaseUser.getEmail();
           // set in textview of toolbar
            binding.subTitleTv.setText(email);
    }
    }
}