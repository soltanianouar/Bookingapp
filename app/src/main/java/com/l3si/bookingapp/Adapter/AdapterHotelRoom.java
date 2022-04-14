package com.l3si.bookingapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.l3si.bookingapp.Filter.FilterHotelUser;
import com.l3si.bookingapp.Model.ModelHotel;
import com.l3si.bookingapp.MyApplication;
import com.l3si.bookingapp.R;
import com.l3si.bookingapp.activity.BedroomActivity;
import com.l3si.bookingapp.activity.WebSiteReservationActivity;
import com.l3si.bookingapp.databinding.RowHotelRoomBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterHotelRoom  extends  RecyclerView.Adapter<AdapterHotelRoom.HolderHotelRoom>   {
    private Context context;
    AppCompatActivity appCompatActivity;
    public ArrayList<ModelHotel> hotelArrayList,filterList;
    private FilterHotelUser filter;
    boolean isInMyRoom = false;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();;
    String hotelId,hotelIdRoom;
    Date selectedDate = new Date();
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
        hotelId =model.getId();
        hotelIdRoom= model.getTimestamp();
        Log.e("hotelIdRoom","---------"+hotelIdRoom);
       // String categoryId =model.getCategoryId();
        String hotelView = model.getUrl();

        String title = model.getTitle();
        String contpersonne = model.getContpersonne();
        String Contlit = model.getContlit();
        String price = model.getPrice();
       // long timestamp = model.getTimetamp();
        // convert time
        //set data
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
        checkIsRoom();
        holder.titleTv.setText(title);
        holder.model= model;
        holder.Contlit.setText(Contlit);
        holder.contpersonne.setText(contpersonne);
        holder.priceTv.setText(price+" DA");
      /*  holder.resarvtionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //addtype();
                MyApplication.AddtoCart(context,hotelId,hotelIdRoom);
                MyApplication.incremnetHotelResarvationCount(hotelId);
                //Intent intent = new Intent(context, BookActivity.class);
                Intent intent = new Intent(context, WebSiteReservationActivity.class);
                context.startActivity(intent);

            }
        });*/

       // new BedroomActivity().getcalendarpicker(binding.daterangText);

      //  Glide.with(context).load(hotelArrayList.get(position).getUrl()).into(holder.hotelView);

        // MyApplication.laodImageFromUrlSinglePage(""+hotelView,holder.hotelView);
       // MyApplication.laodCategory(""+categoryId,holder.categoryTv);
       holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("items"," ok");
                Intent intent = new Intent(context, BedroomActivity.class);
                intent.putExtra("hotelIds",hotelId);
                intent.putExtra("hotelIdroms",hotelIdRoom);
                intent.putExtra("title",title);
                intent.putExtra("price",price);
                intent.putExtra("images",hotelArrayList.get(position).getUrl());
                context.startActivity(intent);
            }
        });
    }

    private void checkIsRoom() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("AddToCart").child(hotelId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isInMyRoom = snapshot.exists();
                        String typeRoom = ""+snapshot.child("typeRoom").getValue();
                        Log.e("typeRoom","****"+typeRoom);
                        if (typeRoom == "disponible" ) {
                            //exists in favorite
                            binding.typeRs.setText("Disponible");
                           // setTextViewDrawableColor(textView, R.color.green);
                            binding.typeRs.setTextColor(Color.GREEN);
                            binding.typeRs.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_baseline_brightness_1_24, 0, 0);
                            //binding.typeRs.setText("Disponible");
                        } else {
                            //not exists in favorite
                            binding.typeRs.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_baseline_brightness_1_red_24, 0, 0);
                            binding.typeRs.setText("No Disponible");
                            binding.typeRs.setTextColor(Color.RED);
                         //   binding.typeRs.setCompoundDrawables(0,R.drawable.ic_baseline_brightness_1_red_24,0,0);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private  void addtype(){

        //save to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hotel");
        ref.child(""+hotelId).child("Rooms").child(""+hotelIdRoom).child("Type").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("typeRoom","disponible");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public int getItemCount() {
        return hotelArrayList.size();
    }
    class HolderHotelRoom extends RecyclerView.ViewHolder{
        ImageView hotelView;
        ProgressBar progressBar;
        ModelHotel model;
        Button resarvtionBtn,CalendarBtn;
        TextView titleTv,contpersonne,priceTv,Contlit,daterangText,typeRs;
        public HolderHotelRoom(@NonNull View itemView) {
            super(itemView);
            hotelView = binding.hotelView;
            View Rootview;

            titleTv = binding.titleTv;
            Contlit = binding.Contlit;
            typeRs = binding.typeRs;
            //daterangText = binding.daterangText;
            progressBar = binding.progressBar;

            //CalendarBtn = binding.CalendarBtn;
            contpersonne = binding.contpersonne;
            priceTv = binding.priceTv;
           // resarvtionBtn = binding.resarvtionBtn;
            itemView.findViewById(R.id.resarvtionBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("demo","onClick: resarvation clicked    ---"+model.getTimestamp());
                    addtype();
                    MyApplication.AddtoCart(context,hotelId,hotelIdRoom);
                    //MyApplication.incremnetHotelResarvationCount(hotelId);
                    //Intent intent = new Intent(context, BookActivity.class);
                    Intent intent = new Intent(context, WebSiteReservationActivity.class);
                    context.startActivity(intent);
                }
            });
        }


    }
}
