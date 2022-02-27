package com.l3si.bookingapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.l3si.bookingapp.Dashboard.DashboardUserActivity;
import com.l3si.bookingapp.Filter.FIiterHotelAdmin;
import com.l3si.bookingapp.Filter.FilterHotelUser;
import com.l3si.bookingapp.Location.LocationActivity;
import com.l3si.bookingapp.Model.ModelHotel;
import com.l3si.bookingapp.MyApplication;
import com.l3si.bookingapp.R;
import com.l3si.bookingapp.activity.BedroomActivity;
import com.l3si.bookingapp.activity.HotelDetailActivity;
import com.l3si.bookingapp.databinding.RowHotelAdminBinding;
import com.l3si.bookingapp.databinding.RowHotelUserBinding;
import com.l3si.bookingapp.fragment.HotelUserFragment;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterHotelUser extends RecyclerView.Adapter<AdapterHotelUser.HolderHotelUser> implements Filterable {
    private Context context;
    public ArrayList<ModelHotel> hotelArrayList,filterList;
    private FilterHotelUser filter;
    private RowHotelUserBinding binding;
    private static final String TAG = "ADAPTER_HOTEL_USER_TAG";
    public AdapterHotelUser(Context context, ArrayList<ModelHotel> hotelArrayList) {
        this.context = context;
        this.hotelArrayList = hotelArrayList;
        this.filterList = hotelArrayList;
    }

    @NonNull
    @Override
    public HolderHotelUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // bind the view
        binding = RowHotelUserBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderHotelUser(binding.getRoot());
    }

    @RequiresApi(api = 31)
    @Override
    public void onBindViewHolder(@NonNull HolderHotelUser holder, int position) {
        // get data , set data , handle click ext
        // get Data
        ModelHotel model = hotelArrayList.get(position);
        String hotelId =model.getId();
        String etSource = model.getLocation();
        double lat1 = model.getLattitude();
        double long1 = model.getLongitude();
        double distance = model.getDistance();

        String categoryId =model.getCategoryId();
        String hotelView = model.getUrl();
        String title = model.getTitle();
        String description = model.getDescription();
        String price = model.getPrice();

        long timestamp = model.getTimetamp();
        // convert time
        //set data
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.priceTv.setText(price+" DA");
        holder.distance.setText(distance+" km du centre ville");
        if(hotelArrayList.get(position).getUrl() != null){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    holder.progressBar.setVisibility(View.INVISIBLE);
                }
            },1000);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Glide.with(context).load(hotelArrayList.get(position).getUrl()).into(holder.hotelView);
                }
            },1500);

        }
       // MyApplication.laodImageFromUrlSinglePage(""+hotelView,holder.hotelView);
        MyApplication.laodCategory(""+categoryId,holder.categoryTv);
        MyApplication.laodDistance(""+hotelId,holder.distance);
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
                intent.putExtra("image",hotelArrayList.get(position).getUrl());
                context.startActivity(intent);


            }
        });
    }

    @Override
    public int getItemCount() {
        return hotelArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null ){
            filter = new FilterHotelUser(filterList , this);
        }
        return filter;
    }

    class HolderHotelUser extends RecyclerView.ViewHolder{
        ImageView hotelView;
        ProgressBar progressBar;
        TextView titleTv,descriptionTv,priceTv,categoryTv,rateTv,distance;
        public HolderHotelUser(@NonNull View itemView) {
            super(itemView);
            hotelView = binding.hotelView;
            progressBar = binding.progressBar;
            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTv;
            priceTv = binding.priceTv;
            categoryTv = binding.categoryTv;
            rateTv = binding.rateTv;
            distance = binding.distance;
        }

    }
}
