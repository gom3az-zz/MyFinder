package com.example.mg.goridetask.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkHelper {
    private static NetworkHelper sNetworkHelper;

    public static synchronized NetworkHelper getInstance() {
        if (sNetworkHelper == null) {
            sNetworkHelper = new NetworkHelper();
        }
        return sNetworkHelper;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
