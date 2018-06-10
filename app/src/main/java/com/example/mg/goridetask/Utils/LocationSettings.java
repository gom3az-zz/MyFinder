package com.example.mg.goridetask.Utils;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class LocationSettings {
  /*  private GoogleApiClient googleApiClient;
    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(mView.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(mView,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);
        }

    }
    private void EnableGPSAutoMatically() {
        GoogleApiClient googleApiClient = null;
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(mView)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            checkIfLoggedIn();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {

                                status.startResolutionForResult(mView, 1000);

                            } catch (IntentSender.SendIntentException e) {
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                            break;
                    }
                }
            });
        }
    }
    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(mView)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(mView.getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                getLocationPermission();
            else
                EnableGPSAutoMatically();
        } else
            EnableGPSAutoMatically();

    }
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(mView.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            checkIfLoggedIn();
        } else {
            ActivityCompat.requestPermissions(mView,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    11);
        }

    }*/
}
