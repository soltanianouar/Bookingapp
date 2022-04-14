package com.l3si.bookingapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.l3si.bookingapp.Filter.FilterHotelUser;
import com.l3si.bookingapp.Model.ModelHotel;
import com.l3si.bookingapp.MyApplication;
import com.l3si.bookingapp.activity.HotelDetailActivity;
import com.l3si.bookingapp.databinding.RowHotelUserBinding;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterRecommendation extends RecyclerView.Adapter<AdapterRecommendation.Holder> {
    private Context context;
    public ArrayList<ModelHotel> hotelrecomendedArrayList,filterList;
    private FilterHotelUser filter;
    private ArrayList prodArrayList=null;
    private RowHotelUserBinding binding;
    public AdapterRecommendation(Context context, ArrayList<ModelHotel> hotelrecomendedArrayList) {
        this.context = context;
        this.hotelrecomendedArrayList = hotelrecomendedArrayList;
        this.filterList = hotelrecomendedArrayList;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // bind the view
        binding = RowHotelUserBinding.inflate(LayoutInflater.from(context),parent,false);
        return new AdapterRecommendation.Holder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        // get data , set data , handle click ext
        // get Data
        ModelHotel model = hotelrecomendedArrayList.get(position);
        String hotelId =model.getId();
        String uid = model.getUid();
        String etSource = model.getLocation();
        double lat1 = model.getLattitude();
        double long1 = model.getLongitude();
        double distance = model.getDistance();
        String categoryId =model.getCategoryId();
        String hotelView = model.getUrl();
        String title = model.getTitle();
        String description = model.getDescription();
        String price = model.getPrice();
        String review = model.getReview();



        long timestamp = model.getTimetamp();
        // convert time
        //set data
        // holder.nameTv.setText(uid);
     //   holder.ratingTil.setRating(Float.parseFloat(ratings));
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.priceTv.setText(price+" DA");
     //   holder.distance.setText(distance+" km du centre ville");
     //   holder.rateTv.setText();
      //  getRating(hotelId);
        //holder.rateTv.setText(getRating(hotelId));
        if(hotelrecomendedArrayList.get(position).getUrl() != null){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    holder.progressBar.setVisibility(View.INVISIBLE);
                }
            },1000);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Glide.with(context).load(hotelrecomendedArrayList.get(position).getUrl()).into(holder.hotelView);
                }
            },1500);
        }
        // MyApplication.laodImageFromUrlSinglePage(""+hotelView,holder.hotelView);
        MyApplication.laodCategory(""+categoryId,holder.categoryTv);
        //MyApplication.laodDistance(""+hotelId,holder.distance);
        MyApplication.laodlocation(""+hotelId,holder.location);
        float ratings =  MyApplication.getRating(""+hotelId,binding.ratingBar,binding.ratingTil);
        Log.e("ratings","*******"+ratings);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HotelDetailActivity.class);

                intent.putExtra("etSource",etSource);
                intent.putExtra("lat1",lat1);
                intent.putExtra("long1",long1);
                intent.putExtra("hotelId",hotelId);
                intent.putExtra("distance",distance);
                //  intents.putExtra("distance",distance);
                intent.putExtra("image",hotelrecomendedArrayList.get(position).getUrl());
                context.startActivity(intent);


            }
        });
    }
   /* @Override
    public Filter getFilter() {
        if (filter == null ){
            filter = new FilterHotelUser(filterList , AdapterLocation.this);
        }
        return filter;
    }*/
    @Override
    public int getItemCount() {
        return hotelrecomendedArrayList.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        ImageView hotelView;
        ProgressBar progressBar;
        RatingBar ratingTil;
        TextView titleTv,descriptionTv,priceTv,categoryTv,rateTv,location;
        public Holder(@NonNull View itemView) {
            super(itemView);
            hotelView = binding.hotelView;
            progressBar = binding.progressBar;
            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTv;
            priceTv = binding.priceTv;
            categoryTv = binding.categoryTv;
            rateTv = binding.rateTv;
            location = binding.location;
            ratingTil = binding.ratingTil;
        }
    }
    private float ratingSum;
  /* private void getRating(String hotelId){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hotel");
        ref.child(hotelId).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        float rating = 0;

                        for (DataSnapshot ds: snapshot.getChildren()){
                            rating += Float.parseFloat(""+ds.child("ratings").getValue());
                        }

                        long numberOfReviews = snapshot.getChildrenCount();
                        float avgRating = rating/numberOfReviews;
                        Log.e("hotelId",""+hotelId);
                        Log.e("rating",""+rating);
                        Log.e("numberOfReviews",""+numberOfReviews);
                        Log.e("RatingVal",""+avgRating);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });


    }*/
}
