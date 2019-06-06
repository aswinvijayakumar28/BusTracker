package com.example.bustracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class bus2tracker extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST = 100;
    private Button stop;
    Geocoder geocoder;
    List<Address> addresses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus2tracker);

        geocoder = new Geocoder(this, Locale.getDefault());

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            finish();
        }
//Check whether this app has access to the location permission//

        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
//If the location permission has been granted, then start the TrackerService//

        if (permission == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(bus2tracker.this,"Bus Tracking Under Progress",Toast.LENGTH_SHORT).show();
            calculatelocation();
        } else {
//If the app doesn’t currently have access to the user’s location, then request access//

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        } stop= findViewById(R.id.stoptrack2);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(bus2tracker.this,"BUS TRACKING UNDER PROGRESS",Toast.LENGTH_SHORT).show();

                finish();
                System.exit(0);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {

//If the permission has been granted...//

        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//...then start the GPS tracking service//
            calculatelocation();
            Toast.makeText(bus2tracker.this,"BUS TRACKING UNDER PROGRESS",Toast.LENGTH_LONG).show();
        } else {

//If the user denies the permission request, then display a toast with some more information//

            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }
    private void calculatelocation(){
        LocationRequest request = new LocationRequest();




        geocoder = new Geocoder(this, Locale.getDefault());

        request.setInterval(10000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

//Get a reference to the database, so your app can perform read and write operations//

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    Location location = locationResult.getLastLocation();
                    if (location != null) {

                        double latti= location.getLatitude();
                        double longi = location.getLongitude();
                        double speed = location.getSpeed();
                        speed = speed + 0.0001;

                        try {
                            addresses = geocoder.getFromLocation(latti, longi, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String address = addresses.get(0).getAddressLine(0);
//Save the location data to the database//

                        ref.child("bus").child("1002").child("location").child("latitude").setValue(latti);
                        ref.child("bus").child("1002").child("location").child("longitude").setValue(longi);
                        ref.child("bus").child("1002").child("location").child("speed").setValue(speed);
                        ref.child("bus").child("1002").child("location").child("place").setValue(address);

                    }
                }
            }, null);

        }


    }
}
