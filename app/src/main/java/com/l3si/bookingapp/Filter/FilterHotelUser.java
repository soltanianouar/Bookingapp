package com.l3si.bookingapp.Filter;

import android.widget.Filter;

import com.l3si.bookingapp.Adapter.AdapterHotelUser;
import com.l3si.bookingapp.Model.ModelHotel;

import java.util.ArrayList;

public class FilterHotelUser  extends Filter {
    //arraylist in which we want to search
    ArrayList<ModelHotel> fillterList ;
    AdapterHotelUser adapterHotelUser;

    public FilterHotelUser(ArrayList<ModelHotel> fillterList, AdapterHotelUser adapterHotelUser) {
        this.fillterList = fillterList;
        this.adapterHotelUser = adapterHotelUser;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        Filter.FilterResults results = new Filter.FilterResults();
        // value should not be null and empty
        if (constraint != null && constraint.length() > 0 ){
            // change to upper case , or lower case to avaid case sensivity
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelHotel> fliterModels = new ArrayList<>();
            for (int i = 0; i<fillterList.size();i++){
                //validate
                if ( fillterList.get(i).getTitle().toUpperCase().contains(constraint)){
                    // add to fliter list
                    fliterModels.add((fillterList.get(i)));
                }
            }
            results.count = fliterModels.size();
            results.values = fliterModels;

        }else {
            results.count = fillterList.size();
            results.values = fillterList;
        }
        return results;

    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        // apply fliter changes
        adapterHotelUser.hotelArrayList = (ArrayList<ModelHotel>)results.values;
        // notify cahnges
        adapterHotelUser.notifyDataSetChanged();
    }
}
