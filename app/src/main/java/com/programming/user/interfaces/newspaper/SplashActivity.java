package com.programming.user.interfaces.newspaper;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.programming.user.interfaces.newspaper.network.LoginREST;
import com.programming.user.interfaces.newspaper.network.ModelManager;
import com.programming.user.interfaces.newspaper.network.RESTConnection;
import com.programming.user.interfaces.newspaper.network.exceptions.AuthenticationError;

import java.util.Properties;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        createServerConnection();
    }

    private void createServerConnection() {
        Properties restProperties = new Properties();
        restProperties.setProperty(RESTConnection.ATTR_SERVICE_URL, BuildConfig.SERVER_URL);
        restProperties.setProperty(RESTConnection.ATTR_REQUIRE_SELF_CERT, "TRUE");

        try {
            ModelManager.configureConnection(restProperties);
            new Thread(() -> {
                try {
                    LoginREST.login(BuildConfig.GROUP_ID, BuildConfig.GROUP_PSWD);
                    Log.e("LOGIN", ModelManager.getIdUser());
//                    goToArticlesList();
                } catch (AuthenticationError authenticationError) {
                    authenticationError.printStackTrace();
                    // TODO: show error
                }
            });
        } catch (AuthenticationError authenticationError) {
            authenticationError.printStackTrace();
        }
    }

    private void goToArticlesList() {
        Intent intent = new Intent(this, ArticleListActivity.class);
        startActivity(intent);
    }
}
