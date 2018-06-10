package com.example.mg.goridetask.LocationActivity;

import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.google.android.gms.maps.GoogleMap;

import java.util.HashMap;
import java.util.List;

public interface ILocationActivityContract {
    interface IView {
        void initView();

        void drawRoute(List<List<HashMap<String, String>>> route, GoogleMap mMap);

        void onRouteError();

        void onDataError();

    }

    interface IActions {
        void logOut();

        Location getLastKnownLocation();

        void onCusomLocationClicked();

        //void onResume();


    }
}
