package com.l3si.bookingapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.custom.FirebaseCustomLocalModel;
import com.google.firebase.ml.custom.FirebaseModelDataType;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInputs;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.custom.FirebaseModelInterpreterOptions;
import com.google.firebase.ml.custom.FirebaseModelOutputs;
import com.l3si.bookingapp.Model.ModelHotel;
import com.l3si.bookingapp.Model.ModelRatingBar;
import com.l3si.bookingapp.Model.Reviews;
import com.l3si.bookingapp.MyApplication;
import com.l3si.bookingapp.R;
import com.l3si.bookingapp.databinding.ActivityHotelDetailBinding;
import com.txusballesteros.widgets.FitChart;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//import com.l3si.bookingapp.Adapter.AdapterReview;

public class HotelDetailActivity extends AppCompatActivity {
    //Animation for buttons
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
    // view binding
    private ActivityHotelDetailBinding binding;
    // hotel id , get from intent
    String hotelId,title,price, distance,review_text;
    ImageView hotelView;
    private ArrayList<ModelRatingBar> reviewArraylist;
    //private AdapterReview adapterReview;
    private final static int REQUEST_CODE = 100;
    boolean isInMyFavorite = false;
    TextView  name, priceTv,posNo,negNo,posPercent,negPercent,text;
    public double lat2, long2,lat11,long11;
    public static String key;
    double latitude,longitude = 0;
    private int positivePercent,negativePercent,total,pos,neg;
    private float pos_sentiment,neg_sentiment,predicts;
    private DatabaseReference databaseReference,reference1,reference2;
    private FirebaseModelInterpreter interpreter;
    private FirebaseModelInputOutputOptions inputOutputOptions;
    FusedLocationProviderClient fusedLocationProviderClient;
    FirebaseAuth firebaseAuth;
    ModelHotel modelHotel;
   public void resetActivity(){
        posNo.setText(Integer.toString(pos));
        negNo.setText(Integer.toString(neg));
        Log.e("pos","posNo"+posNo);
        Log.e("pos","negNo"+posNo);
        total=pos+neg;
        Log.e("pos","total"+total);
        if(total>0){
            positivePercent=(int)((((double)pos)/total)*100);
            negativePercent=(int)((((double)neg)/total)*100); }
        else{
            positivePercent=0;
            negativePercent=0; }
        posPercent.setText(positivePercent+"%");
        negPercent.setText(negativePercent+"%");
        text = findViewById(R.id.text);
        final FitChart fitChart = findViewById(R.id.fitChart);
        fitChart.setMinValue(0f);
        fitChart.setMaxValue(100f);
        fitChart.setValue(positivePercent);
        text.setText(positivePercent+"%");
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHotelDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        posNo=findViewById(R.id.posNo);
        negNo=findViewById(R.id.negNo);
        posPercent=findViewById(R.id.posPercent);
        negPercent=findViewById(R.id.negPercent);
        if (modelHotel != null) {
            Glide.with(getApplicationContext()).load(modelHotel.getUrl()).into(hotelView);
            name.setText(modelHotel.getTitle());
            priceTv.setText(String.valueOf(modelHotel.getPrice()+" DA"));
        }

        //get data from intent e.g hotelId
        Intent intent = getIntent();
        hotelId = intent.getStringExtra("hotelId");
        distance = intent.getStringExtra("distances");
        Log.e("distances", " distances "+distance);
        String imageUrl = intent.getStringExtra("image");
        hotelView = findViewById(R.id.hotelView);
        Glide.with(getApplicationContext()).load(imageUrl).into(hotelView);
        loadHotelDetails();
        MyApplication.incremnetHotelViewCount(hotelId);
        MyApplication.incremnetHotelResarvationCount(hotelId,binding.resavationLabelTv);
        name = findViewById(R.id.titleTv);
        priceTv = findViewById(R.id.priceTv);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            checkIsFavorite();
        }
        binding.ratingBarYours.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                binding.textViewYourCurrentRating.setText(""+rating);
            }
        });

        binding.seeAllReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                Intent intent = new Intent(HotelDetailActivity.this, ProductReviews.class);
                total=pos+neg;
                intent.putExtra("totalReviews", Integer.toString(total));
                intent.putExtra("hotelId",hotelId);
                startActivity(intent);
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
                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(HotelDetailActivity.this, "You're not legged in ", Toast.LENGTH_SHORT).show();
                } else {
                    if (isInMyFavorite) {
                        //in favorrite , remove from favorite
                        MyApplication.removeFromFavorite(HotelDetailActivity.this, hotelId);
                    } else {
                        // not in favorite ,add to favorite
                        MyApplication.addToFavorite(HotelDetailActivity.this, hotelId);
                    }
                }
            }
        });
        // handle click , submitBtn
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                  inputData();
                  addReview();
            }
        });
        binding.resarvtionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HotelDetailActivity.this, BedroomActivity.class);
                intent.putExtra("hotelIds", hotelId);
                intent.putExtra("title", title);
                intent.putExtra("price", price);
                intent.putExtra("image", imageUrl);
                intent.putExtra("lat1", lat11);
                intent.putExtra("long1", long11);
                startActivity(intent);

            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        double distanceapp = MyApplication.distance(lat11, long11, lat2, long2);
        binding.textLat.setText(String.format("%.1f Km", distanceapp));
        Log.e("distanceapp",""+distanceapp);
        loadMyReview();
        loadMydistance();
        //Extract data from firebase using DatabaseReference
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        reference1=database.getReference("Hotel");
       databaseReference= FirebaseDatabase.getInstance().getReference("Hotel").child(hotelId);
        try { FirebaseCustomLocalModel localModel = new FirebaseCustomLocalModel.Builder()
                    .setAssetFilePath("tflite_model.tflite")
                    .build();
            FirebaseModelInterpreterOptions options = new FirebaseModelInterpreterOptions.Builder(localModel).build();
            interpreter = FirebaseModelInterpreter.getInstance(options);
            //Specifying input and output vector shapes for model
            inputOutputOptions = new FirebaseModelInputOutputOptions.Builder()
                            .setInputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 250})
                            .setOutputFormat(0, FirebaseModelDataType.FLOAT32, new int[]{1, 1})
                            .build();
