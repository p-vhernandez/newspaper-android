package com.programming.user.interfaces.newspaper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.programming.user.interfaces.newspaper.model.Article;
import com.programming.user.interfaces.newspaper.network.ModelManager;
import com.programming.user.interfaces.newspaper.network.exceptions.ServerCommunicationError;
import com.programming.user.interfaces.newspaper.tasks.LoadArticlesTask;

import java.util.List;

public class ArticleListActivity extends AppCompatActivity {

    private List<Article> allArticles;

    private ListView lvArticles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        initialize();
    }

    private void initialize() {
        Log.e("NEWSPAPER", "Initialize newspaper app");

        lvArticles = findViewById(R.id.listArticles);

        downloadArticles();
    }

    private void downloadArticles() {
        new Thread(() -> {
            try {
                allArticles = ModelManager.getArticles();
            } catch (ServerCommunicationError serverCommunicationError) {
                serverCommunicationError.printStackTrace();
            }
            Log.e("ARTICLES", "Articles downloaded");
        });
    }
}