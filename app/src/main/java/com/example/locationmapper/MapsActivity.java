package com.example.locationmapper;

import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private LocationListener locationListener;
    private LocationManager locationManager;

    private final long MIN_TIME = 3000;        //update location in every  3 seconds
    private final long MIN_DISTANCE = 1;    // 1 meter displacement

    Polyline polyline = null;
    private ArrayList<LatLng> locationTrail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        locationTrail = new ArrayList<>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

    }



    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;


        // adding polylines
        final PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(locationTrail).clickable(true);

        polyline = googleMap.addPolyline(polylineOptions);
        polyline.setWidth(10f);

        Log.d("DEBUG", "MAP IS READY");

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.d("DEBUG", "UPDATED");

                LatLng updated = new LatLng(location.getLatitude(), location.getLongitude());

                // adding new updated location
                locationTrail.add(updated);

                //adding marker
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timeStamp  = dateFormat.format(new Date());

                mMap.addMarker(new MarkerOptions().position(updated).title("Timestamp: " + timeStamp));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(updated,24.0f));

                //adding new polyline
                polylineOptions.add(updated);
                polyline = mMap.addPolyline(polylineOptions);
                polyline.setColor(Color.rgb(74,137,243));

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //requesting location updates for location listener
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();

        }else{
            // GPS not enabled
            Toast.makeText(this, "GPS is not enabled in your device", Toast.LENGTH_SHORT).show();
            showGPSDisabledAlertToUser();
        }

        //setting location update time and distance
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locationListener);


            // adding the marker
            Location loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            LatLng updated = new LatLng(loc.getLatitude(), loc.getLongitude());

            // adding new updated location
            locationTrail.add(updated);

            // getting timestamp of user
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timeStamp  = dateFormat.format(new Date());

            // adding marker to map
            mMap.addMarker(new MarkerOptions().position(updated).title("Timestamp: " + timeStamp));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(updated,24.0f));

            //adding new polyline
            polylineOptions.add(updated);
            polyline = mMap.addPolyline(polylineOptions);
            polyline.setColor(Color.rgb(74,137,243));

        } catch (SecurityException | NullPointerException e) {
            e.printStackTrace();
        }

    }

    // gps alert method

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Please enable device GPS.")
                .setCancelable(false)
                .setPositiveButton("Enable Gps",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


}
