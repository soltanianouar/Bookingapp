package com.l3si.bookingapp.Location;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.l3si.bookingapp.MainActivity;
import com.l3si.bookingapp.Model.ModelHotel;
import com.l3si.bookingapp.R;
import com.l3si.bookingapp.activity.HotelDetailActivity;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    TextView lattitude,longitude,address,city,country,pinCode,textLat;
    Button getLocation;
    FirebaseAuth firebaseAuth;
    String sType,etSourceId,local;
    EditText etSource,etDestionation;
    double lat1= 0 , long1 = 0 ,lat2 = 0 , long2 = 0;
    int flag = 0;
    private final static int REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        lattitude = findViewById(R.id.lattitude);
        longitude = findViewById(R.id.longitude);
        address = findViewById(R.id.address);
        city = findViewById(R.id.city);
        country = findViewById(R.id.country);
        pinCode = findViewById(R.id.pinCode);
        textLat = findViewById(R.id.textLat);
        getLocation = findViewById(R.id.getLocation);
        etSource = findViewById(R.id.etSource);
        etDestionation = findViewById(R.id.etDestionation);
        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();

        etSourceId = intent.getStringExtra("etSource");
        Log.e("etSource","etSource "+etSourceId);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();

            }
        });
        //initialize places
        Places.initialize(getApplicationContext(),"AIzaSyDXLVwPlWJHoc-bl5cXAQCG_iDsNqVL1CM");
        // set edit text non focusable
        etSource.setFocusable(false);
        etSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //define type
               sType = "source";
                //initialize place field list
                List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG);
                //Creat intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fields).build(LocationActivity.this);
                //start activity result
                startActivityForResult(intent,100);
            }
        });
        // set edit text non focusable
        etDestionation.setFocusable(false);
        etDestionation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //define type
                sType = "destination";
                //initialize place field list
                List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS,Place.Field.LAT_LNG);
                //Creat intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fields).build(LocationActivity.this);
                //start activity result
                startActivityForResult(intent,100);
                ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {


                    }
                });

            }
        });
        // set text on text view
        textLat.setText("0.0 Kilometers");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //check conditon
        if (requestCode == 100 && resultCode == RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            if (sType.equals("source")){
                flag++;
                etDestionation.setText(place.getAddress());
                String sDestionation = String.valueOf(place.getLatLng());
                sDestionation = sDestionation.replaceAll("lat/lng","");
                sDestionation = sDestionation.replace("(","");
                sDestionation = sDestionation.replace(")","");
                String [] split = sDestionation.split(",");
                lat2 = Double.parseDouble(split[0]);
                long2 = Double.parseDouble(split[1]);
                }else {
                flag++;
                etSource.setText(place.getAddress());
                String sSource = String.valueOf(place.getLatLng());
                sSource = sSource.replaceAll("lat/lng","");
                sSource = sSource.replace("(","");
                sSource = sSource.replace(")","");
                String [] split = sSource.split(",");
                lat1 = Double.parseDouble(split[0]);
                long1 = Double.parseDouble(split[1]);
            }
            if (flag >= 2){
                distance(lat1,long1,lat2,long2);

            }
        }else if (requestCode == AutocompleteActivity.RESULT_ERROR){
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }
     void distance(double lat1, double long1, double lat2, double long2) {
        //calculate longitude differtnce
        double longDiff = long1 - long2;
        //calculte distance
        double distance = Math.sin(deg2rad(lat1))
                *Math.sin(deg2rad(lat2))
                +Math.cos(deg2rad(lat1))
                *Math.cos(deg2rad(lat2))
                *Math.cos(deg2rad(longDiff));
        distance = Math.acos(distance);
        //convert distance radion to degree
        distance = rad2deg(distance);
        //distance in miles
        distance = distance*60* 1.1515;
        //distance in kilometre
        distance = distance * 1.609344;
        //set distance en text
        textLat.setText(String.format(Locale.UK,"%2f Kilometres",distance));

    }
    // convert radion to degree
    private double rad2deg(double distance) {
        return (distance*180.0 / Math.PI);
    }
    // convert degree to radian
    private double deg2rad(double lat1) {
        return (lat1*Math.PI/180.0);
    }


    private void getLastLocation(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null){
                                try {
                                    Geocoder geocoder = new Geocoder(LocationActivity.this, Locale.getDefault());
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    lattitude.setText(" "+addresses.get(0).getLatitude());
                                    longitude.setText(" "+addresses.get(0).getLongitude());
                                    address.setText(" "+addresses.get(0).getAddressLine(0));
                                    city.setText(" "+addresses.get(0).getLocality());
                                    country.setText(" "+addresses.get(0).getCountryName());
                                    pinCode.setText(" "+addresses.get(0).getCountryCode());
                                    double lat2 = addresses.get(0).getLatitude();
                                    double long2 = addresses.get(0).getLongitude();
                                    String addres = addresses.get(0).getAddressLine(0);
                                    String cityy = addresses.get(0).getLocality();
                                    String countryy = addresses.get(0).getCountryName();
                                    String pinCode = addresses.get(0).getCountryCode();

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
                                                    Toast.makeText(LocationActivity.this, "Added Location", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                  Toast.makeText(getApplicationContext(), "Failed to add to Location  ..."+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                  //  openintent();
                                  //  ref.push().setValue(hashMap);
                                   // ref.push().setValue(countryy);
                                   // ref.push().setValue(addres);
                                  //  Log.e("cityy","cityy"+cityy);
                                  //  Log.e("countryy","countryy"+countryy);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });
        }else {
            askPermission();
        }
    }
    private void openintent(){
        Intent intent = new Intent(LocationActivity.this, HotelDetailActivity.class);
        intent.putExtra("lat2",lat2);
        intent.putExtra("long2",long2);
        startActivity(intent);
    }
    private void askPermission() {
        ActivityCompat.requestPermissions(LocationActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else {
                Toast.makeText(LocationActivity.this,"Please provide the required permission",Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}