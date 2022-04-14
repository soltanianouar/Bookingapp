package com.l3si.bookingapp.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.l3si.bookingapp.Adapter.AdapterHotelUser;
import com.l3si.bookingapp.Adapter.AdapterRecommendation;
import com.l3si.bookingapp.Model.ModelHotel;
import com.l3si.bookingapp.MyApplication;
import com.l3si.bookingapp.databinding.FragmentHotelUserBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


public class HotelUserFragment extends Fragment {
    private String categoryId,category,distance,uid;
    private ArrayList<Float> recommendedHotels = new ArrayList<>();
    public  ArrayList<HashMap<String, Float>> prodArrayList = new ArrayList<HashMap<String,Float>>();
    double distances,lat1,long1,price;
    private AdapterHotelUser adapterHotelUser;
    private ArrayList<ModelHotel> hotelArrayList = new ArrayList<>();;
    private ArrayList<ModelHotel>  hotelrecomendedArrayList = new ArrayList<>();
    private AdapterRecommendation adapterRecommendation;
    private FragmentHotelUserBinding binding;
    double latitude,longitude;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 100;
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
            distance = getArguments().getString("distance");
            //Log.e("category"," category "+category);
            // Log.e("distance"," distance "+distance);
            //  distance = getArguments().getDouble("distance");
            //Log.e("myValue"," myValue "+distance);
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        }

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentHotelUserBinding.inflate(LayoutInflater.from(getContext()),container,false);
        if (category.equals("All")){
            //lead all
            loadAllHotels();
        }else if (category.equals("Most Viewed")){
            //load most viewd hotel
            //loadMostViewsHotels("title");
        }else if (category.equals("Recommended")){
            // load recommended hotel dfgqsdfghqsdfghsdfghjk  chat hna kol méthud dji fragment  ou alah alik berdil hatha kaml hhhhhhhhhhhhhhh kheliha hna ou 3aw habatha ltht kima kant
            loadRecommended("title");
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
   private void loadRecommended(String orderBy) {

        if(recommendedHotels.size() != 0) Log.e("fillmap","ok");
        //hotelratingArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hotel");
        ref.orderByChild(orderBy).limitToLast(10);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ref.addValueEventListener(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        hotelArrayList.clear();
                        hotelrecomendedArrayList.clear();
                        int sum_pValue =0;
                        int sum_pValues= 0 ;
                        int  avgPosReviews ,avgRéservéCount =0 ;
                        int  numOfhotelRéservé=0;
                        int numOfhotel = 0;
                        int count=0;
                        int pValuePosReviews;
                        double pValueRéservéCount;
                        for (DataSnapshot ds:snapshot.getChildren()){
                            //get data
                            ModelHotel model = ds.getValue(ModelHotel.class);
                            //add to list
                            hotelArrayList.add(model);
                            //---------------------------
                            pValuePosReviews =  model.getPosReviews();
                            pValueRéservéCount =  model.getRéservéCount();
                            if (pValuePosReviews != 0){
                                count++;
                            }
                            if (pValueRéservéCount != 0){
                                count++;
                            }
                            numOfhotel = count;
                            numOfhotelRéservé = count;
                            sum_pValue += pValuePosReviews;
                            sum_pValues += pValueRéservéCount;

                        }
                        avgPosReviews = sum_pValue/numOfhotel;
                        avgRéservéCount = sum_pValues/numOfhotelRéservé;

                    //    Log.e("total","sum_pValue  "+sum_pValue);
                     //   Log.e("total","numOfhotel "+numOfhotel);
                      //
                       // Log.e("total","sum_pValues  "+sum_pValues);
                      //  Log.e("total","numOfhotelRéservé "+numOfhotelRéservé);
                        Log.e("total","avgRéservéCount  "+avgRéservéCount);
                        Log.e("total","avgPosReviews  "+avgPosReviews);

                        //  hotelrecomendedArrayList.clear()
                        for (int i = 0; i <hotelArrayList.size() ; i++) {
                           //  Ratingavg = MyApplication.getRating(""+hotelId,getRatingtext,getRatingbar);
                         //   Log.e("prodArrayList","*//*/*/*//*/*/*"+prodArrayList.get(0));
                            price = Double.parseDouble(hotelArrayList.get(i).getPrice());
                            double ViewsCount = hotelArrayList.get(i).getViewsCount();
                            double RéservéCount = hotelArrayList.get(i).getRéservéCount();
                            double pValuePosReviewss = hotelArrayList.get(i).getPosReviews();
                            String title = hotelArrayList.get(i).getTitle();
                            distances = MyApplication.distance(hotelArrayList.get(i).getLattitude(),hotelArrayList.get(i).getLongitude(), latitude,longitude);
                            Log.e("latlong","getLattitude hotel "+hotelArrayList.get(i).getLattitude());
                            Log.e("latlong","getLongitude hotel "+hotelArrayList.get(i).getLongitude());
                            Log.e("latlong","latitude user "+latitude);
                            Log.e("latlong","longitude user "+longitude);
                            String formattedString = String.format(Locale.FRANCE,"%.02f kilometre", distances);
                            Log.e("distancestotals","--------"+formattedString);
                            /*  Log.e("ratings","*******"+Ratingavg);
                            Log.e("price","--------"+price);*/
                            // hna hab nzid rating ana
                            //hotelrecomendedArrayList.add(hotelArrayList.get(i));
                            if (distances < 46.41){
                                Log.e("distanceszs"," "+distances+" km ");
                            } if (pValuePosReviewss > avgPosReviews){
                                Log.e("demosss"," "+pValuePosReviewss);
                            }
                            if ( distances < 15.0 /*&& ViewsCount > 10 && RéservéCount >avgRéservéCount&& pValuePosReviewss > avgPosReviews*/){
                                hotelrecomendedArrayList.add(hotelArrayList.get(i));
                               // Log.e("show","Title_recommended  "+hotelrecomendedArrayList.get(i));
                                int  j=0;
                                Log.e("show"+j+1,"-------------------------------  ");
                                Log.e("show","                                     ");
                                Log.e("show","title_recommended"+title);
                                Log.e("show","distances_recommended  "+distances);
                                Log.e("show","RéservéCount_recommended   "+RéservéCount);
                                Log.e("show","pValuePosReviewss_recommended  "+pValuePosReviewss);
                                Log.e("show","                                     ");

                             //   Log.e("show","ViewsCount_recommended   "+ViewsCount);


                                Log.e("index",String.valueOf(i));

                            }else {
                       /* if (hotelrecomendedArrayList.size() == 0 ){
                        }*/
                                //Log.e("show_else",String.valueOf(distance));hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh
                            }

                        }
                        adapterRecommendation = new AdapterRecommendation(getContext(),hotelrecomendedArrayList);
                        binding.hotelRv.setAdapter(adapterRecommendation);
                        //  adapterHotelUser = new AdapterHotelUser(getContext(),hotelArrayList);
                        // binding.hotelRv.setAdapter(adapterHotelUser);


                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        },3500);

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
                    ModelHotel model = ds.getValue(ModelHotel.class);
                    hotelArrayList.add(model); }
                adapterHotelUser = new AdapterHotelUser(getContext(),hotelArrayList);
                binding.hotelRv.setAdapter(adapterHotelUser);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void getLastLocation(){
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onSuccess(Location location) {
                            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                latitude = addresses.get(0).getLatitude();
                                longitude = addresses.get(0).getLongitude();
                                Log.e("latitude","fragment -- "+latitude);
                                Log.e("latitude","fragment -- "+longitude);
                                //loadRecommended("title");
                            } catch (IOException e) {
                                e.printStackTrace();
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
              //
                //
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
       //getLastLocation();
        getLastLocation();
      // Log.e("distance","start"+distance);
        //getLocationHotel();
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public  void fillArray(HashMap map){
        prodArrayList.add(map);
        prodArrayList.forEach((val)->{
            Float rating = Float.parseFloat(String.valueOf(val.get("rating")));;
            if (rating >= 3.0f ){
                recommendedHotels.add(val.get("hotelId"));
                recommendedHotels.add(val.get("rating"));

               // Log.e("fillmap", "Recommended Hotel: "+val.get("hotelId"));
            }

            for (int j = 0; j <recommendedHotels.size() ; j++) {
               // Log.e("List", "Recommended Hotel: "+recommendedHotels.get(j));

            }
            for (int j = 0; j <hotelArrayList.size() ; j++) {

                //Log.e("List", "ok ");
            }

        });
        if(recommendedHotels.size() != 0){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                   // Log.e("fillmap","---"+hotelArrayList.get(0));
                    // ay fragmant
                }
            },3000);

        }
        try {
           /* for (int j = 0; j <prodArrayList.size() ; j++) {
                Log.e("fillmap", ""+prodArrayList.get(j).get("rating").getClass());

            }*/
        }catch (ClassCastException e){
            e.printStackTrace();
        }


//prodArrayList.get(j).get("rating")

    }
}