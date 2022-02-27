package com.l3si.bookingapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.l3si.bookingapp.Model.ModelHotel;
import com.l3si.bookingapp.MyApplication;
import com.l3si.bookingapp.R;
import com.l3si.bookingapp.databinding.ActivityHotelDetailBinding;
import com.l3si.bookingapp.fragment.HotelUserFragment;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HotelDetailActivity extends AppCompatActivity {
    // view binding
    private ActivityHotelDetailBinding binding;
    // hotel id , get from intent
    String hotelId;
    String title;
    String price,distance;
    String etSource;
    String loc;
    double lat11;
    double long11;
    String lat22;
    String long22;

    ImageView hotelView;
    private final static int REQUEST_CODE = 100;
    FirebaseAuth firebaseAuth;
    FusedLocationProviderClient fusedLocationProviderClient;
    boolean isInMyFavorite = false;
    boolean isInMyRoom = false;
    TextView categoryTv,name,description,priceTv,quantity,dateTv;
    Button addToCart,buyNow;
     double lat1 =0 , long1=0 ;
    public double lat2,long2;
    int flag = 0;
    int totalQuantitiy =1;
    TextView lattitude,longitude,address,city,country,pinCode,textLat;
    int totalPrice = 0;
    ModelHotel modelHotel = null ;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHotelDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (modelHotel != null) {
            Glide.with(getApplicationContext()).load(modelHotel.getUrl()).into(hotelView);
            name.setText(modelHotel.getTitle());
            // rating.setText(popularProductsModel.getRating());
            //description.setText(popularProductsModel.getDecription());
            priceTv.setText(String.valueOf(modelHotel.getPrice()));
            // totalPrice = popularProductsModel.getPrice()* totalQuantitiy;
        }
        //get data from intent e.g hotelId

        Intent intent = getIntent();
        hotelId = intent.getStringExtra("hotelId");
        distance  = intent.getStringExtra("distance");
        Log.e("distance"," etSource "+distance);
        lat11 = intent.getDoubleExtra("lat1",lat1);
        long11 = intent.getDoubleExtra("long1",long1);
      // long22 = intent.getDoubleExtra("long2",+long22);
        String imageUrl = intent.getStringExtra("image");
       // etSource = intent.getStringExtra("etSource");

        //Log.e("lat1"," detaillactivity "+lat11);
        //Log.e("long1","  detaillactivity "+long11);

        hotelView = findViewById(R.id.hotelView);
        Glide.with(getApplicationContext()).load(imageUrl).into(hotelView);
        loadHotelDetails();
        name = findViewById(R.id.titleTv);
        priceTv = findViewById(R.id.priceTv);
        //addToCart = findViewById(R.id.go);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null){
            checkIsFavorite();
        }
        binding.imageView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HotelDetailActivity.this, BookActivity.class));

            }
        });
        // handle click , go to back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // handle click ,add / remove favorite
        binding.favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() == null){
                    Toast.makeText(HotelDetailActivity.this, "You're not legged in ", Toast.LENGTH_SHORT).show();
                }else  {
                    if (isInMyFavorite){
                        //in favorrite , remove from favorite
                        MyApplication.removeFromFavorite(HotelDetailActivity.this,hotelId);
                    }else  {
                        // not in favorite ,add to favorite
                        MyApplication.addToFavorite(HotelDetailActivity.this,hotelId);
                    }
                }
            }
        });
        binding.resarvtionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HotelDetailActivity.this, BedroomActivity.class);
                intent.putExtra("hotelIds",hotelId);
                intent.putExtra("title",title);
                intent.putExtra("price",price);
                intent.putExtra("image",imageUrl);
                intent.putExtra("lat1",lat11);
                intent.putExtra("long1",long11);

                startActivity(intent);
                //getLastLocation();
            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        double distanceapp = MyApplication.distance(lat11,long11,lat2,long2);
        binding.textLat.setText(String.format("%.1f Km",distanceapp));

      //  transaction.replace(R.id.fragment_single, fragInfo);
        //transaction.commit();
    }

    private void loadHotelDetails() {
        Log.e("hotelId_detail","id:"+hotelId);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hotel");
        ref.child(hotelId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get data
                        String title = ""+snapshot.child("title").getValue();
                        String description = ""+snapshot.child("description").getValue();
                        String price = ""+snapshot.child("price").getValue();
                        String categoryId = ""+snapshot.child("categoryId").getValue();
                        String hotelView = ""+snapshot.child("url").getValue();
                        String timetamp = ""+snapshot.child("timetamp").getValue();

                        MyApplication.laodCategory(
                               ""+categoryId,binding.categoryTv);
                      /*  MyApplication.laodImageFromUrlSinglePage(
                                ""+hotelView,""+title,""+price);*/
                       // MyApplication.laodImageFromUrlSinglePage(""+hotelView,binding.hotelView);
                        //set data
                        Intent intent = getIntent();
                       ImageView hotelViews = findViewById(R.id.hotelView);
                        //String imageUrl = intent.getStringExtra("image");
                        Glide.with(getApplicationContext()).load(hotelView).into(hotelViews);
                        binding.titleTv.setText(title);
                        binding.descriptionTv.setText(description);
                        binding.priceTv.setText(price);
                       // binding.hotelView.setImageURI(Uri.parse(hotelView));
                        //binding.imagetv.setText(b);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    private void checkIsFavorite(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Favorites").child(hotelId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isInMyFavorite = snapshot.exists();
                        if (isInMyFavorite){
                            //exists in favorite
                            binding.favoriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_favorite_white,0,0);
                            binding.favoriteBtn.setText("Remove Favorite");
                        }else {
                            //not exists in favorite
                            binding.favoriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_favorite_border_white,0,0);
                            binding.favoriteBtn.setText("Add Favorite");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();

    }
   /* private void getLastLocation(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null){
                                try {
                                    Geocoder geocoder = new Geocoder(HotelDetailActivity.this, Locale.getDefault());
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    lat2 = addresses.get(0).getLatitude();
                                    long2 = addresses.get(0).getLongitude();
                                    String addres = addresses.get(0).getAddressLine(0);
                                    String cityy = addresses.get(0).getLocality();
                                    String countryy = addresses.get(0).getCountryName();
                                    String pinCode = addresses.get(0).getCountryCode();
                                    Log.e("lat2"," detaillactivity "+lat2);
                                    Log.e("long2"," detaillactivity "+long2);
                                    HashMap<String , Object> hashMap = new HashMap<>();
                                    hashMap.put("lattitude",""+lat2);
                                    hashMap.put("longitude",""+long2);
                                    hashMap.put("address",""+addres);
                                    hashMap.put("city",""+cityy);
                                    hashMap.put("country",""+countryy);
                                    hashMap.put("pinCode",""+pinCode);
                                    Task<Void> ref = FirebaseDatabase.getInstance().getReference("Users")
                                            .child(firebaseAuth.getUid()).child("Location").child(countryy)
                                            .setValue(hashMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(HotelDetailActivity.this, "Added Location", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Failed to add to Location  ..."+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    //  ref.push().setValue(hashMap);
                                    // ref.push().setValue(countryy);
                                    // ref.push().setValue(addres);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });
        }else {
            askPermission();
        }
    }*/



    private void askPermission() {
        ActivityCompat.requestPermissions(HotelDetailActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
               // getLastLocation();
            }else {
                Toast.makeText(HotelDetailActivity.this,"Please provide the required permission",Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}