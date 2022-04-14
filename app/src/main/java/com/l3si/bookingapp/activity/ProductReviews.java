package com.l3si.bookingapp.activity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.l3si.bookingapp.Adapter.ExampleAdapter;
import com.l3si.bookingapp.Model.ExampleItem;
import com.l3si.bookingapp.Model.ModelHotel;
import com.l3si.bookingapp.Model.Reviews;
import com.l3si.bookingapp.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProductReviews extends AppCompatActivity {

    public static ArrayList<Integer> imageTag;

    private Reviews review,review2;
    private ArrayList<Reviews> reviews;
    private DatabaseReference ref;
    private String hotelId,reviewID;
    int total;
    FirebaseAuth firebaseAuth;
    private ArrayList<ModelHotel> reviewArraylist;
    ImageButton backBtn;
    private BroadcastReceiver broadcastReceiver;
    private ArrayList<ExampleItem> mExampleList;
    private ProgressBar progressBar;
    private TextView reviews_total,titleTv;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private RecyclerView.LayoutManager mLayoutManager;
    //Check internet throughout the activity using BroadcastReceiver
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_reviews);
        progressBar=findViewById(R.id.progressBar);
        mRecyclerView=findViewById(R.id.recyclerView);
        backBtn = findViewById(R.id.backBtn);
        titleTv = findViewById(R.id.titleTv);
        firebaseAuth = FirebaseAuth.getInstance();
        Bundle bundle = getIntent().getExtras();
        hotelId=bundle.getString("hotelId");
        reviewID=bundle.getString("reviewID");

        total=Integer.parseInt(bundle.getString("totalReviews"));

        reviews_total=findViewById(R.id.reviews_total);
        reviews_total.setText(Integer.toString(total));

        imageTag=new ArrayList<>();
        reviews=new ArrayList<>();
      loadHotelInfo();

        //Extract data from firebase using DatabaseReference
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        ref=database.getReference("Hotel");
        //Give path to fetch data
        ref.child(hotelId).child("Hotel_reviews")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Reviews review=ds.getValue(Reviews.class);
                 //   = "" + ds.child("review").getValue();
                   // String reviewss = review.getReview();
                    //Log.e("review","get "+reviewss);
                    reviews.add(review);
                }
                //Pass the data to xml components
                createExampleList();
                buildRecyclerView();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void createExampleList(){
        mExampleList = new ArrayList<>();
        //Latest review on top
        for(int i=reviews.size()-1;i>=0;i--){
            review2=reviews.get(i);
            Log.e("review2","pos "+review2.getPos_sentiment());
            Log.e("review2","neg "+review2.getNeg_sentiment());


            if(review2.getPos_sentiment()>review2.getNeg_sentiment()){
                mExampleList.add(new ExampleItem(R.drawable.pos,review2.getReview(),String.format("%.2f%% Positive",100*review2.getPos_sentiment())));
                imageTag.add(1);    //+ve
            }
            else{
                mExampleList.add(new ExampleItem(R.drawable.neg,review2.getReview(),String.format("%.2f%% Negative",100*review2.getNeg_sentiment())));
                imageTag.add(-1);   //-ve
            }
        }



    }

    private void loadHotelInfo() {
        Intent intent = getIntent();
        hotelId = intent.getStringExtra("hotelId");
        Log.e("hotelId_detail", "id:" + hotelId);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hotel");
        ref.child(hotelId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get data
                        String title = "" + snapshot.child("title").getValue();
                        String hotelView = "" + snapshot.child("url").getValue();
                        ImageView hotelViews = findViewById(R.id.progileIv);
                        //set hotel info
                        Glide.with(getApplicationContext()).load(hotelView).into(hotelViews);
                        titleTv.setText(title);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private float ratingSum = 0;
    //Create recycler view
    public void buildRecyclerView(){
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new ExampleAdapter(mExampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        progressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
       // unregisterReceiver(broadcastReceiver);
    }
}