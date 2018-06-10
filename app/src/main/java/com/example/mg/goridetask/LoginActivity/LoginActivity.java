package com.example.mg.goridetask.LoginActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.example.mg.goridetask.R;
import com.facebook.FacebookException;
import com.facebook.login.widget.LoginButton;

public class LoginActivity extends Activity implements ILoginActivityContract.IView,
        View.OnClickListener {
    LoginButton loginButton;
    private LoginActivityPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);
        mPresenter = new LoginActivityPresenter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
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
    public void showNoInternet(FacebookException error) {
        Toast.makeText(LoginActivity.this, "no network"+ error.getMessage(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void showLoginCanceled() {
        Toast.makeText(LoginActivity.this, "login canceled by user", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_button) mPresenter.onFacebookButttonClick();
    }

}

