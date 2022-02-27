package com.l3si.bookingapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.l3si.bookingapp.Adapter.AdapterBooking;
import com.l3si.bookingapp.Model.ModelBooking;
import com.l3si.bookingapp.MyApplication;
import com.l3si.bookingapp.R;
import com.l3si.bookingapp.databinding.ActivityHotelDetailBinding;

import java.util.ArrayList;
import java.util.List;

public class BookActivity extends AppCompatActivity {
    int overAllTotalAmount;
    Toolbar toolbar;
    Button buy_now,deletedata;
    TextView overAllAmount;
    RecyclerView recyclerView;
    List<ModelBooking> cartModelList;
    AdapterBooking cartAdapter;
    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        buy_now = findViewById(R.id.buy_now);
        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int totalBill = intent.getIntExtra("totalAmount",0);
                overAllAmount.setText("Total Amount : "+totalBill+" DA");
            }
        };
        //get data from my cart adapter
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,new IntentFilter("MyTotalAmount"));
        overAllAmount = findViewById(R.id.textView3);
        recyclerView = findViewById(R.id.bookRv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartModelList = new ArrayList<>();
        cartAdapter = new AdapterBooking(this,cartModelList);
        recyclerView.setAdapter(cartAdapter);
        buy_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(BookActivity.this, "La livraison ne pas disponible", Toast.LENGTH_SHORT).show();
            }
        });
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                ref.child(auth.getUid()).child("AddToCart")
    .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get data
                String title = ""+snapshot.child("title").getValue();
                String description = ""+snapshot.child("description").getValue();
                String price = ""+snapshot.child("price").getValue();
                String categoryId = ""+snapshot.child("categoryId").getValue();
                String hotelView = ""+snapshot.child("url").getValue();
                String timetamp = ""+snapshot.child("timetamp").getValue();
                ActivityHotelDetailBinding binding;
             //   MyApplication.laodCategory(""+categoryId,binding.categoryTv);
                      /*  MyApplication.laodImageFromUrlSinglePage(
                                ""+hotelView,""+title,""+price);*/
                // MyApplication.laodImageFromUrlSinglePage(""+hotelView,binding.hotelView);
                //set data
                Intent intent = getIntent();
                ImageView hotelViews = findViewById(R.id.hotelView);
                //String imageUrl = intent.getStringExtra("image");
                Glide.with(getApplicationContext()).load(hotelView).into(hotelViews);
               // binding.titleTv.setText(title);
              //  binding.descriptionTv.setText(description);
             //   binding.priceTv.setText(price);
                // binding.hotelView.setImageURI(Uri.parse(hotelView));
                //binding.imagetv.setText(b);
               /* for (DataSnapshot doc:snapshot.getChildren()){

                    String  documentId = doc.getKey();
                    ModelBooking myCartModel = doc.getValue(ModelBooking.class);
                    myCartModel.setHotelId(documentId);
                    cartModelList.add(myCartModel);
                    cartAdapter.notifyDataSetChanged();
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



}}