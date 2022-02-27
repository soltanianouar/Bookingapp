package com.l3si.bookingapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.l3si.bookingapp.Adapter.AdapterHotelFavorite;
import com.l3si.bookingapp.Adapter.AdapterHotelRoom;
import com.l3si.bookingapp.Adapter.AdapterHotelUser;
import com.l3si.bookingapp.Model.ModelHotel;
import com.l3si.bookingapp.Model.ModelRoom;
import com.l3si.bookingapp.MyApplication;
import com.l3si.bookingapp.ProfileActivity;
import com.l3si.bookingapp.ProfileEditActivity;
import com.l3si.bookingapp.R;
import com.l3si.bookingapp.databinding.ActivityBedroomBinding;
import com.l3si.bookingapp.databinding.ActivityHotelDetailBinding;

import java.util.ArrayList;
import java.util.Locale;

public class BedroomActivity extends AppCompatActivity {
    // view binding
    private ActivityBedroomBinding binding;
    // hotel id , get from intent
    String hotelIds,hotelId,title,price;
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
        Intent intent = getIntent();
        hotelId = intent.getStringExtra("hotelIds");
        title = intent.getStringExtra("title");
        price = intent.getStringExtra("price");
        String lat11 = intent.getStringExtra("lat1");
        String long11 = intent.getStringExtra("long1");
        String lat22 = intent.getStringExtra("lat2");
        String long22 = intent.getStringExtra("long2");
        Log.e("lat1"," bedroomactivity "+lat11);
        Log.e("long1"," bedroomactivity "+long11);
        Log.e("lat2"," bedroomactivity "+lat22);
        Log.e("long2"," bedroomactivity "+long22);
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
        //set distance en text
        //MyApplication.distance(textLat , lat1,long1,lat2,long2);
      //  textLat.setText(String.format(Locale.US,"%2f Kilometres",MyApplication.distance(lat1,long1,lat2,long2)));
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
