package com.example.mg.goridetask.LocationActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.mg.goridetask.R;
import com.example.mg.goridetask.Utils.DirectionsJSONParser;
import com.example.mg.goridetask.Utils.MapUtil;
import com.example.mg.goridetask.Utils.NetworkHelper;
import com.facebook.login.LoginManager;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class LocationActivityPresenter implements ILocationActivityContract.IActions,
        OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        MapUtil.onFinishLoadingData {

    private LocationActivity mView;
    private SharedPreferences mSharedPreferences;
    private Bundle savedInstanceState;
    private GoogleMap mMap;
    private Marker mDestMarker;
    private Location mLocation;
    private LocationManager mLocationManager;
    private LatLng mCurrent, mDestination;
    private MapUtil mMapUtil;
    private static final String TAG = "LocationActivityPresent";
    private static final String Key_Current_Location_lat = "Key_Current_Location_lat";
    private static final String Key_Current_Location_lng = "Key_Current_Location_lng";
    private static final String Key_Destination_lat = "Key_Destination_lat";
    private static final String Key_Destination_lng = "Key_Destination_lng";

    LocationActivityPresenter(LocationActivity view,
                              SharedPreferences preferences,
                              Bundle savedInstanceState) {
        mView = view;
        mSharedPreferences = preferences;
        this.savedInstanceState = savedInstanceState;
        mMapUtil = new MapUtil();
        SupportMapFragment mapFragment = (SupportMapFragment) mView.getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMapLongClickListener(this);
        onMyLocationButtonClick();
        if (savedInstanceState != null)
            if (savedInstanceState.getString("savedLocationCheck").equals("1")) {
                Double currlat = Double.valueOf(mSharedPreferences.getString(Key_Current_Location_lat, ""));
                Double currlng = Double.valueOf(mSharedPreferences.getString(Key_Current_Location_lng, ""));
                Double deslat = Double.valueOf(mSharedPreferences.getString(Key_Destination_lat, ""));
                Double destlng = Double.valueOf(mSharedPreferences.getString(Key_Destination_lng, ""));

                mCurrent = new LatLng(currlat, currlng);
                mDestination = new LatLng(deslat, destlng);
                onMapLongClick(mDestination);
            }

    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean onMyLocationButtonClick() {
        if (MapUtil.isLocationEnabled(mView) &&
                NetworkHelper.isNetworkAvailable(mView) &&
                mMapUtil.getLocationPermission(mView)) {

            mLocation = getLastKnownLocation();
            if (mLocation == null) {
                mLocation = new Location(LocationManager.GPS_PROVIDER);
                mLocation.setLatitude(mCurrent.latitude);
                mLocation.setLongitude(mCurrent.longitude);
            }

            double latitude = mLocation.getLatitude();
            double longitude = mLocation.getLongitude();

            CameraUpdate center =
                    CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);

            mMap.moveCamera(center);
            mMap.animateCamera(zoom);
        } else mMapUtil.checkPermissions(mView);

        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public void onCusomLocationClicked() {
        final Dialog builder = new Dialog(mView);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.setContentView(R.layout.custom_dialog);
        Button locate = builder.findViewById(R.id.btn_locate);
        final EditText longitudeText = builder.findViewById(R.id.text_long);
        final EditText latitudeText = builder.findViewById(R.id.text_lat);

        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!latitudeText.getText().toString().equals("") &&
                        !longitudeText.getText().toString().equals("")) {

                    double longitude = Double.valueOf(longitudeText.getText().toString());
                    double latitude = Double.valueOf(latitudeText.getText().toString());
                    LatLng destination = new LatLng(longitude, latitude);
                    onMapLongClick(destination);
                    builder.dismiss();
                } else if (latitudeText.getText().toString().equals("") &&
                        longitudeText.getText().toString().equals("")) {

                    mView.onDataError();
                    latitudeText.setError("required");
                    longitudeText.setError("required");

                } else if (latitudeText.getText().toString().equals("")) {

                    mView.onDataError();
                    latitudeText.setError("required");
                } else {

                    mView.onDataError();
                    longitudeText.setError("required");
                }
            }
        });
        builder.show();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (MapUtil.isLocationEnabled(mView) && NetworkHelper.isNetworkAvailable(mView)) {
            mMap.clear();
            mCurrent = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            mDestination = latLng;
            mMap.addMarker(new MarkerOptions()
                    .position(mCurrent)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            if (mDestMarker != null) {
                mDestMarker.remove();
                mDestMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            } else {
                mDestMarker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            }
            mView.setSavedLocationCheck("1");

            mSharedPreferences.edit().putString(Key_Destination_lat, String.valueOf(mDestination.latitude)).apply();
            mSharedPreferences.edit().putString(Key_Destination_lng, String.valueOf(mDestination.longitude)).apply();
            mSharedPreferences.edit().putString(Key_Current_Location_lat, String.valueOf(mCurrent.latitude)).apply();
            mSharedPreferences.edit().putString(Key_Current_Location_lng, String.valueOf(mCurrent.longitude)).apply();
            String url = DirectionsJSONParser.getDirectionsUrl(mCurrent, mDestination);
            MapUtil mapUtil = new MapUtil();
            mapUtil.downloadRoutes(url, mView, this);
        } else mMapUtil.checkPermissions(mView);
    }

    @Override
    public void routeData(String response) {

        final List<List<HashMap<String, String>>> routes;
        final JSONObject jObject;

        try {
            jObject = new JSONObject(response);
            String status = jObject.getString("status");
            if (status.equals("ZERO_RESULTS"))
                mView.onRouteError();
            else {
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
                mView.drawRoute(routes, mMap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case Activity.RESULT_CANCELED:
                mView.finish();
                break;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 99) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_DENIED) mView.finish();

        }
    }

    @Override
    public void logOut() {
        LoginManager.getInstance().logOut();
        mView.finish();
    }
}
