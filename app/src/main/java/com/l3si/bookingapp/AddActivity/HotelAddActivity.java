package com.l3si.bookingapp.AddActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.l3si.bookingapp.Model.ModelCategory;
import com.l3si.bookingapp.activity.BedroomActivity;
import com.l3si.bookingapp.activity.HotelDetailActivity;
import com.l3si.bookingapp.databinding.ActivityHotelAddBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class HotelAddActivity extends AppCompatActivity {
    //view binding
    private ActivityHotelAddBinding binding;
    //firebase auth
    private FirebaseAuth firebaseAuth;
    //progress dialog
    private ProgressDialog progressDialog;
    //arrayliste to hold hotel category
    private ArrayList<String> categoryTitleArrayList,categoryIdArraylist;
    //
    //uri of picked image
    private Uri imgUrl= null;
    public  Context context;
    private static final int PICK_IMAGE = 1;
    // TAG for debugging
    private static final String TAG = "ADD_IMAGE_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHotelAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        loadhotelcategories();
        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);
        //handle click  , go back page
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //handle click  , upload image
        binding.imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hotelPickIntent();

            }
        });
        //handle click  , pick category
        binding.categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryPickDialog();

            }
        });
        // handle click ; upload image
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        //  validtae data
                validateData();
            }
        });
        binding.addroomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  validtae data
                startActivity(new Intent(HotelAddActivity.this, RoomAddActivity.class));
            }
        });
    }
    private String title = "", description = "",price= "",location= "";
    private void validateData() {
        //1 ---- validate data
        //get data
        title = binding.titleEt.getText().toString().trim();
        description = binding.descriptionEt.getText().toString().trim();
        price = binding.priceEt.getText().toString().trim();
        location = binding.locationEt.getText().toString().trim();
       // category = binding.categoryTv.getText().toString().trim();
        // validate data
        if (TextUtils.isEmpty(title)){
            Toast.makeText(this, "Enter Title...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(description)){
            Toast.makeText(this, "Enter description...", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(price)){
            Toast.makeText(this, "Enter price...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(selectedCategoryTitle)){
            Toast.makeText(this, "Pick Category...", Toast.LENGTH_SHORT).show();
        }else if (imgUrl == null){
            Toast.makeText(this, "Pick image...", Toast.LENGTH_SHORT).show();
        }else {
            // all data is valid , can upload now
            uploadImagetoStorge();
        }
    }
    private void uploadImagetoStorge() {
        //2 ---- upload image to firebase  storge
        // show progrse
        progressDialog.setMessage("Uploading image ...");
        progressDialog.show();
        //timestamp
        long timetamp = System.currentTimeMillis();
        // path of image in firebase storge
        String filePathandName = "Hotel/"+timetamp;
        //storge ref
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathandName);
        storageReference.putFile(imgUrl)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       // Toast.makeText(HotelAddActivity.this, "Image upload failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        // get image url
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String uploadedimageUrl= ""+uriTask.getResult();
                        //upload to firebase db
                        uploadImagetoDb(uploadedimageUrl,timetamp);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(HotelAddActivity.this, "Image upload failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void uploadImagetoDb(String uploadedimageUrl, long timetamp) {
        //3 ---- upload image to firebase  db
        progressDialog.setMessage("Uploading image ...");
        String uid = firebaseAuth.getUid();
        //setup data to upload
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+uid);
        hashMap.put("id",""+timetamp);
        hashMap.put("title",""+title);
        hashMap.put("price",""+price);
        hashMap.put("location",""+location);
        hashMap.put("description",""+description);
        hashMap.put("categoryId",""+selectedCategoryId);
        hashMap.put("url",""+uploadedimageUrl);
        hashMap.put("timetamp",timetamp);
        hashMap.put("viewsCount",0);
        //db ref :
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hotel");
        ref.child(""+timetamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(HotelAddActivity.this, "Successfully to upload... ", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HotelAddActivity.this, "Failed to upload to db "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void loadhotelcategories() {
        categoryTitleArrayList = new ArrayList<>();
        categoryIdArraylist = new ArrayList<>();
        //db ref to load category
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryTitleArrayList.clear();
                categoryIdArraylist.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    //get id and title of category
                    String categoryId = ""+ds.child("id").getValue();
                    String categoryTitle = ""+ds.child("category").getValue();
                    //add to respective arraylist
                    categoryTitleArrayList.add(categoryTitle);
                    categoryIdArraylist.add(categoryId);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //selected category id and category title
    private String selectedCategoryId, selectedCategoryTitle;
    private void categoryPickDialog() {
        //get array of category from array list
        String [] categoriesArray  = new String[categoryTitleArrayList.size()];
        for (int i = 0; i< categoryTitleArrayList.size(); i++){
            categoriesArray[i] = categoryTitleArrayList.get(i);

        }
        AlertDialog.Builder builder = new  AlertDialog.Builder(this);
        builder.setTitle("Pick category").setItems(categoriesArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
        // handle item click
                // get clicked item from list
                selectedCategoryTitle = categoryTitleArrayList.get(which);
                selectedCategoryId = categoryIdArraylist.get(which);
                //set to cateogry textview
                binding.categoryTv.setText(selectedCategoryTitle);



            }
        }).show();
    }
    private void hotelPickIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                //
                imgUrl = data.getData();

            }else {
                Toast.makeText(this, "cancelled upload image", Toast.LENGTH_SHORT).show();
            }


        }
    }
}