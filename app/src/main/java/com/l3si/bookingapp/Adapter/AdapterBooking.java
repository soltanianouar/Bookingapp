package com.l3si.bookingapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.l3si.bookingapp.Model.ModelBooking;
import com.l3si.bookingapp.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterBooking extends RecyclerView.Adapter<AdapterBooking.ViewHolder> {
    private Context context;
    private List<ModelBooking> list;
    FirebaseDatabase db;
    FirebaseAuth auth;
    int totalAmount = 0;
    public AdapterBooking(Context context, List<ModelBooking> list) {
        this.context = context;
        this.list = list;
        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_hotel_book,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.date.setText(list.get(position).getCurrentDate());
        holder.time.setText(list.get(position).getCurrentTime());
        //final Uri uri = Uri.parse(list.get(position));
        //holder.imageView.setImageURI(uri);
        //  Glide.with(context).load(list.get(position).getCart_product_image()).into(holder.imageView);
        // holder.imageView.setImageURI(list.get(position).getCart_product_image());
        //  Picasso.get().load(list.get(position).getCart_product_image()).into(holder.imageView);
        // Glide.with(context).load(list.get(position).getImg_url()).into(holder.imageView);
        holder.price.setText(list.get(position).getHotelPrice());
        holder.name.setText(list.get(position).getHotelName());
        holder.totalQuantitiy.setText(list.get(position).getToltal());
        holder.totalPrice.setText(String.valueOf(list.get(position).getTotalPrice()));
      /*  holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("AddToCart").document(auth.getCurrentUser().getUid())
                        .collection("User").document(list.get(position).getDocumentId()).delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    list.remove(list.get(position));
                                    notifyDataSetChanged();
                                    Toast.makeText(context, "Item Deleted", Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(context, "Error"+task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });*/
        //Total amount pass to Cart ACtivity
        totalAmount = totalAmount + list.get(position).getTotalPrice();
        Intent intent =  new Intent("MyTotalAmount");
        intent.putExtra("totalAmount",totalAmount);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,price,date,time,totalQuantitiy,totalPrice;
        ImageView imageView;
        LinearLayout deleteItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameTv);
            imageView = itemView.findViewById(R.id.cart_product_image);
            deleteItem = itemView.findViewById(R.id.deletedata);
            price = itemView.findViewById(R.id.product_price);
            date = itemView.findViewById(R.id.current_date);
            time = itemView.findViewById(R.id.current_time);
            totalQuantitiy = itemView.findViewById(R.id.total_quantity);
            totalPrice = itemView.findViewById(R.id.total_price);
        }
    }
}