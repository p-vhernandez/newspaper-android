package com.programming.user.interfaces.newspaper.network;

import android.util.Log;

import com.programming.user.interfaces.newspaper.model.Article;
import com.programming.user.interfaces.newspaper.network.exceptions.ServerCommunicationError;
import com.programming.user.interfaces.newspaper.utils.Logger;
import com.programming.user.interfaces.newspaper.utils.ServiceCallUtils;

import org.json.simple.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.programming.user.interfaces.newspaper.utils.ServiceCallUtils.parseHttpStreamResult;

public class ArticlesREST {

    public static List<Article> getArticles(int buffer, int offset) throws ServerCommunicationError {
        List<Article> result = new ArrayList<>();
        String limits = "";

        if (buffer > 0 && offset >= 0) {
            limits = "/" + buffer + "/" + offset;
        }

        try {
            String parameters = "";
            String request = ModelManager.restConnection.serviceURL + ModelManager.ARTICLES_METHOD + limits;

            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (ModelManager.restConnection.requireSelfSigned) {
                TrustModifier.relaxHostChecking(connection);
            }

            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", ModelManager.getAuthTokenHeader());
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
            connection.setUseCaches(false);

            int HttpResult = connection.getResponseCode();

            if (HttpResult == HttpURLConnection.HTTP_OK) {
                String res = parseHttpStreamResult(connection);
                List<JSONObject> objects = ServiceCallUtils.readRestResultFromList(res);

                for (JSONObject jsonObject : objects) {
//                    if (jsonObject.get("thumbnail_image") != null
//                            || !Objects.requireNonNull(jsonObject.get("thumbnail_image")).equals("")) {
                        result.add(new Article(jsonObject));
//                    }
                }

                Logger.log(Logger.INFO, objects.size() + " objects (Article) retrieved");
            } else {
                throw new ServerCommunicationError(connection.getResponseMessage());
            }
        } catch (Exception e) {
            Logger.log(Logger.ERROR, "Listing articles :" + e.getClass() + " ( " + e.getMessage() + ")");
            throw new ServerCommunicationError(e.getClass() + " ( " + e.getMessage() + ")");
        }


        return result;
    }

    /**
     * @return the list of articles in remote service with pagination
     * @throws ServerCommunicationError
     */
    public static List<Article> getArticlesFrom(int buffer, int offset) throws ServerCommunicationError {
        String limits = "";

        if (buffer > 0 && offset >= 0) {
            limits = "/" + buffer + "/" + offset;
        }

        List<Article> result = new ArrayList<>();
        try {
            String parameters = "";
            String request = ModelManager.restConnection.serviceURL + ModelManager.ARTICLES_METHOD + limits;

            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (ModelManager.restConnection.requireSelfSigned) {
                TrustModifier.relaxHostChecking(connection);
            }

            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", ModelManager.getAuthTokenHeader());
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
            connection.setUseCaches(false);

            int HttpResult = connection.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                String res = parseHttpStreamResult(connection);
                List<JSONObject> objects = ServiceCallUtils.readRestResultFromList(res);
                for (JSONObject jsonObject : objects) {
                    result.add(new Article(jsonObject));
                }
                Logger.log(Logger.INFO, objects.size() + " objects (Article) retrieved");
            } else {
                throw new ServerCommunicationError(connection.getResponseMessage());
            }
        } catch (Exception e) {
            Logger.log(Logger.ERROR, "Listing articles :" + e.getClass() + " ( " + e.getMessage() + ")");
            throw new ServerCommunicationError(e.getClass() + " ( " + e.getMessage() + ")");
        }

        return result;
    }

    /**
     * @return the article in remote service with id idArticle
     * @throws ServerCommunicationError
     */
    public static Article getArticle(int idArticle) throws ServerCommunicationError {
        Article result;

        try {
            String parameters = "";
            String request = ModelManager.restConnection.serviceURL + ModelManager.ARTICLE_METHOD + "/" + idArticle;
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (ModelManager.restConnection.requireSelfSigned) {
                TrustModifier.relaxHostChecking(connection);
            }

            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", ModelManager.getAuthTokenHeader());
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
            connection.setUseCaches(false);

            int HttpResult = connection.getResponseCode();

            if (HttpResult == HttpURLConnection.HTTP_OK) {
                String res = parseHttpStreamResult(connection);
                JSONObject object = ServiceCallUtils.readRestResultFromGetObject(res);
                result = new Article(object);
                Logger.log(Logger.INFO, " object (Article) retrieved");
            } else {
                throw new ServerCommunicationError(connection.getResponseMessage());
            }
        } catch (Exception e) {
            Logger.log(Logger.ERROR, "Getting article :" + e.getClass() + " ( " + e.getMessage() + ")");
            throw new ServerCommunicationError(e.getClass() + " ( " + e.getMessage() + ")");
        }

        return result;
    }

    private static int saveArticle(Article a) throws ServerCommunicationError {
        try {
            String parameters = "";
            String request = ModelManager.restConnection.serviceURL + ModelManager.ARTICLES_METHOD;
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (ModelManager.restConnection.requireSelfSigned) {
                TrustModifier.relaxHostChecking(connection);
            }

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", ModelManager.getAuthTokenHeader());
            connection.setRequestProperty("charset", "utf-8");
            connection.setUseCaches(false);

            ServiceCallUtils.writeJSONParams(connection, a.toJSON());
            int HttpResult = connection.getResponseCode();

            if (HttpResult == HttpURLConnection.HTTP_OK) {
                String res = parseHttpStreamResult(connection);

                // get id from status ok when saved
                int id = ServiceCallUtils.readRestResultFromInsert(res);
                Logger.log(Logger.INFO, "Object inserted, returned id:" + id);

                return id;
            } else {
                throw new ServerCommunicationError(connection.getResponseMessage());
            }
        } catch (Exception e) {
            Logger.log(Logger.ERROR, "Inserting article [" + a + "] : " + e.getClass()
                    + " ( " + e.getMessage() + ")");
            throw new ServerCommunicationError(e.getClass() + " ( " + e.getMessage() + ")");
        }
    }

    private static void deleteArticle(int idArticle) throws ServerCommunicationError {
        try {
            String parameters = "";
            String request = ModelManager.restConnection.serviceURL + ModelManager.ARTICLES_METHOD + "/" + idArticle;
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (ModelManager.restConnection.requireSelfSigned) {
                TrustModifier.relaxHostChecking(connection);
            }

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.setRequestProperty("Authorization", ModelManager.getAuthTokenHeader());
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
            connection.setUseCaches(false);

            int HttpResult = connection.getResponseCode();

            if (HttpResult == HttpURLConnection.HTTP_OK || HttpResult == HttpURLConnection.HTTP_NO_CONTENT) {
                Logger.log(Logger.INFO, "Article (id:" + idArticle + ") deleted with status "
                        + HttpResult + ":" + parseHttpStreamResult(connection));
            } else {
                throw new ServerCommunicationError(connection.getResponseMessage());
            }
        } catch (Exception e) {
            Logger.log(Logger.ERROR, "Deleting article (id:" + idArticle + ") : "
                    + e.getClass() + " ( " + e.getMessage() + ")");
            throw new ServerCommunicationError(e.getClass() + " ( " + e.getMessage() + ")");
        }
    }

}
