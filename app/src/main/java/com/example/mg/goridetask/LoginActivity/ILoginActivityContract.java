package com.example.mg.goridetask.LoginActivity;

import android.content.Intent;

import com.facebook.AccessToken;
import com.facebook.FacebookException;

public interface ILoginActivityContract {
    interface IView {
        void showNoInternet(FacebookException error);

        void showLoginCanceled();

    }

    interface IActions {
        void onFacebookButttonClick();

        void getUserProfile(AccessToken mAccessToken);

        void checkIfLoggedIn();

        void onActivityResult(int requestCode, int resultCode, Intent data);

        void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

        void goToLocationActivity(String profile_name, String photoUrl);

        void onResume();

    }
}

