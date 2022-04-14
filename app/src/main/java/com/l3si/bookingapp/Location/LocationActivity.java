package com.l3si.bookingapp.Location;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.l3si.bookingapp.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
        firebaseAuth = FirebaseAuth.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();

            }
        });
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