package com.l3si.bookingapp;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.l3si.bookingapp.databinding.ActivityHotelDetailBinding;
import com.l3si.bookingapp.fragment.HotelUserFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

public class MyApplication  extends Application   {
   // ImageView imageView;
   private static ActivityHotelDetailBinding binding;

    private final static int REQUEST_CODE = 100;
    @Override
    public void onCreate() { super.onCreate();

    }
    public static final String formatTimestamp(long timestamp){
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);
        //format timesamp^to dd/mm/yyyy
        String date = DateFormat.format("dd/MM/yyyy",cal).toString();

     return date;
    }

    /* public  static  void laodImageFromUrlSinglePage(String HoteleUrl,ImageView imageViews) {
        String TAG = "HOTEL_LOAD_SINGLE_TAG";
       StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(HoteleUrl);
        DatabaseReference reff = FirebaseDatabase.getInstance().getReference("Hotel");
        ref.child(HoteleUrl).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                generatedFilePath = downloadUri.toString(); /// The string(file link) that you need
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }*/
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static double distance(double lat11, double long11, double lat2, double long2) {
        //calculate longitude differtnce
        double longDiff = lat11 - long2;
        //calculte distance
        double distance = Math.sin(deg2rad(lat11))
                *Math.sin(deg2rad(lat2))
                +Math.cos(deg2rad(lat11))
                *Math.cos(deg2rad(lat2))
                *Math.cos(deg2rad(longDiff));
        distance = Math.acos(distance);
        //convert distance radion to degree
        distance = rad2deg(distance);
        //distance in miles
        //distance = distance*60* 1.1515;
        //distance in kilometre
        // float myFloat = 2.001f;
        distance = (float) (distance * 1.6);
        //set distance en text
        //   String formattedString = String.format("%.02f", myFloat);

       //addLocation(""+,"",""+distance);
        Log.e("distance"," textLat "+distance);
        // Intent intent = new Intent(this, HotelUserFragment.class);
        // intent.putExtra("distance",distance);
        /*Bundle bundle = new Bundle();
        //myMessage = distance;
        bundle.putDouble("distance", distance );
        HotelUserFragment fragInfo = new HotelUserFragment();
        fragInfo.setArguments(bundle);*/
        return distance;
    }
    // convert radion to degree
    private static double rad2deg(double distance) {
        return (distance*180.0 / Math.PI);
    }
    // convert degree to radian
    private static double deg2rad(double lat1) {
        return (lat1*Math.PI/180.0);
    }
    public static  void laodDistance(String hotelId , TextView distanceTv) {
        // get Category using categoryId
        String TAG = "Category_HOTEL_TAG";
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Distance").child(hotelId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get category
                        String distance = ""+snapshot.child("distance").getValue();
                        //set to category text view
                        distanceTv.setText(distance);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
    public static  void laodCategory(String categoryId , TextView categoryTv) {
        // get Category using categoryId
        String TAG = "Category_HOTEL_TAG";

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(categoryId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get category
                        String category = ""+snapshot.child("category").getValue();
                        //set to category text view
                        categoryTv.setText(category);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
    public static void deleteHotel(Context context , String hotelId, String hotelUrl, String hotelTitle) {
        String TAG = "DELETE_HOTEL_TAG";


        //deletig hotel
        ProgressDialog progressDialog = new ProgressDialog(context);

            progressDialog.setTitle("Please Wait...");
        progressDialog.setMessage("Deleting"+hotelTitle+"...");
        progressDialog.show();
        // deleting from storge
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(hotelUrl);
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //deleting into form db
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Hotel");
                        reference.child(hotelId)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //deleted from db
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "Hotel Deleted Successfully...", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
    public   static  void AddtoCart(Context context,String hotelId) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        String saveCurrentTime,saveCurrentDate;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime= currentTime.format(calForDate.getTime());
        long timestamp = System.currentTimeMillis();
        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("hotelId",""+hotelId);
        hashMap.put("timestamp",""+timestamp);
       /*cartMap.put("title",name.getText().toString());
        cartMap.put("price",priceTv.getText().toString());
        cartMap.put("currentTime",saveCurrentTime);
        cartMap.put("currentDate",saveCurrentDate);*/
        // cartMap.put("totalQuantitiy",quantity.getText().toString());
        // cartMap.put("totalPrice",totalPrice);
        ArrayList hotelArrayList = new ArrayList<>();
        //load favorite hotel from database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("AddToCart").child(hotelId)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Added To Cart", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to add to favorite  ..."+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
    public static String addLocation(Context context, String hotelId, String distance){
        // we can add only if user logged in
        // 1 check if user is logged in
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null ){
            // not logged in , cant add to fav
            Toast.makeText(context, "You're not logged in", Toast.LENGTH_SHORT).show();

        }else {
            //long timestamp = System.currentTimeMillis();
            //setup data to add in firebase db of current user for favorite hotel
            HashMap<String , Object> hashMap = new HashMap<>();
            hashMap.put("distance",""+distance);
            hashMap.put("hotelId",""+hotelId);
            //save to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Distance").child(hotelId)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Added to your Distance list ...", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Failed to add to Distance  ..."+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        return distance;
    }
    public static void addToFavorite(Context context , String hotelId){
        // we can add only if user logged in
        // 1 check if user is logged in
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null ){
            // not logged in , cant add to fav
            Toast.makeText(context, "You're not logged in", Toast.LENGTH_SHORT).show();

        }else {
            long timestamp = System.currentTimeMillis();
            //setup data to add in firebase db of current user for favorite hotel
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("hotelId",""+hotelId);
            hashMap.put("timestamp",""+timestamp);
            //save to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Favorites").child(hotelId)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Added to your favorites list ...", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Failed to add to favorite  ..."+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
    public static void addToRoom (Context context , String hotelId){
        // we can add only if user logged in
        // 1 check if user is logged in
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null ){
            // not logged in , cant add to fav
            Toast.makeText(context, "You're not logged in", Toast.LENGTH_SHORT).show();

        }else {
            long timestamp = System.currentTimeMillis();
            //setup data to add in firebase db of current user for favorite hotel
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("hotelId",""+hotelId);
            hashMap.put("timestamp",""+timestamp);
            //save to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Rooms").child(hotelId)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Added to your favorites list ...", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Failed to add to favorite  ..."+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
    public static void removeFromFavorite(Context context , String hotelId){
        // we can remove only if user logged in
        // 1 check if user is logged in
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null ){
            // not logged in , cant add to fav
            Toast.makeText(context, "You're not logged in", Toast.LENGTH_SHORT).show();

        }else {

            //remove to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Favorites").child(hotelId)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "remove from your favorites list ...", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Failed to remove from favorite  ..."+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}
