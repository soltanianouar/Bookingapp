package com.l3si.bookingapp.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.l3si.bookingapp.Filter.FIiterHotelAdmin;
import com.l3si.bookingapp.activity.HotelDetailActivity;
import com.l3si.bookingapp.activity.HotelEditActivity;
import com.l3si.bookingapp.Model.ModelHotel;
import com.l3si.bookingapp.MyApplication;
import com.l3si.bookingapp.databinding.RowHotelAdminBinding;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterHotelAdmin extends RecyclerView.Adapter<AdapterHotelAdmin.HolderHotelAdmin> implements Filterable {
    private Context context;
    public ArrayList<ModelHotel>  HotelArrayList , filterList;
    private RowHotelAdminBinding binding;
    private FIiterHotelAdmin filrer;
    //progress
    private ProgressDialog progressDialog;
    public AdapterHotelAdmin() {
    }
    public AdapterHotelAdmin(Context context, ArrayList<ModelHotel> HotelArrayList) {
        this.context = context;
        this.HotelArrayList = HotelArrayList;
        this.filterList = HotelArrayList;
        //init progress dialog
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

    }
    @NonNull
    @Override
    public HolderHotelAdmin onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowHotelAdminBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderHotelAdmin(binding.getRoot());
    }
    @Override
    public void onBindViewHolder(@NonNull HolderHotelAdmin holder, int position) {
        // get Data
        ModelHotel model = HotelArrayList.get(position);
        String hotelId =model.getId();
        String categoryId =model.getCategoryId();
        String hotelView = model.getUrl();
        String title = model.getTitle();
        String description = model.getDescription();
        String price = model.getPrice();
        long timestamp = model.getTimetamp();
        // convert time
        String date = MyApplication.formatTimestamp(timestamp);
        //set data
        holder.titleTv.setText(title);
        holder.descriptionTv.setText(description);
        holder.priceTv.setText(price+" DA");
        //holder.mCost.setText("DA "+list.get(position).getPrice());

            // load futher detaile like category hotel from url , hotel price in spa
        if(HotelArrayList.get(position).getUrl() != null){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    holder.progressBar.setVisibility(View.INVISIBLE);
                }
            },3000);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Glide.with(context).load(HotelArrayList.get(position).getUrl()).into(holder.hotelView);
                }
            },4000);

        }
        MyApplication.laodCategory(""+categoryId,holder.categoryTv);
       // laodImageFromUrl(model, holder);
       //MyApplication.laodImageFromUrlSinglePage(""+hotelUrl,""+title,""+price);
       // MyApplication.laodImageFromUrlSinglePage(""+hotelView,holder.hotelView);
        // handel click , show dialoh with options 1 = edit 2 = delte
        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moreOptiosnDialog(model,holder);
            }
        });
        // handel click , open hotel details page
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HotelDetailActivity.class);
                intent.putExtra("hotelId",hotelId);
                context.startActivity(intent);
            }
        });

    }

    private void moreOptiosnDialog(ModelHotel model, HolderHotelAdmin holder) {
        String hotelId = model.getId();
        String hotelUrl = model.getUrl();
        String hotelTitle = model.getTitle();

        // options to show in dialog
        String [] options = {"Edit","Delete"};
        // elrt dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("CHoose Options")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle dialog option click
                        if(which == 0){
                            //edit clicked
                            Intent intent = new Intent(context, HotelEditActivity.class);
                            intent.putExtra("HotelId",hotelId);
                            context.startActivity(intent);

                        }else if (which == 1){
                            //Delete clicked
                            MyApplication.deleteHotel(context,""+hotelId,""+hotelUrl,""+hotelTitle);
                           // deleteHotel(model,holder);
                        }
                    }
                }).show();
    }



  /*  private void laodImageFromUrl(ModelHotel model, HolderHotelAdmin holder) {
        // USING url we can get file and its
        String imageUrl  = model.getUrl();
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
        ref.getMetadata()
                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/

   /* private void laodCategory(ModelHotel model, HolderHotelAdmin holder) {
        // get Category using categoryId
        String categoryId = model.getCategoryId();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(categoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get category
                        String category = ""+snapshot.child("category").getValue();
                        //set to category text view
                        holder.categoryTv.setText(category);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }*/
    @Override
    public int getItemCount() {
        return HotelArrayList.size();
    }
    @Override
    public Filter getFilter() {
        if (filrer == null ){
            filrer = new FIiterHotelAdmin(filterList , this);
        }
        return filrer;
    }

    class HolderHotelAdmin extends RecyclerView.ViewHolder {
        ImageView hotelView,moreBtn;
        ProgressBar progressBar;
        TextView titleTv,descriptionTv,priceTv,categoryTv,rateTv,locationEt;
        public HolderHotelAdmin(@NonNull View itemView) {
            super(itemView);
            hotelView = binding.hotelView;
            progressBar = binding.progressBar;
            titleTv = binding.titleTv;
            descriptionTv = binding.descriptionTv;
            priceTv = binding.priceTv;
            categoryTv = binding.categoryTv;
            rateTv = binding.rateTv;
            moreBtn = binding.moreBtn;

        }
    }
}

