package com.l3si.bookingapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.l3si.bookingapp.databinding.ActivityProfileBinding;
import com.l3si.bookingapp.databinding.ActivityProfileEditBinding;

import java.util.HashMap;

public class ProfileEditActivity extends AppCompatActivity {
    //view binding
    private ActivityProfileEditBinding binding;
    //Firebase auth , get/update user data using uid
    private FirebaseAuth firebaseAuth;
    private static final String TAG = "PROFILE_EDIT_TAG";
    private Uri imageUri = null;
    private String name ="";
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setup progressbar
         progressDialog = new ProgressDialog(this);
         progressDialog.setTitle("Please wait");
         progressDialog.setCanceledOnTouchOutside(false);

        //setup firebase
        firebaseAuth = FirebaseAuth.getInstance();
        loadUserInfo();
        //handle click ,go back
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //handle click ,pick image
        binding.profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageAttachMenu();
            }
        });
        //handle click ,update profile
        binding.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });


    }

    private void validateData() {
        //get data
        name = binding.nameEt.getText().toString().trim();
        //validate data
        if (TextUtils.isEmpty(name)){
            //no name is entred
            Toast.makeText(this, "Enter name ...", Toast.LENGTH_SHORT).show();
        }else {
            //name is entred
            if (imageUri == null){
                //need to update without image
                updateProfile("");
            }else {
                // need to update with image
                uploadImage();
            }
        }
    }

    private void uploadImage() {
        progressDialog.setMessage("Updating profile image");
        progressDialog.show();
        //image path and name , use uid to remplce previous
        String filePathAndName = "ProfileImages/"+firebaseAuth.getUid();
        //Storage reference
        StorageReference reference = FirebaseStorage.getInstance().getReference(filePathAndName);
        reference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) {
                            String uploadedImageUrl = ""+uriTask.getResult();
                            updateProfile(uploadedImageUrl);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileEditActivity.this, "Failed to upload image"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateProfile(String imageUrl){
        progressDialog.setMessage("Updating user profile...");
        progressDialog.show();
        //setup data to update in db
        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("name",""+name);
        if (imageUri !=null){
            hashMap.put("profileImage",""+imageUrl);
        }
        //update data to db
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(firebaseAuth.getUid())
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileEditActivity.this, "Profile Updated...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ProfileEditActivity.this, "Failed to update db"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showImageAttachMenu() {
        // init / setup menu
        PopupMenu popupMenu = new PopupMenu(this,binding.profileIv);
        popupMenu.getMenu().add(Menu.NONE,0,0,"Camera");
        popupMenu.getMenu().add(Menu.NONE,1,1,"Gallery");
        popupMenu.show();
        //handle menu , item clicks
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //GET id of item clicked
                int which = item.getItemId();
                if (which == 0){
                    //camera clicked
                    pickImageCamera();
                } else if (which == 1) {
                    //gallery clicked
                    pickImageGallery();
                }
                return false;
            }
        });
    }

    private void pickImageCamera() {
        // intent to pick image from camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"New Pick");//image title
        values.put(MediaStore.Images.Media.DESCRIPTION,"Sample Image Description");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        cameraActivityResultLauncher.launch(intent);

    }
    private void pickImageGallery() {
        // intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);

    }
    private ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                //used to handle result of camera intent
                    // get uri of image
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();//
                        binding.profileIv.setImageURI(imageUri);
                    }else {
                        Toast.makeText(ProfileEditActivity.this, "canelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
    private ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //used to handle result of gallery intent
                    // get uri of image
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();//
                        imageUri = data.getData();
                        binding.profileIv.setImageURI(imageUri);
                    }else {
                        Toast.makeText(ProfileEditActivity.this, "canelled", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
    private void loadUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // GET all info of user here from snapshot
                        String email = ""+snapshot.child("email").getValue();
                        String name = ""+snapshot.child("name").getValue();
                        String profileImage = ""+snapshot.child("profileImage").getValue();
                        String timestamp = ""+snapshot.child("timestamp").getValue();
                        String uid = ""+snapshot.child("uid").getValue();
                        String userType = ""+snapshot.child("userType").getValue();

                        //set  data to ui
                        binding.nameEt.setText(name);

                        //set image , using glid
                        Glide.with(ProfileEditActivity.this)
                                .load(profileImage)
                                .placeholder(R.drawable.ic_person_gray)
                                .into(binding.profileIv);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}