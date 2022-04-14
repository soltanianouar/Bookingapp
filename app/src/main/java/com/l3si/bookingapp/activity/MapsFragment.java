package com.l3si.bookingapp.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.l3si.bookingapp.R;

import java.io.IOException;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

public  class MapsFragment extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION =1 ;
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /* Location Manager use either the Network provider or GPS provider of the android device to fetch the location
        information of the user, like latitude or longitude. */

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        /* This part asks for location access permission from the user. */

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                /* User's latitude and longitude is fetched here using the location object. */
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();


                /* Geocoder derives the location name, the specific country and city of the user as a list. */
                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    String adress = addresses.get(0).getLocality() + ":";
                    adress += addresses.get(0).getCountryName();

                    /* The latitude and longitude is combined and placed on the google map using a marker in the following part. */

                    LatLng latLng = new LatLng(latitude, longitude);

                    if (marker != null) {
                        marker.remove();
                    }
                    marker = mMap.addMarker(new MarkerOptions().position(latLng).title(adress));
                    mMap.setMaxZoomPreference(10);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.0f));



                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        /* As stated before, location manager uses either network provider or the gps provider to fetch user's location information. */
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

    }
    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
    }
}
  /*  @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }*/
