package com.example.mg.goridetask.LocationActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mg.goridetask.R;
import com.example.mg.goridetask.Utils.DirectionsJSONParser;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationActivity extends AppCompatActivity implements ILocationActivityContract.IView {

    @BindView(R.id.text_profile_name)
    TextView mProfileName;
    @BindView(R.id.img_profile_image)
    ImageView mProfileImage;
    @BindView(R.id.toolbar)
    Toolbar toolbar;


    private static final String TAG = "LocationActivity";
    private static final String TAG2 = "savedLocationCheck";
    private LocationActivityPresenter mPresenter;
    private String savedLocationCheck = "0";
    SharedPreferences prefs = null;


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        prefs = this.getSharedPreferences("LatLng", MODE_PRIVATE);
        mPresenter = new LocationActivityPresenter(this, prefs, savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    @Override
    public void initView() {
        String name = getIntent().getStringExtra("PROFILE_NAME");
        String photo = getIntent().getStringExtra("PHOTO");
        mProfileName.setText(name);
        Glide.with(this)
                .load(Uri.parse(photo))
                .into(mProfileImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPresenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TAG2, savedLocationCheck);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                mPresenter.logOut();
                break;
            case R.id.custom_location:
                mPresenter.onCusomLocationClicked();
                break;
        }
        return true;
    }

    @Override
    public void drawRoute(List<List<HashMap<String, String>>> route, GoogleMap mMap) {
        ArrayList points = new ArrayList();
        PolylineOptions lineOptions = new PolylineOptions();
        MarkerOptions markerOpt = new MarkerOptions();
        for (int i = 0; i < route.size(); i++) {
            List<HashMap<String, String>> path = route.get(i);
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);
                points.add(position);
                if (j == path.size() / 2) {
                    markerOpt = new MarkerOptions();
                    markerOpt.position(new LatLng(lat, lng))
                            .title("Estimated Time")
                            .alpha(0)
                            .snippet(String.valueOf(DirectionsJSONParser.estimatedTime) + " mins");
                }
            }
            lineOptions = new PolylineOptions();
            lineOptions.addAll(points);
            lineOptions.width(12);
            lineOptions.color(Color.RED);
            lineOptions.geodesic(true);

        }

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.customwind, null);

                TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
                TextView tvSubTitle = (TextView) view.findViewById(R.id.tv_subtitle);

                tvTitle.setText(marker.getTitle());
                tvSubTitle.setText(marker.getSnippet());

                return view;
            }
        });

        if (points.size() != 0) {
            mMap.addPolyline(lineOptions);
            mMap.addMarker(markerOpt).showInfoWindow();
        }
    }

    @Override
    public void onRouteError() {
        Toast.makeText(this, "Couldn't find path!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDataError() {
        Toast.makeText(this, "check entered data!", Toast.LENGTH_SHORT).show();
    }

    public void setSavedLocationCheck(String locationCheck) {
        this.savedLocationCheck = locationCheck;
    }
}
