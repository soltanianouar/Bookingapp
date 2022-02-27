package com.l3si.bookingapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.l3si.bookingapp.databinding.ActivityHotelEditBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class HotelEditActivity extends AppCompatActivity {
    private ActivityHotelEditBinding  binding;

    //hotel id get from intent started from adapterhotelAdmin
    private String HotelId;
    private ProgressDialog progressDialog;
    private ArrayList<String> categoryTitleArraylist,categoryIdArraylist;
    private static final String TAG = "HOTEL_EDIT_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHotelEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //hotel id get from intent started from adapterhotelAdmin

        HotelId = getIntent().getStringExtra("HotelId");
        //setup progess dialog

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        loadCategories();
        loadHotelInfo();
        // handel click , pick category
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog();
            }
        });

        // handel back click ,
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
        // handel back click ,
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private String title = "",price = "", description="";
    private void validateData() {
        //GETDATA
         title = binding.titleEt.getText().toString().trim();
        description = binding.descriptionEt.getText().toString().trim();
        price = binding.priceEt.getText().toString().trim();

        //validate data
        if (TextUtils.isEmpty(title)){
            Toast.makeText(this, "Enter Title ...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(description)){
            Toast.makeText(this, "Enter description ...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(price)){
            Toast.makeText(this, "Enter price ...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(selectedCategoryId)){
            Toast.makeText(this, "Pick Category ...", Toast.LENGTH_SHORT).show();
        }else {
            updateHotel();
        }
    }

    private void updateHotel() {
        //start update hotel info
        //show progress
        progressDialog.setMessage("Updating Hotel info ...");
        progressDialog.show();
        // seutp data to update to db

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("title",""+title);
        hashMap.put("description",""+description);
        hashMap.put("price",""+price);
        hashMap.put("categoryId",""+selectedCategoryId);

        // start updating
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hotel");
        ref.child(HotelId)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(HotelEditActivity.this, "Hotel info Updated ...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(HotelEditActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadHotelInfo() {
        DatabaseReference refHotel = FirebaseDatabase.getInstance().getReference("Hotel");
        refHotel.child(HotelId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get hotel info
                        selectedCategoryId = ""+snapshot.child("categoryId").getValue();
                        String description = ""+snapshot.child("description").getValue();
                        String title = ""+snapshot.child("title").getValue();
                        String price = ""+snapshot.child("price").getValue();
                        //set to views
                        binding.titleEt.setText(title);
                        binding.descriptionEt.setText(description);
                        binding.priceEt.setText(price);
                        DatabaseReference refHotelCategory = FirebaseDatabase.getInstance().getReference("Categories");
                        refHotelCategory.child(selectedCategoryId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        // get Category
                                        String category = ""+snapshot.child("category").getValue();
                                        // set to category text view
                                        binding.categoryTv.setText(category);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String selectedCategoryId="",selectedCategoryTitle="";
    private void categoryDialog(){
        //make sting arrat fril arratkust if string

        String [] categoiesArray  = new String[categoryTitleArraylist.size()];
        for (int i=0 ; i<categoryTitleArraylist.size();i++){
            categoiesArray[i] = categoryTitleArraylist.get(i);

        }
        // aelrt dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Category")
                .setItems(categoiesArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
            selectedCategoryId = categoryIdArraylist.get(which);
            selectedCategoryTitle = categoryTitleArraylist.get(which);
            // set tp textview
                       binding.categoryTv.setText(selectedCategoryTitle);

                        }

                }).show();
    }

    private void loadCategories() {
        //loadCategories
        categoryIdArraylist= new ArrayList<>();
        categoryTitleArraylist= new ArrayList<>();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryIdArraylist.clear();
                categoryTitleArraylist.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    String id = ""+ds.child("id").getValue();
                    String category = ""+ds.child("category").getValue();
                    categoryIdArraylist.add(id);
                    categoryTitleArraylist.add(category);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}