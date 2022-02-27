package com.l3si.bookingapp.Filter;

import android.widget.Filter;

import com.l3si.bookingapp.Adapter.AdapterCategory;
import com.l3si.bookingapp.Adapter.AdapterHotelAdmin;
import com.l3si.bookingapp.Model.ModelCategory;
import com.l3si.bookingapp.Model.ModelHotel;

import java.util.ArrayList;

public class FIiterHotelAdmin extends Filter{
    //arraylist in which we want to search
    ArrayList<ModelHotel> fillterList ;
    //adapter in which filter need to be implemnted
    AdapterHotelAdmin adapterHotelAdmin;

    public FIiterHotelAdmin(ArrayList<ModelHotel> fillterList, AdapterHotelAdmin adapterHotelAdmin) {
        this.fillterList = fillterList;
        this.adapterHotelAdmin = adapterHotelAdmin;

    }

    @Override
    protected Filter.FilterResults performFiltering(CharSequence constraint) {
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
    protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
        // apply fliter changes
        adapterHotelAdmin.HotelArrayList = (ArrayList<ModelHotel>)results.values;
        // notify cahnges
        adapterHotelAdmin.notifyDataSetChanged();
    }
}

