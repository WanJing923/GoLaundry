package com.example.golaundry.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.golaundry.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    Location LastLocation;
    Marker movingPin;
    private LatLng currentPinPosition;
    private boolean shouldUpdateLocation = true;
    String formattedAddress, area;

    public MapsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        //get permission
        if (ContextCompat.checkSelfPermission(MapsActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                onLocationChanged(LastLocation);
            } else {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        //intent back to previous activity
        findViewById(R.id.map_next_button).setOnClickListener(v -> returnWithLocation());

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        //let user location enable
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);

        //create the moving pin marker
        movingPin = mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Moving Pin"));

        //set up the OnCameraMoveStartedListener to detect camera movements
        mMap.setOnCameraMoveStartedListener(reason -> {
            // Check if the user initiated the camera movement
            if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                //user-initiated camera movement, do not update camera position
            } else {
                // update the camera to the current location
                updateCameraToCurrentLocation();
            }
        });

        //set up the OnMapClickListener to update current pin position when user clicks on the map
        mMap.setOnMapClickListener(latLng -> {
            shouldUpdateLocation = false; //disable auto updates
            //update current pin position
            currentPinPosition = latLng;
            //update position of the moving pin marker
            if (movingPin != null) {
                movingPin.setPosition(currentPinPosition);
            }
            //update the camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            //reverse geocoding to get address
            updateAddressForLocation(latLng);

            shouldUpdateLocation = true; //enable auto updates
            onConnected(null); //restart location updates
        });
    }

    private void updateAddressForLocation(LatLng latLng) {
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            assert addresses != null;
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                formattedAddress = address.getAddressLine(0);
                area = address.getLocality();
                // Update the address in the TextView
                TextView addressTextView = findViewById(R.id.mf_tv_address);
                addressTextView.setText(formattedAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void updateCameraToCurrentLocation() {
        if (LastLocation != null) {
            LatLng latLng = new LatLng(LastLocation.getLatitude(), LastLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (shouldUpdateLocation) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LastLocation = location;

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //set current pin position to the user's current location if not set yet
        if (currentPinPosition == null) {
            currentPinPosition = latLng;
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        // Perform reverse geocoding to get address
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!Objects.requireNonNull(addresses).isEmpty()) {
                Address address = addresses.get(0);
                area = address.getLocality();
                formattedAddress = address.getAddressLine(0); // full address
                TextView addressTextView = findViewById(R.id.mf_tv_address);
                addressTextView.setText(formattedAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //update moving pin's position
        if (movingPin != null) {
            movingPin.setPosition(latLng);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //intent back to previous activity
    public void returnWithLocation() {
        if (LastLocation != null) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("formattedAddress", formattedAddress);
            returnIntent.putExtra("area", area);
            setResult(RESULT_OK, returnIntent);
//            Log.d("MapsActivity", "Selected Area: " + area);
        }
        finish();
    }

}