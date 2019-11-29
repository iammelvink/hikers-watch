package com.iammelvink.hikerswatch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private TextView txtLat;
    private TextView txtLng;
    private TextView txtAcc;
    private TextView txtAlt;
    private TextView txtAdd;

    /*Apps start here*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Connecting UI elements to code*/
        txtLat = (TextView) findViewById(R.id.txtLat);
        txtLng = (TextView) findViewById(R.id.txtLng);
        txtAcc = (TextView) findViewById(R.id.txtAcc);
        txtAlt = (TextView) findViewById(R.id.txtAlt);
        txtAdd = (TextView) findViewById(R.id.txtAdd);

        /*Calling getlocation*/
        getLocation();

    }

    /*Checks for permissions*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        /*Checks if permission granted*/
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                /*Calling startListening*/
                startListening();
            }
        }
    }

    public void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 5, locationListener);
        }
    }

    /*Gets location*/
    @SuppressLint("MissingPermission")
    public void getLocation() {
        /*Managers locations*/
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        /*Listens for locations*/
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {


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
        }

        ;

        /*Check if lower than Android 6.0.1*/
        if (Build.VERSION.SDK_INT < 23) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 5, locationListener);
        } else {
            /*Ask permission
             * 1 = One permission*/
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 5, locationListener);

                /*
                 * Getting last known location*/
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null) {
                    /*Calling updateLocation*/
                    updateLocation(lastKnownLocation);
                }

            }
        }
    }

    /*Updates location when needed*/
    public void updateLocation(Location location) {

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addressList != null && addressList.size() > 0) {

                String address = "";
                if (addressList.get(0).getThoroughfare() != null) {

                    /*Address*/
                    address += addressList.get(0).getThoroughfare() + " ";
                }

                if (addressList.get(0).getLocality() != null) {

                    /*City*/
                    address += addressList.get(0).getLocality() + " ";
                }

                if (addressList.get(0).getPostalCode() != null) {

                    /*Postal code*/
                    address += addressList.get(0).getPostalCode() + " ";
                }

                if (addressList.get(0).getAdminArea() != null) {

                    /*Province*/
                    address += addressList.get(0).getAdminArea() + " ";
                }

                if (addressList.get(0).getCountryName() != null) {

                    /*Country name*/
                    address += addressList.get(0).getCountryName() + " ";
                }

                if (addressList.get(0).getCountryCode() != null) {

                    /*Country code*/
                    address += addressList.get(0).getCountryCode() + " ";
                }

                if (addressList.get(0).getExtras() != null) {

                    /*Extras*/
                    address += addressList.get(0).getExtras();
                }

                Log.i("Address", address);

                txtLat.setText("Latitude: " + Double.toString(location.getLatitude()));
                txtLng.setText("Longitude: " + Double.toString(location.getLongitude()));
                txtAcc.setText("Accuracy: " + Float.toString(location.getAccuracy()));
                txtAlt.setText("Altitude: " + Double.toString(location.getAltitude()));
                txtAdd.setText("Address: " + address);
                Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
