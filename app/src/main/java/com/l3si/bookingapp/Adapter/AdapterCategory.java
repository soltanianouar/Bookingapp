package com.l3si.bookingapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.l3si.bookingapp.Filter.FilrerCategory;
import com.l3si.bookingapp.activity.HotelListAdminActivity;
import com.l3si.bookingapp.Model.ModelCategory;
import com.l3si.bookingapp.databinding.RowCategoryBinding;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.HolderCategory>  implements Filterable {
    private Context context;
    public ArrayList<ModelCategory> categoryArrayList, filterList;
    private RowCategoryBinding binding;
 // instane of our filter class
    private FilrerCategory filrer;
    public AdapterCategory(Context context, ArrayList<ModelCategory> categoryArrayList) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
        this.filterList = categoryArrayList;
    }

    @NonNull
    @Override
    public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowCategoryBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderCategory(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCategory holder, int position) {
        // get Data
        ModelCategory model = categoryArrayList.get(position);
        String id = model.getId();
        String category = model.getCategory();
        String uid = model.getUid();
        long timestamp = model.getTimestamp();
        //set data
        holder.categoryTvv.setText(category);
        //handl click , delete category
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // confirm delete dailog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete").setMessage("Are you sure you want to delete this category ?").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "Deleting...", Toast.LENGTH_SHORT).show();
                        deleteCategory(model,holder);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });
        // handle item click , goto HotelListADMIN
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HotelListAdminActivity.class);
                intent.putExtra("categoryId",id);
                intent.putExtra("categoryTitle",category);
                context.startActivity(intent);

            }
        });
    }


        private void deleteCategory(ModelCategory model, HolderCategory holder) {
        // get id of category to delete
        String id = model.getId();
        DatabaseReference  ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //delete successfolly
                        Toast.makeText(context, "Successfully deleted...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filrer == null ){
            filrer = new FilrerCategory(filterList , this);
        }
        return filrer;
    }

    class HolderCategory extends RecyclerView.ViewHolder {
        TextView categoryTvv;
        ImageButton deleteBtn;
        public HolderCategory(@NonNull View itemView) {
            super(itemView);
            categoryTvv = binding.categoryTv;
            deleteBtn = binding.deleteBtn;
        }
    }
}
