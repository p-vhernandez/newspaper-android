package com.programming.user.interfaces.newspaper.network;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.security.auth.callback.Callback;

public class RESTClient {

    private static final String METHOD_POST = "POST";

    private static final String PROPERTY_CONTENT_TYPE = "Content-Type";
    private static final String PROPERTY_CHARSET = "charset";

    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String CHARSET_TYPE_UTF_8 = "utf-8";

    public static void POST(String method, Callback callback) {
        try {
            URL url = new URL(method);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (ModelManager.restConnection.requireSelfSigned) {
                TrustModifier.relaxHostChecking(connection);
            }

            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod(METHOD_POST);
            connection.setRequestProperty(PROPERTY_CONTENT_TYPE, CONTENT_TYPE_JSON);
            connection.setRequestProperty(PROPERTY_CHARSET, CHARSET_TYPE_UTF_8);
            connection.setUseCaches(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
