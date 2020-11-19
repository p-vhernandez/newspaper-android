package com.programming.user.interfaces.newspaper.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.programming.user.interfaces.newspaper.ArticleListActivity;
import com.programming.user.interfaces.newspaper.R;
import com.programming.user.interfaces.newspaper.network.LoginREST;
import com.programming.user.interfaces.newspaper.network.ModelManager;
import com.programming.user.interfaces.newspaper.network.exceptions.AuthenticationError;

public class LoginActivity extends AppCompatActivity {

    private EditText eUsername;
    private EditText ePassword;

    private CheckBox cbLoggedIn;

    private Button btnLogin;

    private String username;
    private String password;

    private Boolean keepMeLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialize();
    }

    private void initialize() {
        eUsername = findViewById(R.id.edit_username);
        ePassword = findViewById(R.id.edit_password);
        cbLoggedIn = findViewById(R.id.logged_in_checkbox);
        btnLogin = findViewById(R.id.login_button);

        configureClickListeners();
    }

    private void configureClickListeners() {
        btnLogin.setOnClickListener(view -> {
            username = String.valueOf(eUsername.getText());
            password = String.valueOf(ePassword.getText());
            keepMeLoggedIn = cbLoggedIn.isChecked();

            userLogin();
        });
    }

    private void userLogin() {
        new Thread(() -> {
            try {
                LoginREST.login(username, password);

                if (keepMeLoggedIn) {
                    ModelManager.stayloggedin(ModelManager.getIdUser(),
                            ModelManager.getLoggedAPIKey(), ModelManager.getLoggedAuthType());
                }

                runOnUiThread(this::goBackToArticlesList);
            } catch (AuthenticationError authenticationError) {
                authenticationError.printStackTrace();
                // TODO: show error
            }
        }).start();
    }

    private void goBackToArticlesList() {
        Intent intent = new Intent(this, ArticleListActivity.class);
        startActivity(intent);
        finish();
    }

}
