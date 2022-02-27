package com.l3si.bookingapp.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.l3si.bookingapp.Adapter.AdapterHotelUser;
import com.l3si.bookingapp.Dashboard.DashboardUserActivity;
import com.l3si.bookingapp.Model.ModelHotel;
import com.l3si.bookingapp.Model.ModelLocation;
import com.l3si.bookingapp.MyApplication;
import com.l3si.bookingapp.activity.HotelDetailActivity;
import com.l3si.bookingapp.databinding.FragmentHotelUserBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class
HotelUserFragment extends Fragment {
    private String categoryId;
    private String category,locations;
    private String uid;
    private String hotelId;
    private double distance,lat1,long1;
    private ArrayList<ModelHotel> hotelArrayList;
    private ArrayList<ModelLocation> locationArrayList;
    private AdapterHotelUser adapterHotelUser;
    private FirebaseAuth firebaseAuth;
    private FragmentHotelUserBinding binding;
    double lat2,long2;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 100;
    Context context;
    String a = "96.77784729003906";
    private  static  final  String TAG = "HOTELS_USER_TAG";
    public HotelUserFragment() {
        // Required empty public constructor
    }


    public static HotelUserFragment newInstance(String categoryId, String category, String uid ,String distance) {
        HotelUserFragment fragment = new HotelUserFragment();
        Bundle args = new Bundle();
        args.putString("categoryId", categoryId);
        args.putString("category", category);
        args.putString("uid", uid);
       // args.putDouble("distance", distance);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getString("categoryId");
            category = getArguments().getString("category");
            uid = getArguments().getString("uid");
            //lat1 = getArguments().getDouble("lat1");
           // long1 = getArguments().getDouble("long1");
            //distance = getArguments().getString("distance");
            //Log.e("category"," category "+category);
           // Log.e("distance"," distance "+distance);
          //  distance = getArguments().getDouble("distance");
            //Log.e("myValue"," myValue "+distance);
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        binding = FragmentHotelUserBinding.inflate(LayoutInflater.from(getContext()),container,false);
        if (category.equals("All")){
            //lead all 
            loadAllHotels();
        }else if (category.equals("Most Viewed")){
            //load most viewd hotel
            loadMostViewsHotels("title");
        }else if (category.equals("Recommended")){
            // load recommended hotel dfgqsdfghqsdfghsdfghjk
           // loadRecommended();
        }else {
            //load selected category hotel
            loadCaregorizedHotels();
        }
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                        adapterHotelUser.getFilter().filter(s);
                }catch (Exception e){

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return binding.getRoot();
    }
    private void loadCaregorizedHotels() {
        hotelArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hotel");
        ref.orderByChild("categoryId").equalTo(categoryId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hotelArrayList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    //get data
                    ModelHotel model = ds.getValue(ModelHotel.class);
                    //add to list
                    hotelArrayList.add(model);
                }
                adapterHotelUser = new AdapterHotelUser(getContext(),hotelArrayList);
                binding.hotelRv.setAdapter(adapterHotelUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadRecommended() {



    }
    private void loadMostViewsHotels(String orderBy) {
        hotelArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hotel");
        ref.orderByChild(orderBy).limitToLast(10);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hotelArrayList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    //get data
                    ModelHotel model = ds.getValue(ModelHotel.class);
                    //add to list
                    hotelArrayList.add(model);

                }
                adapterHotelUser = new AdapterHotelUser(getContext(),hotelArrayList);
                binding.hotelRv.setAdapter(adapterHotelUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void loadAllHotels() {
        hotelArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hotel");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hotelArrayList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    //get data
                    ModelHotel model = ds.getValue(ModelHotel.class);

                    //add to list
                    hotelArrayList.add(model);
                }
                adapterHotelUser = new AdapterHotelUser(getContext(),hotelArrayList);
                binding.hotelRv.setAdapter(adapterHotelUser);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void getLocationHotel(){
        //get data from intent e.g hotelId

      /*  hotelArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hotel");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hotelArrayList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    //get data
                    ModelHotel model = ds.getValue(ModelHotel.class);
                    model.getLongitude();
                    model.getLattitude();
                    Log.e("model"," lathotel"+model.getLongitude());
                   // Log.e("long1"," longhotel "+model);

                    //add to list
                  //  hotelArrayList.add(model);
                }
               // adapterHotelUser = new AdapterHotelUser(getContext(),hotelArrayList);
               // binding.hotelRv.setAdapter(adapterHotelUser);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });*/
    }
    private void getLastLocation(){
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null){
                                try {
                                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    double lat2 = addresses.get(0).getLatitude();
                                    double long2 = addresses.get(0).getLongitude();
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
                                   /* Task<Void> ref = FirebaseDatabase.getInstance().getReference("Users")
                                            .child(firebaseAuth.getUid()).child("Location").child(countryy)
                                            .setValue(hashMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getContext(), "Added Location", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(), "Failed to add to Location  ..."+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });*/

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
    }
    private void askPermission() {
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else {
                Toast.makeText(getContext(),"Please provide the required permission",Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onStart() {
        super.onStart();
        getLastLocation();
        //getLocationHotel();
    }
}