package com.programming.user.interfaces.newspaper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.programming.user.interfaces.newspaper.login.LoginActivity;
import com.programming.user.interfaces.newspaper.model.Article;
import com.programming.user.interfaces.newspaper.network.ModelManager;
import com.programming.user.interfaces.newspaper.network.exceptions.ServerCommunicationError;
import com.programming.user.interfaces.newspaper.utils.PreferencesManager;
import com.programming.user.interfaces.newspaper.utils.adapters.ArticlesAdapder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArticleListActivity extends ActivityTemplate {

    private List<Article> allArticles;
    private List<Article> articlesToShow;

    private FloatingActionButton btnLogin;
    private FloatingActionButton btnLogout;

    private ListView lvArticles;

    private ArticlesAdapder adapter;

    private TextView tvWelcomeBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        initialize();
        downloadArticles();
    }

    private void initialize() {
        tvWelcomeBack = findViewById(R.id.welcome_back);
        lvArticles = findViewById(R.id.listArticles);
        btnLogin = findViewById(R.id.login_button);
        btnLogout = findViewById(R.id.logout_button);

        checkUserLoggedIn();
        configureClickListeners();
    }

    private void checkUserLoggedIn() {
        if (ModelManager.getIdUser() == null) {
            btnLogin.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);
            tvWelcomeBack.setVisibility(View.GONE);
        } else {
            btnLogin.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);

            String txtWelcome = getResources().getString(R.string.welcome_back_home_page) + " " +
                    PreferencesManager.getUserName(this) + "!";
            tvWelcomeBack.setText(txtWelcome);
            tvWelcomeBack.setVisibility(View.VISIBLE);
        }
    }

    private void configureClickListeners() {
        btnLogin.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(view -> {
            ModelManager.restConnection.clear();

            PreferencesManager.setUserLoggedIn(this, false);
            PreferencesManager.setUserName(this, "");
            PreferencesManager.setUserApiKey(this, "");

            recreate();
        });
    }

    private void downloadArticles() {
        new Thread(() -> {
            try {
                allArticles = ModelManager.getArticles();
                articlesToShow = allArticles;
                runOnUiThread(this::configureAdapter);
            } catch (ServerCommunicationError e) {
                e.printStackTrace();
                // TODO: show error
            }
        }).start();
    }

    private void configureAdapter() {
        Collections.sort(allArticles);
        adapter = new ArticlesAdapder((ArrayList<Article>) articlesToShow);
        lvArticles.setAdapter(adapter);
    }

    private void filterArticles() {
        // TODO
        adapter.setArticlesToShow((ArrayList<Article>) articlesToShow);
    }
}