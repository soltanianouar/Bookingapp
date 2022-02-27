package com.l3si.bookingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.l3si.bookingapp.Adapter.AdapterHotelFavorite;
import com.l3si.bookingapp.Model.ModelHotel;
import com.l3si.bookingapp.databinding.ActivityMainBinding;
import com.l3si.bookingapp.databinding.ActivityProfileBinding;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    //view binding
    private ActivityProfileBinding binding;
    FirebaseAuth firebaseAuth;
    //arrayliste to hold the hotels
    private ArrayList<ModelHotel> hotelArrayList;
    //adapter to set in recy
    private AdapterHotelFavorite adapterHotelFavorite;
    String hotelId,title,price;
    ImageView hotelView;
    private static final String TAG = "PROFILE_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        //get data from intent e.g hotelId
        Intent intent = getIntent();
        //hotelId = intent.getStringExtra("hotelId");

        hotelView = findViewById(R.id.hotelView);

        loadUserInfo();
        loadFavoriteHotels();
        //handle click ,sttart profile edit page
        binding.profileEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,ProfileEditActivity.class));
            }
        });
        //handle click ,go back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //setup adapter
        adapterHotelFavorite = new AdapterHotelFavorite(ProfileActivity.this,hotelArrayList);
        binding.hotelsRv.setAdapter(adapterHotelFavorite);
    }
    private void loadFavoriteHotels() {
        Intent intent = getIntent();
        //String imageUrl = intent.getStringExtra("image");
       // Glide.with(getApplicationContext()).load(imageUrl).into(hotelView);
        // init list
        hotelArrayList = new ArrayList<>();
        //load favorite hotel from database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Favorites")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        hotelArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()){
                            String hotelId = ""+ds.child("hotelId").getValue();
                            ModelHotel modelHotel = ds.getValue(ModelHotel.class);
                            //set id to model
                          //  ModelHotel modelHotel = new ModelHotel();
                             modelHotel.setId(hotelId);
                            //add model to liste
                           // hotelArrayList.add(modelHotel);
                            //String hotelView = ""+snapshot.child("url").getValue();
                            hotelArrayList.add(modelHotel);
                          //  Intent intent = getIntent();
                          //  ImageView hotelViews = findViewById(R.id.hotelView);
                          //  String imageUrl = intent.getStringExtra("image");
                          //  Glide.with(getApplicationContext()).load(hotelView).into(hotelViews);
                        }
                        adapterHotelFavorite.notifyDataSetChanged();
                        //set number of favorite hotels
                        binding.favoriteHotelCountTv.setText(""+hotelArrayList.size());//cant set int /long to texvieow so concatnate with sting
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // GET all info of user here from snapshot
                        String email = ""+snapshot.child("email").getValue();
                        String name = ""+snapshot.child("name").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();
                        String uid = ""+snapshot.child("uid").getValue();
                        String userType = ""+snapshot.child("userType").getValue();
                        // format date to dd/MM/yyyy
                        String formattedDate =MyApplication.formatTimestamp(Long.parseLong(timestamp));
                        //set  data to ui
                        binding.emailTv.setText(email);
                        binding.nameTv.setText(name);
                        binding.memberDateTv.setText(formattedDate);
                        binding.accountTypeTv.setText(userType);
                        //set image , using glid
                        Glide.with(ProfileActivity.this)
                                .load(profileImage)
                                .placeholder(R.drawable.ic_person_gray)
                                .into(binding.progileIv);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}