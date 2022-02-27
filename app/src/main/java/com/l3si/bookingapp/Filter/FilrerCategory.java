package com.l3si.bookingapp.Filter;

import android.widget.Filter;

import com.l3si.bookingapp.Adapter.AdapterCategory;
import com.l3si.bookingapp.Model.ModelCategory;

import java.util.ArrayList;

public class FilrerCategory extends Filter {
    //arraylist in which we want to search
    ArrayList<ModelCategory> fillterList ;
    //adapter in which filter need to be implemnted
    AdapterCategory adapterCategory;

    public FilrerCategory(ArrayList<ModelCategory> fillterList, AdapterCategory adapterCategory) {
        this.fillterList = fillterList;
        this.adapterCategory = adapterCategory;

    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        // value should not be null and empty
        if (constraint != null && constraint.length() > 0 ){
            // change to upper case , or lower case to avaid case sensivity
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelCategory> fliterModels = new ArrayList<>();
            for (int i = 0; i<fillterList.size();i++){
                //validate
                if ( fillterList.get(i).getCategory().toUpperCase().contains(constraint)){
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
        adapterCategory.categoryArrayList = (ArrayList<ModelCategory>)results.values;
     // notify cahnges
      adapterCategory.notifyDataSetChanged();
    }
}