//            String review_text=add_review.getText().toString();
        } catch (FirebaseMLException e) {
            e.printStackTrace();
        }

    }
    //Preprocessing the review
    public float[][] preprocess(String add_review) throws FileNotFoundException, IOException {
        Map<String, Float> word_index = new HashMap<String, Float>();
        List<Float> encode = new ArrayList<Float>();
        List<String> review = new ArrayList<String>();
        String key;
        float value;
           BufferedReader br = null;
           try { br = new BufferedReader(new InputStreamReader(getAssets().open("dictionary1.txt"), "UTF-8"));
               String line;
               while ((line = br.readLine()) != null) {
                   //process line
                   String[] embedding=line.split(" ");
                   key=embedding[0];
                   value=Integer.parseInt(embedding[1]);
                   word_index.put(key, value);

               }
           } catch (IOException e) {
               //log the exception
           } finally {
               if (br != null) {
                   try {
                       br.close();
                   } catch (IOException e) {
                       //log the exception
                   } } }
        word_index.put("<PAD>",(float)0);
        word_index.put("<START>",(float)1);
        word_index.put("<UNK>",(float)2);
        word_index.put("<UNUSED>",(float)3);
        String nline=add_review.replace(",", "").replace(".", "").replace("(", "").replace(")", "").replace(":", "").replace("\"","").replace("!", "");
        String[] reviews=nline.split(" ");
        for (int i=0;i<reviews.length;i++){
            review.add(reviews[i]);
        }
        //Encode the review after preprocessing
        encode=review_encode(review,word_index);
        float[][] final_review = new float[1][250];
        int array_count=0;
        Iterator itr=encode.iterator();
        while(itr.hasNext()){
            Float array_elements=(Float)itr.next();
            float array_element=array_elements.floatValue();
            final_review[0][array_count]=array_element;
            array_count++;
            Log.e("final_review",""+final_review);
        }
        return final_review;
    }
    //Encode the user input review according to the dictionary
    public List review_encode(List s,Map word_index){
        int length=1;
        List<Float> encoded = new ArrayList<Float>();
        encoded.add((float)1);
        Iterator itr=s.iterator();
        while(itr.hasNext()){
            String word=(String)itr.next();
            word=word.toLowerCase();
            if(word_index.containsKey(word)){
                encoded.add((Float)(word_index.get(word))); }
            else{ encoded.add((float)2); }
            length++;
        }
        for(int i=1;i<=250-length;i++){
            encoded.add((float)0);
        }
        Log.e("encoded",""+encoded);
        return encoded;
    }
    //Enter button after typing review
    public void sendToDatabase(View view) {
    }
    public void addReview() {
        //Get review
        // review_text=binding.reviewEt.getText().toString();
        review_text = ""+binding.reviewEt.getText().toString().trim();
        float[][] review= new float[1][];
        try {
            //Preprocess and encode the review
            review = preprocess(review_text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("review",""+review);
        Log.e("review_text",""+review_text);
        //Pass the encoded review through the ML model
        FirebaseModelInputs inputs = null;
        try {
            inputs = new FirebaseModelInputs.Builder()
                    .add(review)  // add() as many input arrays as your model requires
                    .build();
        } catch (FirebaseMLException e) {
            e.printStackTrace();
        }
        //Obtain model results
        interpreter.run(inputs, inputOutputOptions)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseModelOutputs>() {
                            @Override
                            public void onSuccess(FirebaseModelOutputs result) {
                                //Get the sentiment prediction
                                float[][] output = result.getOutput(0);
                                float[] probabilities = output[0];
                                predicts=probabilities[0];
                                String predict=String.valueOf(predicts);
                                pos_sentiment=predicts;
                                neg_sentiment=1.0f-pos_sentiment;
                                Log.e("neg_sentiment",""+neg_sentiment);
                                Log.e("pos_sentiment",""+pos_sentiment);
                                if(!TextUtils.isEmpty(review_text)) {
                                    String id2 = databaseReference.push().getKey();
                                    Log.e("id2",""+id2);
                                    Reviews hotel_reviews = new Reviews(id2,review_text,pos_sentiment,neg_sentiment);
                                    databaseReference.child("Hotel_reviews").child(id2).setValue(hotel_reviews);
                                    binding.reviewEt.setText("");
                                    reference2= FirebaseDatabase.getInstance().getReference("Hotel").child(hotelId);
                                    Map<String,Object> map=new HashMap<>();
                                    if(pos_sentiment>neg_sentiment){
                                        pos= pos+1;
                                        map.put("posReviews",pos);
                                        //Log.e("Reviews"," pos "+pos);
                                    }
                                    else{
                                        neg= neg+1;
                                        Log.e("demo","neg"+neg);
                                       // modelHotel.setNegReviews(modelHotel.getNegReviews()+1);
                                        map.put("negReviews",neg);
                                        Log.e("Reviews"," neg "+neg);
//
                                    }

                                    reference2.updateChildren(map);
                                    resetActivity();
                                    //Move to the next activity to display all reviews
                                    Intent intent = new Intent(HotelDetailActivity.this, ProductReviews.class);
                                    total=pos+neg;

                                    //Pass movie name and number of reviews with intent so that the movie data does not need oe retrieved again
                                    intent.putExtra("totalReviews", Integer.toString(total));
                                    intent.putExtra("hotelId", hotelId);
                                    startActivity(intent);
                                }
                                else {
                                    Toast.makeText(HotelDetailActivity.this, "You cannot post an empty review.",Toast.LENGTH_LONG).show();
                                }

                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });
    }
    private void getLastLocation(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onSuccess(Location location) {
                            Geocoder geocoder = new Geocoder(HotelDetailActivity.this, Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                latitude = addresses.get(0).getLatitude();
                                longitude = addresses.get(0).getLongitude();
                                Log.e("latitude","detaite"+latitude);
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
    private float ratingSum = 0;
    private void loadMyReview() {
        reviewArraylist = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hotel");
        ref.child(hotelId).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        reviewArraylist.clear();
                        float rating = 0;
                        for (DataSnapshot ds: snapshot.getChildren()){
                            rating += Float.parseFloat(""+ds.child("ratings").getValue());
                        }
                        long numberOfReviews = snapshot.getChildrenCount();
                        float avgRating = rating/numberOfReviews;
                        binding.textViewAverageAllRating.setText(String.format("%.2f",avgRating));
                        binding.ratingBarAll.setRating(Float.parseFloat(String.valueOf(avgRating)));

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private float distanceSum = 0;
    private void loadMydistance() {
        reviewArraylist = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Distance")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        reviewArraylist.clear();
                        ratingSum = 0 ;
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String distance = (""+ds.child("distance").getValue());

                        }
                        binding.textLat.setText(String.format("%.2f",distance));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private float ratingSumm = 0;
    private void loadHotelDetails() {
        Log.e("hotelId_detail", "id:" + hotelId);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hotel");
        ref.child(hotelId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get data
                        String title = "" + snapshot.child("title").getValue();
                        String hotelView = "" + snapshot.child("url").getValue();
                        String description = "" + snapshot.child("description").getValue();
                        String price = "" + snapshot.child("price").getValue();
                        String categoryId = "" + snapshot.child("categoryId").getValue();
                        String timetamp = "" + snapshot.child("timetamp").getValue();
                        String rating = "" + snapshot.child("ratings").getValue();
                        String viewsCount = "" + snapshot.child("viewsCount").getValue();
                        String réservéCount = "" + snapshot.child("réservéCount").getValue();
                        pos = Integer.parseInt(""+snapshot.child("posReviews").getValue());
                        neg = Integer.parseInt(""+snapshot.child("negReviews").getValue());

                        Log.e("rating",""+rating);
                        MyApplication.laodCategory(
                                "" + categoryId, binding.categoryTv);
                      /*  MyApplication.laodImageFromUrlSinglePage(
                                ""+hotelView,""+title,""+price);*/


                         resetActivity();

                        // resetActivity();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
    private void inputData() {
        String ratings = ""+binding.ratingBarYours.getRating();
        String avgrating = ""+binding.ratingBarAll.getRating();

        // for time of review
        String timestamp = ""+System.currentTimeMillis();
        //setup data in hashmap
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+firebaseAuth.getUid());
        hashMap.put("ratings",""+ratings);
        hashMap.put("avgrating",""+avgrating);
        hashMap.put("timestamp",""+timestamp);
        //put to db >users > Ratings
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference("Hotel");
        reff.child(hotelId).child("Ratings").push().setValue(hashMap);
        //put to db >users > Ratings
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Ratings").child(hotelId).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //review added to db
                        Toast.makeText(HotelDetailActivity.this, "Review published successfully ...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HotelDetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkIsFavorite() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Favorites").child(hotelId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isInMyFavorite = snapshot.exists();
                        if (isInMyFavorite) {
                            //exists in favorite
                            binding.favoriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_favorite_white, 0, 0);
                            binding.favoriteBtn.setText("Remove Favorite");
                        } else {
                            //not exists in favorite
                            binding.favoriteBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_favorite_border_white, 0, 0);
                            binding.favoriteBtn.setText("Add Favorite");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    public void seeAllReviews(View view){
        view.startAnimation(buttonClick);
        Intent intent = new Intent(HotelDetailActivity.this, ProductReviews.class);
        total=pos+neg;
        intent.putExtra("totalReviews", Integer.toString(total));
        intent.putExtra("hotelId",hotelId);
        startActivity(intent);
    }
    @Override
    protected void onStart() {
        super.onStart();
       // addReview();
       // getLastLocation();
    }
    private void askPermission() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else {
                Toast.makeText(this,"Please provide the required permission",Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}