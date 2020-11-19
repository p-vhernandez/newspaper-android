package com.programming.user.interfaces.newspaper.network;

import com.programming.user.interfaces.newspaper.model.Image;
import com.programming.user.interfaces.newspaper.network.exceptions.ServerCommunicationError;
import com.programming.user.interfaces.newspaper.utils.Logger;
import com.programming.user.interfaces.newspaper.utils.ServiceCallUtils;

import java.net.HttpURLConnection;
import java.net.URL;

import static com.programming.user.interfaces.newspaper.utils.ServiceCallUtils.parseHttpStreamResult;

public class ImageREST {

    private static int saveImage(Image i) throws ServerCommunicationError {
        try {
            String parameters = "";
            String request = ModelManager.restConnection.serviceURL + ModelManager.ARTICLES_IMAGE_METHOD;
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (ModelManager.restConnection.requireSelfSigned) {
                TrustModifier.relaxHostChecking(connection);
            }

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Authorization", ModelManager.getAuthTokenHeader());
            connection.setRequestProperty("charset", "utf-8");
            connection.setUseCaches(false);

            ServiceCallUtils.writeJSONParams(connection, i.toJSON());
            int HttpResult = connection.getResponseCode();

            if (HttpResult == HttpURLConnection.HTTP_OK) {
                String res = parseHttpStreamResult(connection);

                // get id from status ok when saved
                int id = ServiceCallUtils.readRestResultFromInsert(res);
                Logger.log(Logger.INFO, "Object image saved with id:" + id);

                return id;
            } else {
                throw new ServerCommunicationError(connection.getResponseMessage());
            }
        } catch (Exception e) {
            Logger.log(Logger.ERROR, "Saving image [" + i + "] : " + e.getClass()
                    + " ( " + e.getMessage() + ")");
            throw new ServerCommunicationError(e.getClass() + " ( " + e.getMessage() + ")");
        }
    }

    private static void deleteImage(int idArticle) throws ServerCommunicationError {
        try {
            String parameters = "";
            String request = ModelManager.restConnection.serviceURL + ModelManager.IMAGE_METHOD + idArticle;
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
                Logger.log(Logger.INFO, "Image of article (id:" + idArticle + ") deleted with status " + HttpResult + ":" + parseHttpStreamResult(connection));
            } else {
                throw new ServerCommunicationError(connection.getResponseMessage());
            }
        } catch (Exception e) {
            Logger.log(Logger.ERROR, "Deleting image of article (id:" + idArticle + ") : " + e.getClass() + " ( " + e.getMessage() + ")");
            throw new ServerCommunicationError(e.getClass() + " ( " + e.getMessage() + ")");
        }
    }

}
