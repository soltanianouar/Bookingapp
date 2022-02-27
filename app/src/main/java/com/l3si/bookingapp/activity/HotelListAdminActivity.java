package com.l3si.bookingapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.l3si.bookingapp.Adapter.AdapterHotelAdmin;
import com.l3si.bookingapp.Model.ModelHotel;
import com.l3si.bookingapp.databinding.ActivityHotelListAdminBinding;

import java.util.ArrayList;

public class HotelListAdminActivity extends AppCompatActivity {
    //view binding
    private ActivityHotelListAdminBinding binding;
    //firebase auth
    private FirebaseAuth firebaseAuth;
    //progress dialog
    private ArrayList<ModelHotel> hotelArrayList;
    private String categoryId,categoryTitle;
    private AdapterHotelAdmin adapterHotelAdmin;
    private static final String TAG = "HOTEL_LIST_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHotelListAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //get data from intant
        Intent intent = getIntent();
        categoryId = intent.getStringExtra("categoryId");
        categoryTitle = intent.getStringExtra("categoryTitle");

        //set hotel category
        binding.subTitleTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                    adapterHotelAdmin.getFilter().filter(s);
            }catch (Exception e){

            }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        loadHotelList();
        //search
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // SEARCG as and when user type each latter
                adapterHotelAdmin.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        // handle click , go to back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void loadHotelList() {
        // intit list before adding data
        hotelArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hotel");
        ref.orderByChild("categoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                            hotelArrayList.clear();
                            for (DataSnapshot ds: snapshot.getChildren()){
                                //get data
                                ModelHotel model = ds.getValue(ModelHotel.class);
                                //add to list
                                hotelArrayList.add(model);
                            }
                            //adapter
                        adapterHotelAdmin = new AdapterHotelAdmin(HotelListAdminActivity.this,hotelArrayList);
                        binding.hotelRv.setAdapter(adapterHotelAdmin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}