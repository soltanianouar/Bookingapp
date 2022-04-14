package com.l3si.bookingapp;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.RatingBar;
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
import com.l3si.bookingapp.Model.ModelHotel;
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
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ArrayList<ModelHotel> hotelratingArrayList;
    public static DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference("Users");
    private final static int REQUEST_CODE = 100;
    //private static float  rating = 0;
    private static float avg;
    private static ArrayList<HashMap<String, String>> prodArrayList =  new ArrayList<HashMap<String, String>>();
    private  static  HashMap<String,String> prodHashmap = new HashMap<String, String>();
    private static String dis;
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
    public static void incremnetHotelResarvationCount(String hotelId ,TextView ResarvationCount) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hotel");
        ref.child(hotelId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String réservéCount = ""+snapshot.child("réservéCount").getValue();
                        if (réservéCount.equals("")||réservéCount.equals("null")){
                            réservéCount = "0";
                        }
                        long newviewsCount = Long.parseLong(réservéCount)+1;
                        HashMap<String,Object> hashMap  = new HashMap<>();
                        hashMap.put("réservéCount",newviewsCount);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Hotel");
                        reference.child(hotelId)
                                .updateChildren(hashMap);
                      //  ResarvationCount.setText((int) newviewsCount);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
    public static void incremnetHotelViewCount(String hotelId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hotel");
        ref.child(hotelId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String viewsCount = ""+snapshot.child("viewsCount").getValue();
                        if (viewsCount.equals("")||viewsCount.equals("null")){
                            viewsCount = "0";
                        }
                        long newviewsCount = Long.parseLong(viewsCount)+1;
                        HashMap<String,Object> hashMap  = new HashMap<>();
                        hashMap.put("viewsCount",newviewsCount);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Hotel");
                        reference.child(hotelId)
                                .updateChildren(hashMap);
                        //HotelViewCount.set
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
   /* public static void getcalendarpicker (TextView daterangText , Button CalendarBtn){
        MaterialDatePicker materialDatePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setSelection(Pair.create(MaterialDatePicker.thisMonthInUtcMilliseconds(),MaterialDatePicker.todayInUtcMilliseconds())).build();
        CalendarBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // materialDatePicker.show(get,"");
                //materialDatePicker.show(getSupportFragmentManager(), "tag");
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        daterangText.setText(materialDatePicker.getHeaderText());
                    }
                });
            }


        });
    }*/
    public static float getRating(String hotelId, TextView valueRating, RatingBar valueRatingbar  ){
        ArrayList<ModelHotel> hotelratingArrayList = new ArrayList<>();
        float Rating = avg;
        //ArrayList<HashMap<String, String>> prodArrayList =  new ArrayList<HashMap<String, String>>();
        //prodArrayList =  new ArrayList<HashMap<String, String>>();
        //HashMap<String,String> prodHashmap = new HashMap<String, String>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hotel");
        ref.child(hotelId).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        float  rating = 0;
                        for (DataSnapshot ds: snapshot.getChildren()){
                            rating += Float.parseFloat(""+ds.child("ratings").getValue());
                        }
                        long numberOfReviews = snapshot.getChildrenCount();
                        float avgRating = rating/numberOfReviews;
                        avg = avgRating;
                       //hotelratingArrayList.add(avg);
                        Log.e("Rating"," list "+avg);
                        valueRating.setText(String.format("%.2f",avgRating));
                        valueRatingbar.setRating(Float.parseFloat(String.valueOf(avgRating)));
                        prodHashmap.put("rating", String.valueOf(avg));
                        prodHashmap.put("hotelId",hotelId);
                        prodHashmap.put("valueRating",hotelId);
                        prodArrayList.add(prodHashmap);
                        new HotelUserFragment().fillArray(prodHashmap);
                        //Log.e("prodHashmap"," -------- "+prodArrayList.get(0));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }

                });

      //Log.e("Rating",""+Rating);
        return Rating;
    }
    public static  String laodlocation(String hotelId , TextView distanceTv) {
        // get Category using categoryId
        String Location = dis;
        String TAG = "Category_HOTEL_TAG";
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hotel");
        ref.child(hotelId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get category
                        String location = ""+snapshot.child("location").getValue();
                        //set to category text view
                        dis= location;
                        distanceTv.setText(location);
                        Log.e("distanceTv",""+distanceTv);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        return Location;
    }
    public static void Ratingbar(String key, RatingBar ratingBar, TextView rating_count){
        rating_count.setText("(0 Utilisateurs)");
        DatabaseReference mProductRef = FirebaseDatabase.getInstance().getReference("Hotel");
        mProductRef.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //value event listener good and bad in same time
                try {
                    float rating = dataSnapshot.child("Rating").child("rating").getValue(Float.class);
                    //lets get Raters count
                    long raters_count = dataSnapshot.child("Rating").child("Users").getChildrenCount();
                    //lets make maths Calculate
                    float card_rating = rating/raters_count;
                    ratingBar.setRating(card_rating);
                    rating_count.setText("("+raters_count+" Utilisateurs"+")");
                }catch (NullPointerException e){
                    e.printStackTrace();
                    Log.e("Rating Error", "onDataChange: "+ratingBar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public final static double AVERAGE_RADIUS_OF_EARTH = 6371;
    @RequiresApi(api = Build.VERSION_CODES.N)

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lon2 - lon1);

        double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)) +
                (Math.cos(Math.toRadians(lat2))) *
                        (Math.cos(Math.toRadians(lat1))) *
                        (Math.sin(lngDistance / 2)) *
                        (Math.sin(lngDistance / 2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH * c));
     /*   double theta = long2 - long11;
        double dist = Math.sin(deg2rad(lat11)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat11)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

            dist = dist * 1.609344;


        return (dist);*/
        //calculate longitude differtnce
       /* double longDiff = long11 - long2;
      //  double latDiff = lat11 - lat2;
   //     double Diff = latDiff - longDiff;
        //calculte distance
        double distance = Math.sin(deg2rad(lat11))
                *Math.sin(deg2rad(lat2))
                +Math.cos(deg2rad(lat11))
                *Math.cos(deg2rad(lat2))
                *Math.cos(deg2rad(longDiff));
        double distances = Math.acos(distance);
        //convert distance radion to degree
       double distanceess = rad2deg(distances);
        //distance in miles
       // double distancemiles = distance*60* 1.1515;
        //distance in kilometre
       // float myFloat = 2.001f;
        double distancekm = (distanceess *1.60934 );
        //set distance en text
       //  String formattedString = String.format(Locale.US,"%.02f", distance);
        return distanceess;*/
    }

    // convert radion to degree
   /* private static double rad2deg(double distance) {
        return (distance*180.0 / Math.PI);
    }
    // convert degree to radian
    private static double deg2rad(double lat1) {
        return (lat1*Math.PI/180.0);
    }*/
    public static  void laodAnalesiSentiment(String hotelId , TextView avgtotalsentimentpos) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Hotel");
        ref.child(hotelId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get category
                            ModelHotel modelHotel = snapshot.getValue(ModelHotel.class);
                            int  Total = modelHotel.getPosReviews();
                            Log.e("total",""+Total);
                            avgtotalsentimentpos.setText(Total);

                        //set to category text view

                        //categoryTv.setText(reviews);
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
    public   static  void AddtoCart(Context context,String hotelId,String hotelIdRoom /*,String price,String date*/) {

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
        hashMap.put("hotelIdRoom",""+hotelIdRoom);
        //hashMap.put("price",""+price);
       // hashMap.put("rang_date",""+date);
        hashMap.put("hotelIdRoom",""+hotelIdRoom);
        hashMap.put("timestamp",""+timestamp);
        hashMap.put("typeRoom","disponible");
        HashMap<String , Object> hashMaps = new HashMap<>();
        hashMaps.put("typeRoom","disponible");
       /*cartMap.put("title",name.getText().toString());
        cartMap.put("price",priceTv.getText().toString());
        cartMap.put("currentTime",saveCurrentTime);
        cartMap.put("currentDate",saveCurrentDate);*/
        // cartMap.put("totalQuantitiy",quantity.getText().toString());
        // cartMap.put("totalPrice",totalPrice);
        ArrayList hotelArrayList = new ArrayList<>();
        //load favorite hotel from database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).child("AddToCart").child(hotelIdRoom)
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
    public static void addToDistance(Context context , String hotelId,String distance){
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
            hashMap.put("distance",""+distance);
           // hashMap.put("timestamp",""+timestamp);
            //save to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("Distance").child(hotelId)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                          // Toast.makeText(context, "Added to your Distance ...", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Failed to add to Distance  ..."+e.getMessage(), Toast.LENGTH_SHORT).show();
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
    public static void addToRoom(Context context , String hotelId){
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
          //  hashMap.put("date de réservation",""+date);
          //  hashMap.put("typeRoom",""+disponible);
            //save to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("AddToCart").child(hotelId)
                    .setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Added to your cart list ...", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Failed to add to cart  ..."+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
    public static void removeFromRoom(Context context , String hotelId){
        // we can remove only if user logged in
        // 1 check if user is logged in
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null ){
            // not logged in , cant add to fav
            Toast.makeText(context, "You're not logged in", Toast.LENGTH_SHORT).show();

        }else {

            //remove to db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).child("AddToCart").child(hotelId)
                    .removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "remove from your cart list ...", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Failed to remove from cart  ..."+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}
