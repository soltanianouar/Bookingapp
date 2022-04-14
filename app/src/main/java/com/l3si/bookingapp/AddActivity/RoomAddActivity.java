package com.l3si.bookingapp.AddActivity;

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
import com.l3si.bookingapp.databinding.ActivityRoomAddBinding;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RoomAddActivity extends AppCompatActivity {
    //view binding
    private ActivityRoomAddBinding binding;
    //firebase auth
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;
    //arrayliste to hold hotel category
    private ArrayList<String> HotelTitleArrayList,HotelIdArraylist;
    //
    //uri of picked image
    private Uri imgUrl= null;
    public Context context;
    private static final int PICK_IMAGE = 1;
    // TAG for debugging
    private static final String TAG = "ADD_IMAGE_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRoomAddBinding.inflate(getLayoutInflater());
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
        binding.hotelTv.setOnClickListener(new View.OnClickListener() {
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
    }
    private String title = "", Contlit = "",price= "", contpersonne= "";
    private void validateData() {
        //1 ---- validate data
        //get data
        title = binding.titleEt.getText().toString().trim();
        Contlit = binding.ContlitEt.getText().toString().trim();
        price = binding.priceEt.getText().toString().trim();
        contpersonne = binding.contpersonneEt.getText().toString().trim();
        // category = binding.categoryTv.getText().toString().trim();
        // validate data
        if (TextUtils.isEmpty(title)){
            Toast.makeText(this, "Enter Title...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(Contlit)){
            Toast.makeText(this, "Enter Contlit...", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(price)){
            Toast.makeText(this, "Enter price...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(contpersonne)){
            Toast.makeText(this, "Enter contpersonne...", Toast.LENGTH_SHORT).show();
        }else if (imgUrl == null){
            Toast.makeText(this, "Pick image...", Toast.LENGTH_SHORT).show();
        }else {
            // all data is valid , can upload now
            uploadImagetoStorge();
        }
    }
    //selected category id and category title
    private String selectedhotelId, selectedHotelTitle;
    private void categoryPickDialog() {
        //get array of category from array list
        String [] HotelsArray  = new String[HotelTitleArrayList.size()];
        for (int i = 0; i< HotelTitleArrayList.size(); i++){
            HotelsArray[i] = HotelTitleArrayList.get(i);

        }
        AlertDialog.Builder builder = new  AlertDialog.Builder(this);
        builder.setTitle("Pick hotel").setItems(HotelsArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // handle item click
                // get clicked item from list
                selectedHotelTitle = HotelTitleArrayList.get(which);
                selectedhotelId = HotelIdArraylist.get(which);
                //set to cateogry textview
                binding.hotelTv.setText(selectedHotelTitle);



            }
        }).show();
    }
    private void loadhotelcategories() {
        HotelTitleArrayList = new ArrayList<>();
        HotelIdArraylist = new ArrayList<>();
        //db ref to load category
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hotel");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HotelTitleArrayList.clear();
                HotelIdArraylist.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    //get id and title of category
                    String hotelId = ""+ds.child("id").getValue();
                    String hotelTitle = ""+ds.child("title").getValue();
                    //add to respective arraylist
                    HotelTitleArrayList.add(hotelTitle);
                    HotelIdArraylist.add(hotelId);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void uploadImagetoStorge() {
        //2 ---- upload image to firebase  storge
        // show progrse
        progressDialog.setMessage("Uploading image ...");
        progressDialog.show();
        //timestamp
        long timetamp = System.currentTimeMillis();
        long timetamps = System.currentTimeMillis();
        // path of image in firebase storge
        String filePathandName = "Hotel/"+timetamp+"rooms"+timetamps;
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
                        String hotelId= ""+uriTask.getResult();
                        //upload to firebase db
                        uploadImagetoDb(uploadedimageUrl,timetamp,hotelId);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RoomAddActivity.this, "Image upload failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void uploadImagetoDb(String uploadedimageUrl,long timetamp ,String hotelId) {

        //3 ---- upload image to firebase  db
        progressDialog.setMessage("Uploading image ...");
        String uid = firebaseAuth.getUid();
        //setup data to upload
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+uid);
        hashMap.put("idHotel",""+timetamp);
        hashMap.put("title",""+title);
        hashMap.put("price",""+price);
        hashMap.put("contlit",""+Contlit);
        hashMap.put("id",""+selectedhotelId);
        hashMap.put("contpersonne",""+contpersonne);
        hashMap.put("url",""+uploadedimageUrl);
        //db ref :
        long timestamp = System.currentTimeMillis();
        //setup data to add in firebase db of current user for favorite hotel
        hashMap.put("hotelId",""+hotelId);
        hashMap.put("timestamp",""+timestamp);
        hashMap.put("idRoom",""+timestamp);
        hashMap.put("typeRoom","disponible");
        //save to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hotel");
        ref.child(""+selectedhotelId).child("Rooms").child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RoomAddActivity.this, "Added to your hotel list ...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RoomAddActivity.this, "Failed to add to favorite  ..."+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    //selected category id and category title
    //private String selectedhotelId, selectedHotelTitle;
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