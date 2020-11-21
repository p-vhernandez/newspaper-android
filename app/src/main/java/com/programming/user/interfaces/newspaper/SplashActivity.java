package com.programming.user.interfaces.newspaper;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.programming.user.interfaces.newspaper.network.LoginREST;
import com.programming.user.interfaces.newspaper.network.ModelManager;
import com.programming.user.interfaces.newspaper.network.RESTConnection;
import com.programming.user.interfaces.newspaper.network.exceptions.AuthenticationError;

import java.util.Properties;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Hide navigation bar
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        createServerConnection();
    }

    private void createServerConnection() {
        Properties restProperties = new Properties();
        restProperties.setProperty(RESTConnection.ATTR_SERVICE_URL, BuildConfig.SERVER_URL);
        restProperties.setProperty(RESTConnection.ATTR_REQUIRE_SELF_CERT, "TRUE");

        // Show splash screen for 2 seconds
        // before launching the app
        new Handler().postDelayed(() -> {
            try {
                ModelManager.configureConnection(restProperties);
                goToArticlesList();
            } catch (AuthenticationError authenticationError) {
                authenticationError.printStackTrace();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void goToArticlesList() {
        Intent intent = new Intent(this, ArticleListActivity.class);
        startActivity(intent);
        finish();
    }
}
