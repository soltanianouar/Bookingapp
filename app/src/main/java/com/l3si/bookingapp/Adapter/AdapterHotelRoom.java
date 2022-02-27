package com.l3si.bookingapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.l3si.bookingapp.Filter.FilterHotelUser;
import com.l3si.bookingapp.Model.ModelHotel;
import com.l3si.bookingapp.Model.ModelRoom;
import com.l3si.bookingapp.MyApplication;
import com.l3si.bookingapp.activity.BedroomActivity;
import com.l3si.bookingapp.activity.BookActivity;
import com.l3si.bookingapp.activity.HotelDetailActivity;
import com.l3si.bookingapp.databinding.RowHotelRoomBinding;
import com.l3si.bookingapp.databinding.RowHotelUserBinding;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterHotelRoom  extends  RecyclerView.Adapter<AdapterHotelRoom.HolderHotelRoom>{
    private Context context;
    public ArrayList<ModelHotel> hotelArrayList,filterList;
    private FilterHotelUser filter;
    private RowHotelRoomBinding binding;
    private static final String TAG = "ADAPTER_HOTEL_USER_TAG";
    public AdapterHotelRoom(Context context, ArrayList<ModelHotel> hotelArrayList) {
        this.context = context;
        this.hotelArrayList = hotelArrayList;
        this.filterList = hotelArrayList;
    }
    @NonNull
    @Override
    public HolderHotelRoom onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // bind the view
        binding = RowHotelRoomBinding.inflate(LayoutInflater.from(context),parent,false);
        return new AdapterHotelRoom.HolderHotelRoom(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderHotelRoom holder, int position) {
        // get data , set data , handle click ext
        // get Data
        ModelHotel model = hotelArrayList.get(position);
        String hotelId =model.getId();
       // String categoryId =model.getCategoryId();
        String hotelView = model.getUrl();
        String title = model.getTitle();
        String contpersonne = model.getContpersonne();
        String Contlit = model.getContlit();
        String price = model.getPrice();
       // long timestamp = model.getTimetamp();
        // convert time
        //set data
        holder.titleTv.setText(title);
        holder.Contlit.setText(Contlit);
        holder.contpersonne.setText(contpersonne);
        holder.priceTv.setText(price+" DA");
        holder.resarvtionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.AddtoCart(context,hotelId);
                Intent intent = new Intent(context, BookActivity.class);
                context.startActivity(intent);
            }
        });
        Glide.with(context).load(hotelArrayList.get(position).getUrl()).into(holder.hotelView);

        // MyApplication.laodImageFromUrlSinglePage(""+hotelView,holder.hotelView);
       // MyApplication.laodCategory(""+categoryId,holder.categoryTv);
       holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, BedroomActivity.class);
                intent.putExtra("hotelIds",hotelId);
                intent.putExtra("title",title);
                intent.putExtra("price",price);
                intent.putExtra("images",hotelArrayList.get(position).getUrl());
                context.startActivity(intent);
            }
        });
       // ModelHotel model = hotelArrayList.get(position);
      /*  loadHotelDetails(model,holder);
        //handle click , open hotel details page
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(context, HotelDetailActivity.class);
                intent.putExtra("hotelId",model.getId());// pass hotel id not category id
                context.startActivity(intent);

            }
        });*/

    }


    @Override
    public int getItemCount() {
        return hotelArrayList.size();
    }

    class HolderHotelRoom extends RecyclerView.ViewHolder{
        ImageView hotelView;
        Button resarvtionBtn;
        TextView titleTv,contpersonne,priceTv,Contlit,rateTv;
        public HolderHotelRoom(@NonNull View itemView) {
            super(itemView);
            hotelView = binding.hotelView;
            titleTv = binding.titleTv;
            Contlit = binding.Contlit;
            resarvtionBtn = binding.resarvtionBtn;
            contpersonne = binding.contpersonne;
            priceTv = binding.priceTv;
        }

    }
}
