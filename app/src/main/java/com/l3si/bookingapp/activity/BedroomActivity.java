package com.l3si.bookingapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.l3si.bookingapp.Adapter.AdapterHotelRoom;
import com.l3si.bookingapp.Adapter.AdapterHotelUser;
import com.l3si.bookingapp.Model.ModelHotel;
import com.l3si.bookingapp.R;
import com.l3si.bookingapp.databinding.ActivityBedroomBinding;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

public class BedroomActivity extends AppCompatActivity {
    // view binding
    private ActivityBedroomBinding binding;
    // hotel id , get from intent
    String hotelIds,hotelId,hotelIdroms,title,price;
    ModelHotel modelHotel = null ;
    ImageView hotelView;
    TextView categoryTv,name,description,priceTv,quantity,dateTv,textLat;
    FirebaseAuth firebaseAuth;
    double lat1= 0 , long1 = 0 ,lat2 = 0 , long2 = 0;
    private ArrayList<ModelHotel> hotelArrayList;
    //adapter to set in recy
    private AdapterHotelRoom adapterHotelRoom;
    private AdapterHotelUser adapterHotelUser;
    private static final String TAG = "PROFILE_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBedroomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        if (modelHotel != null) {
            Glide.with(getApplicationContext()).load(modelHotel.getUrl()).into(hotelView);
            name.setText(modelHotel.getTitle());
            // rating.setText(popularProductsModel.getRating());
            //description.setText(popularProductsModel.getDecription());
            priceTv.setText(String.valueOf(modelHotel.getPrice()));
            // totalPrice = popularProductsModel.getPrice()* totalQuantitiy;
        }
        getcalendarpicker();
        Intent intent = getIntent();
        hotelId = intent.getStringExtra("hotelIds");
        hotelIdroms = intent.getStringExtra("hotelIdroms");
        title = intent.getStringExtra("title");
        price = intent.getStringExtra("price");

      //  String imageUrl = intent.getStringExtra("image");
        hotelView = findViewById(R.id.hotelView);
      //  Glide.with(getApplicationContext()).load(imageUrl).into(hotelView);
      //  String imageUrl = intent.getStringExtra("image");
        hotelView = findViewById(R.id.hotelView);
        //Glide.with(getApplicationContext()).load(imageUrl).into(hotelView);

        loadRoomsHotels(hotelIds);
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        adapterHotelRoom = new AdapterHotelRoom(this,hotelArrayList);
        binding.roomRv.setAdapter(adapterHotelRoom);

    }

    public void getcalendarpicker(){
        MaterialDatePicker materialDatePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setSelection(Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(),MaterialDatePicker.todayInUtcMilliseconds())).build();
        binding.CalendarBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // materialDatePicker.show(get,"");
                materialDatePicker.show(getSupportFragmentManager(), "tag");
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        binding.daterangText.setText(materialDatePicker.getHeaderText());
                    }
                });
            }

        });
    }
    private void loadRoomsHotels(String hotelIds) {
        // init list
        Log.e("hotelId_Bedroom","id:"+hotelIds);
        hotelArrayList = new ArrayList<>();
        //load favorite hotel from database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hotel");
        ref.child(hotelId).child("Rooms")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        hotelArrayList.clear();
                        Log.e("hotelId","id:"+hotelId);
                        for (DataSnapshot ds : snapshot.getChildren()){
                            ModelHotel modelHotel = ds.getValue(ModelHotel.class);
                            hotelArrayList.add(modelHotel);
                        }
                        adapterHotelRoom.notifyDataSetChanged();

                        //set number of favorite hotels
                        //setup adapter
                        /*adapterHotelRoom = new AdapterHotelRoom(getApplicationContext(),hotelArrayList);
                        binding.roomRv.setAdapter(adapterHotelRoom);*/
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}
