package com.l3si.bookingapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.l3si.bookingapp.Model.ModelHotel;
import com.l3si.bookingapp.MyApplication;
import com.l3si.bookingapp.activity.HotelDetailActivity;
import com.l3si.bookingapp.databinding.RowHotelFavoriteBinding;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterHotelFavorite extends RecyclerView.Adapter<AdapterHotelFavorite.HolderHotelFavorite> {
    private Context context;
    private ArrayList<ModelHotel> hotelArrayList;
    //view bindin for row_hotel_favorite
    private RowHotelFavoriteBinding binding ;
    private static final String TAG = "FAV_HOTEL_TAG";
    public AdapterHotelFavorite(Context context, ArrayList<ModelHotel> hotelArrayList) {
        this.context = context;
        this.hotelArrayList = hotelArrayList;
    }

    @NonNull
    @Override
    public HolderHotelFavorite onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowHotelFavoriteBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderHotelFavorite(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderHotelFavorite holder, int position) {
        ModelHotel model = hotelArrayList.get(position);
        String hotelView = model.getUrl();
        loadHotelDetails(model,holder);
      //  Glide.with(context).load(hotelView).into(binding.hotelView);

        //handle click , open hotel details page
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(context, HotelDetailActivity.class);
                intent.putExtra("hotelId",model.getId());// pass hotel id not category id
                intent.putExtra("image",hotelArrayList.get(position).getUrl());
                context.startActivity(intent);
            }
        });
        //handle click , remove from favorite
        holder.removeFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.removeFromFavorite(context,model.getId());// pass hotel id not category id
            }
        });
    }

    private void loadHotelDetails(ModelHotel model, HolderHotelFavorite holder) {
        String hotelId = model.getId();
        hotelArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hotel");
        ref.child(hotelId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get hotel info
                        String hotelTitle = ""+snapshot.child("title").getValue();
                        String description = ""+snapshot.child("description").getValue();
                        String categoryId = ""+snapshot.child("categoryId").getValue();
                        String timetamp = ""+snapshot.child("timetamp").getValue();
                        String uid = ""+snapshot.child("uid").getValue();
                        String hotelView = ""+snapshot.child("url").getValue();
                        String hotelprice = ""+snapshot.child("price").getValue();
                        //set to model
                        model.setFavorite(true);
                        model.setTitle(hotelTitle);
                        model.setDescription(description);
                        model.setPrice(hotelprice);
                        model.setTimetamp(Long.parseLong(timetamp));
                        model.setCategoryId(categoryId);
                        model.setUid(uid);

                        model.setUrl(hotelView);
                        //format date
                        String date = MyApplication.formatTimestamp(Long.parseLong(timetamp));
                        MyApplication.laodCategory(categoryId,holder.categoryTv);
                     //  MyApplication.laodImageFromUrlSinglePage(""+hotelurl,""+hotelTitle,""+hotelprice);
                      //  MyApplication.laodImageFromUrlSinglePage(""+hotelView,holder.hotelView);
                        //set data to views
                        holder.titleTv.setText(hotelTitle);
                        holder.descriptionTv.setText(description);
                        holder.dateTv.setText(date);
                        holder.priceTv.setText(hotelprice);
                        holder.hotelView.setImageURI(Uri.parse(hotelView));

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

    //viewHolder class
    class HolderHotelFavorite extends RecyclerView.ViewHolder {
        ImageView hotelView;
        ProgressBar progressBar;
        TextView titleTv,descriptionTv,categoryTv,priceTv,dateTv;
        ImageButton removeFavBtn;
        public HolderHotelFavorite(@NonNull View itemView) {
            super(itemView);
            hotelView = binding.hotelView;
            progressBar = binding.progressBar;
            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTv;
            categoryTv = binding.categoryTv;
            priceTv = binding.priceTv;
            dateTv = binding.dateTv;
            removeFavBtn = binding.removeFavBtn;
        }
    }
}
