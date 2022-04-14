package com.l3si.bookingapp.Dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.l3si.bookingapp.Location.LocationActivity;
import com.l3si.bookingapp.Model.ModelCategory;
import com.l3si.bookingapp.Model.ModelHotel;
import com.l3si.bookingapp.Model.ModelRegestration;
import com.l3si.bookingapp.MyApplication;
import com.l3si.bookingapp.ProfileActivity;
import com.l3si.bookingapp.R;
import com.l3si.bookingapp.databinding.ActivityDashboardUserBinding;
import com.l3si.bookingapp.fragment.HotelUserFragment;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class DashboardUserActivity extends AppCompatActivity {
    public ArrayList<ModelCategory> categoryArrayList;
    public ArrayList<ModelHotel> modelHotels;
    public ArrayList<ModelRegestration> RecommendedArrayList;
    public ViewPagerAdapter viewPagerAdapter;

    //view binding

    private ActivityDashboardUserBinding binding;
    //firebase auth
    private FirebaseAuth firebaseAuth;
    boolean isInMyFavorite = false;
    String hotelId, distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //get data from intent e.g hotelId
        Intent intent = getIntent();
        hotelId = intent.getStringExtra("hotelId");
        distance = intent.getStringExtra("distance");
        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadUserInfo();
        setupViewPagerAdapter(binding.viewpager);
        binding.tabLayout.setupWithViewPager(binding.viewpager);
        binding.buttonlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardUserActivity.this, LocationActivity.class));
            }
        });
        //handle click , open profile
        binding.profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardUserActivity.this, ProfileActivity.class));
            }
        });
        //handle click , lougout
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();

            }
        });
        loadReviews();// avg rating
    }
    private float ratingSum = 0;
    private void loadReviews() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ratingSum = 0 ;
                        for (DataSnapshot ds: snapshot.getChildren()){
                            float rating = Float.parseFloat(""+ds.child("ratings").getValue());
                            ratingSum = ratingSum+rating;

                        }

                        long numberOfReviews = snapshot.getChildrenCount();
                        float avgRating = ratingSum/numberOfReviews;
                       // binding.ratingBarAll.setRating(avgRating);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void loadUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // GET all info of user here from snapshot
                        String name = ""+snapshot.child("name").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();
                        // format date to dd/MM/yyyy
                        String formattedDate = MyApplication.formatTimestamp(Long.parseLong(timestamp));
                        //set  data to ui

                        binding.nameuser.setText("Bienvenue,"+name);
                        //set image , using glid
                        Glide.with(DashboardUserActivity.this)
                                .load(profileImage)
                                .placeholder(R.drawable.ic_person_gray)
                                .into(binding.progileIv);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void checkUser() {
        // get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        ModelRegestration modelRegestration = new ModelRegestration();

        if (firebaseUser == null) {
            binding.subTitleTv.setText("Not logged In");
        } else {
            //logged in get user info
            String email = firebaseUser.getEmail();
            String name  =  modelRegestration.getName();
            // set in textview of toolbar
            binding.subTitleTv.setText(email);
            binding.nameuser.setText("Welcome "+email);
        }
    }
    private void setupViewPagerAdapter(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, this);
        categoryArrayList = new ArrayList<>();
        categoryArrayList = new ArrayList<>();
        // load category from firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear before adding to list
                categoryArrayList.clear();
                //add data to models
                ModelCategory modelAll = new ModelCategory("01", "All", "", 1);
                ModelCategory modelMostViewed = new ModelCategory("02", "Most Viewed", "", 1);
                ModelCategory modelRecommended = new ModelCategory("03", "Recommended", "", 1);
                //add models to list
                categoryArrayList.add(modelAll);
                categoryArrayList.add(modelMostViewed);
                categoryArrayList.add(modelRecommended);
                //add data to view pager adapter
                viewPagerAdapter.addFragment(HotelUserFragment.newInstance(
                        "" + modelAll.getId(),
                        "" + modelAll.getCategory(),
                        "" + modelAll.getUid(),
                        "" + modelAll.getDistance()
                ), modelAll.getCategory());
                viewPagerAdapter.addFragment(HotelUserFragment.newInstance(
                        "" + modelMostViewed.getId(),
                        "" + modelMostViewed.getCategory(),
                        "" + modelMostViewed.getUid(),
                        "" + modelAll.getDistance()
                ), modelMostViewed.getCategory());
                viewPagerAdapter.addFragment(HotelUserFragment.newInstance(
                        "" + modelRecommended.getId(),
                        "" + modelRecommended.getCategory(),
                        "" + modelRecommended.getUid(),
                        "" + modelAll.getDistance()
                ), modelRecommended.getCategory());
                //refreash list
                viewPagerAdapter.notifyDataSetChanged();
                // load from firebase
                for (DataSnapshot ds : snapshot.getChildren()) {
                    //get Data
                    ModelCategory model = ds.getValue(ModelCategory.class);
                    // add data to viewPagerAdapter
                    viewPagerAdapter.addFragment(HotelUserFragment.newInstance(
                            "" + model.getId(),
                            "" + model.getCategory(),
                            "" + model.getUid(),
                            "" + model.getDistance()
                    ), model.getCategory());
                    //refreash list
                    viewPagerAdapter.notifyDataSetChanged();
                }
                //set adapter to view pager
                viewPager.setAdapter(viewPagerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<HotelUserFragment> fragmentslist = new ArrayList<>();
        private ArrayList<String> fragmentTitleList = new ArrayList<>();
        private Context context;

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior, Context context) {
            super(fm, behavior);
            this.context = context;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentslist.get(position);
        }

        @Override
        public int getCount() {
            return fragmentslist.size();
        }

        private void addFragment(HotelUserFragment fragment, String title) {
            // add fragment passed as parameter in fragmentslist
            fragmentslist.add(fragment);
            // add fragment passed as parameter in fragmentTitleList
            fragmentTitleList.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // getLastLocation();
    }
}