package com.programming.user.interfaces.newspaper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.programming.user.interfaces.newspaper.login.LoginActivity;
import com.programming.user.interfaces.newspaper.model.Article;
import com.programming.user.interfaces.newspaper.network.LoginREST;
import com.programming.user.interfaces.newspaper.network.ModelManager;
import com.programming.user.interfaces.newspaper.network.RESTConnection;
import com.programming.user.interfaces.newspaper.network.exceptions.AuthenticationError;
import com.programming.user.interfaces.newspaper.network.exceptions.ServerCommunicationError;
import com.programming.user.interfaces.newspaper.tasks.LoadArticlesTask;
import com.programming.user.interfaces.newspaper.utils.adapters.ArticlesAdapder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class ArticleListActivity extends AppCompatActivity {

    private List<Article> allArticles;
    private List<Article> articlesToShow;

    private FloatingActionButton btnLogin;
    private FloatingActionButton btnLogout;

    private ListView lvArticles;

    private ArticlesAdapder adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        initialize();
        downloadArticles();
    }

    private void initialize() {
        lvArticles = findViewById(R.id.listArticles);
        btnLogin = findViewById(R.id.login_button);
        btnLogout = findViewById(R.id.logout_button);

        if (ModelManager.getIdUser() == null) {
            btnLogin.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);
        } else {
            btnLogin.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);
        }

        configureClickListeners();
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

    private void configureClickListeners() {
        btnLogin.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        btnLogout.setOnClickListener(view -> {
            ModelManager.restConnection.clear();
            onRestart();
        });
    }

    private void filterArticles() {
        // TODO
        adapter.setArticlesToShow((ArrayList<Article>) articlesToShow);
    }
}