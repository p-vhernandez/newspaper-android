package com.programming.user.interfaces.newspaper.network;

import com.programming.user.interfaces.newspaper.network.exceptions.AuthenticationError;
import static com.programming.user.interfaces.newspaper.utils.ServiceCallUtils.parseHttpStreamResult;

import com.programming.user.interfaces.newspaper.utils.Logger;
import com.programming.user.interfaces.newspaper.utils.ServiceCallUtils;

import org.json.simple.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class LoginREST {

    /**
     * Login onto remote service
     *
     * @param username user name
     * @param password user password
     * @throws AuthenticationError
     */
    @SuppressWarnings("unchecked")
    public static void login(String username, String password) throws AuthenticationError {
        String response;

        try {
            String request = ModelManager.restConnection.serviceURL + ModelManager.LOGIN_METHOD;
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
            connection.setRequestProperty("charset", "utf-8");
            connection.setUseCaches(false);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("username", username);
            jsonParam.put("passwd", password);

            ServiceCallUtils.writeJSONParams(connection, jsonParam);
            int HttpResult = connection.getResponseCode();

            if (HttpResult == HttpURLConnection.HTTP_OK) {
                response = parseHttpStreamResult(connection);

                JSONObject userJsonObject = ServiceCallUtils.readRestResultFromSingle(response);
                ModelManager.restConnection.idUser = Objects.requireNonNull(userJsonObject.get("user")).toString();
                ModelManager.restConnection.authType = Objects.requireNonNull(userJsonObject.get("Authorization")).toString();
                ModelManager.restConnection.apikey = Objects.requireNonNull(userJsonObject.get("apikey")).toString();
                ModelManager.restConnection.isAdministrator = userJsonObject.containsKey("administrator");
            } else {
                Logger.log(Logger.ERROR, connection.getResponseMessage());
                throw new AuthenticationError(connection.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new AuthenticationError(e.getMessage());
        }
    }

}
