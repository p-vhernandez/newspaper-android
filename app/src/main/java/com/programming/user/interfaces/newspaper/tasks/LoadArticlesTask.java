package com.programming.user.interfaces.newspaper.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.programming.user.interfaces.newspaper.BuildConfig;
import com.programming.user.interfaces.newspaper.model.Article;
import com.programming.user.interfaces.newspaper.network.ArticlesREST;
import com.programming.user.interfaces.newspaper.network.LoginREST;
import com.programming.user.interfaces.newspaper.network.ModelManager;
import com.programming.user.interfaces.newspaper.network.exceptions.AuthenticationError;
import com.programming.user.interfaces.newspaper.network.exceptions.ServerCommunicationError;

import java.util.List;

public class LoadArticlesTask extends AsyncTask<Void, Void, List<Article>> {

    private static final String TAG = "LoadArticlesTask";

    @Override
    protected List<Article> doInBackground(Void... voids) {
        List<Article> allArticles = null;

        // ModelManager uses singleton pattern,
        // connecting once per app execution in enough
        if (!ModelManager.isConnected()) {
            if (ModelManager.getIdUser() == null
                    || ModelManager.getIdUser().equals("")) {
                // First login
                try {
                    LoginREST.login(BuildConfig.GROUP_ID, BuildConfig.GROUP_PSWD);
                } catch (AuthenticationError e) {
                    e.printStackTrace();
                }
            } else {
                // We have user credentials from previous connections
                ModelManager.stayloggedin(ModelManager.getLoggedUser(),
                        ModelManager.getLoggedAPIKey(), ModelManager.getLoggedAuthType());
            }
        }

        // Successful connection
        if (ModelManager.isConnected()) {
            try {
                // Obtain 6 articles from offset 0
                allArticles = ArticlesREST.getArticles(6, 0);

                for (Article article: allArticles) {
                    Log.d(TAG, article.toString());
                }
            } catch (ServerCommunicationError e) {
                e.printStackTrace();
            }
        }

        return  allArticles;
    }
}
