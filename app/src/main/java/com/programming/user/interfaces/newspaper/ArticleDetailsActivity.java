package com.programming.user.interfaces.newspaper;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.programming.user.interfaces.newspaper.network.ArticlesREST;
import com.programming.user.interfaces.newspaper.network.exceptions.ServerCommunicationError;

public class ArticleDetailsActivity extends AppCompatActivity {

    public static final String INTENT_ARTICLE_ID = "4rticl31d";

    private int articleID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        initialize();

        Intent intent = getIntent();
        articleID = intent.getIntExtra(INTENT_ARTICLE_ID, -1);

        if (articleID != -1) {
            downloadArticleInfo();
        } else {
            // TODO: show error ? & go back to list
        }
    }

    private void initialize() {

    }

    private void downloadArticleInfo() {
        new Thread(() -> {
            try {
                ArticlesREST.getArticle(articleID);
            } catch (ServerCommunicationError serverCommunicationError) {
                serverCommunicationError.printStackTrace();
            }
        }).start();
    }
}
