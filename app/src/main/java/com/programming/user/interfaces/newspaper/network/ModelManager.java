package com.programming.user.interfaces.newspaper.network;

import android.content.Context;

import com.programming.user.interfaces.newspaper.model.Article;
import com.programming.user.interfaces.newspaper.network.exceptions.AuthenticationError;
import com.programming.user.interfaces.newspaper.network.exceptions.ServerCommunicationError;
import com.programming.user.interfaces.newspaper.utils.PreferencesManager;

import java.util.List;
import java.util.Properties;

public class ModelManager {

    public static RESTConnection restConnection = null;

    protected static final String LOGIN_METHOD = "login";
    protected static final String ARTICLES_METHOD = "articles";
    protected static final String ARTICLE_METHOD = "article";
    protected static final String ARTICLES_IMAGE_METHOD = "article/image";
    protected static final String IMAGE_METHOD = "image/";

    public static boolean isConnected() {
        return restConnection.idUser != null;
    }

    public static String getLoggedUser() {
        return restConnection.idUser;
    }

    public static String getLoggedAPIKey() {
        return restConnection.apikey;
    }

    public static String getLoggedAuthType() {
        return restConnection.authType;
    }

    public static boolean isAdministrator() { return restConnection.isAdministrator; }

    /**
     * @param ini Initializes entity manager urls and users
     * @throws AuthenticationError
     */
    public static void configureConnection(Properties ini) throws AuthenticationError {
        restConnection = new RESTConnection(ini);
    }

    public static void stayloggedin(String idUser, String apikey, String authType) {
        restConnection.idUser = idUser;
        restConnection.authType = authType;
        restConnection.apikey = apikey;
    }

    /**
     * @return user id logged in
     */
    public static String getIdUser() {
        return restConnection.idUser;
    }

    /**
     * @return auth token header for user logged in
     */
    protected static String getAuthTokenHeader() {
        return restConnection.authType + " apikey=" + restConnection.apikey;
    }

    /**
     * @return the list of articles in remote service
     * @throws ServerCommunicationError
     */
    public static List<Article> getArticles(Context context) throws ServerCommunicationError {
        if (PreferencesManager.getUserLoggedIn(context)) {
            return ArticlesREST.getArticlesFrom(-1, -1);
        } else {
            return ArticlesREST.getArticles(-1, -1);
        }
    }

}
