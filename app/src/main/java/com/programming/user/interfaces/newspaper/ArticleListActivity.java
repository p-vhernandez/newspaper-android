package com.programming.user.interfaces.newspaper;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.programming.user.interfaces.newspaper.model.Article;
import com.programming.user.interfaces.newspaper.network.ModelManager;
import com.programming.user.interfaces.newspaper.network.RESTConnection;
import com.programming.user.interfaces.newspaper.network.exceptions.AuthenticationError;
import com.programming.user.interfaces.newspaper.network.exceptions.ServerCommunicationError;
import com.programming.user.interfaces.newspaper.tasks.LoadArticlesTask;

import java.util.List;
import java.util.Properties;

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
        lvArticles = findViewById(R.id.listArticles);
        initializeProperties();
    }

    private void initializeProperties() {
        Properties restProperties = new Properties();
        restProperties.setProperty(RESTConnection.ATTR_SERVICE_URL, BuildConfig.SERVER_URL);
        restProperties.setProperty(RESTConnection.ATTR_REQUIRE_SELF_CERT, "TRUE");
//        restProperties.setProperty(RESTConnection.ATTR_PROXY_HOST, "");
//        restProperties.setProperty(RESTConnection.ATTR_PROXY_PORT, "");
//        restProperties.setProperty(RESTConnection.ATTR_PROXY_USER, BuildConfig.GROUP_ID);
//        restProperties.setProperty(RESTConnection.ATTR_PROXY_PASS, BuildConfig.GROUP_PSWD);

        configureConnection(restProperties);
    }

    private void configureConnection(Properties restProperties) {
        try {
            ModelManager.configureConnection(restProperties);
            downloadArticles();
        } catch (AuthenticationError authenticationError) {
            authenticationError.printStackTrace();
        }
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