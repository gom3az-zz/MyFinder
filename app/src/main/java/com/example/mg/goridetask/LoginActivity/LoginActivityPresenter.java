package com.example.mg.goridetask.LoginActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.mg.goridetask.LocationActivity.LocationActivity;
import com.example.mg.goridetask.Utils.MapUtil;
import com.example.mg.goridetask.Utils.NetworkHelper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class LoginActivityPresenter implements ILoginActivityContract.IActions {
    private LoginActivity mView;
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PUBLIC_PROFILE = "public_profile";
    private static final String KEY_FRIENDS = "user_friends";
    private static final int REQUEST_CODE = 99;
    private CallbackManager callbackManager;

    LoginActivityPresenter(LoginActivity view) {
        this.mView = view;
    }

    @Override
    public void onResume() {
        MapUtil mapUtil = new MapUtil();
        mapUtil.checkPermissions(mView);

        if (MapUtil.isLocationEnabled(mView)) checkIfLoggedIn();
    }

    @Override
    public void checkIfLoggedIn() {
        if (Profile.getCurrentProfile() != null && NetworkHelper.isNetworkAvailable(mView) ) {
            try {
                String firstName = Profile.getCurrentProfile().getName();
                String id = Profile.getCurrentProfile().getId();
                URL img_value = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                String photoUrl = img_value.toString();
                goToLocationActivity(firstName, photoUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFacebookButttonClick() {
        callbackManager = CallbackManager.Factory.create();
        mView.loginButton.setReadPermissions(Arrays.asList(KEY_EMAIL, KEY_PUBLIC_PROFILE, KEY_FRIENDS));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallBacks());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (FacebookSdk.isFacebookRequestCode(requestCode)) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    checkIfLoggedIn();
                    break;
                case Activity.RESULT_CANCELED:
                    mView.finish();
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkIfLoggedIn();
            } else {
                mView.finish();
            }
        }
    }

    @Override
    public void getUserProfile(AccessToken mAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                mAccessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String profile_name = object.getString("name");
                            long fb_id = object.getLong("id");
                            URL img_value = new URL("https://graph.facebook.com/" + fb_id + "/picture?type=large");
                            String photoUrl = img_value.toString();

                            goToLocationActivity(profile_name, photoUrl);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void goToLocationActivity(String profile_name, String photoUrl) {
        Intent goToFbUserActivity = new Intent(mView, LocationActivity.class);
        goToFbUserActivity.putExtra("PROFILE_NAME", profile_name);
        goToFbUserActivity.putExtra("PHOTO", photoUrl);
        goToFbUserActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mView.startActivity(goToFbUserActivity);
    }


    class FacebookCallBacks implements FacebookCallback<LoginResult> {

        @Override
        public void onSuccess(LoginResult loginResult) {
            getUserProfile(loginResult.getAccessToken());
        }

        @Override
        public void onCancel() {
            mView.showLoginCanceled();
        }

        @Override
        public void onError(FacebookException error) {
            mView.showNoInternet(error);
        }
    }

}