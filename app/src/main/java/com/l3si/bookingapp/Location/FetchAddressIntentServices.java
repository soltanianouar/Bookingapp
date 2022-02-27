package com.l3si.bookingapp.Location;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.internal.Constants;

import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;

public class FetchAddressIntentServices extends IntentService {
    ResultReceiver resultReceiver;

    public FetchAddressIntentServices() {
        super("FetchAddressIntentServices");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (intent != null) {
            String errormessgae = "";
            resultReceiver = intent.getParcelableExtra(ConstantsS.RECEVIER);
            Location location = intent.getParcelableExtra(ConstantsS.LOCATION_DATA_EXTRA);
            if (location == null) {
                return;
            }
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        1);
            } catch (Exception ioException) {
                Log.e("", "Error in getting address for the location");
            }

            if (addresses == null || addresses.size() == 0) {
                errormessgae = "No address found for the location";
                Toast.makeText(this, "" + errormessgae, Toast.LENGTH_SHORT).show();
            } else {
                Address address = addresses.get(0);
                String str_postcode = address.getPostalCode();
                String str_Country = address.getCountryName();
                String str_state = address.getAdminArea();
                String str_district = address.getSubAdminArea();
                String str_locality = address.getLocality();
                String str_address = address.getFeatureName();
                devliverResultToRecevier(ConstantsS.SUCCESS_RESULT, str_address, str_locality, str_district, str_state, str_Country, str_postcode);
            }
        }

    }

    private void devliverResultToRecevier(int resultcode, String address, String locality, String district, String state, String country, String postcode) {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsS.ADDRESS, address);
        bundle.putString(ConstantsS.LOCAITY, locality);
        bundle.putString(ConstantsS.DISTRICT, district);
        bundle.putString(ConstantsS.STATE, state);
        bundle.putString(ConstantsS.COUNTRY, country);
        bundle.putString(ConstantsS.POST_CODE, postcode);
        resultReceiver.send(resultcode, bundle);
    }

}

/* List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (Exception exception) {
                errormessgae = exception.getMessage();
            }
            ArrayList<String> addressFragments = null;
            if (addresses == null || addresses.isEmpty()) {
                devliverResultToRecevier(Constants.FAILURE_RESULT, errormessgae);
            } else {
                Address address = addresses.get(0);
                addressFragments = new ArrayList<>();
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++)
                    addressFragments.add(address.getAddressLine(i));
            }
            devliverResultToRecevier(Constants.SUCCESS_RESULT, TextUtils.join(
                    Objects.requireNonNull(System.getProperty("line.separator")), addressFragments));*/
