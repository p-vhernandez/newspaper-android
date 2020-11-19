package com.programming.user.interfaces.newspaper.network;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Objects;
import java.util.Properties;

public class RESTConnection {

    private Properties ini = null;

    protected String idUser = null;
    protected String authType;
    protected String apikey;
    protected String serviceURL;

    protected boolean isAdministrator = false;
    protected boolean requireSelfSigned = false;

    public static final String ATTR_LOGIN_USER = "username";
    public static final String ATTR_LOGIN_PASS = "password";
    public static final String ATTR_SERVICE_URL = "service_url";
    public static final String ATTR_REQUIRE_SELF_CERT = "require_self_signed_cert";
    public static final String ATTR_PROXY_HOST = "proxy_host";
    public static final String ATTR_PROXY_PORT = "proxy_port";
    public static final String ATTR_PROXY_USER = "proxy_user";
    public static final String ATTR_PROXY_PASS = "proxy_pass";
    public static final String ATTR_APACHE_AUTH_USER = "apache_auth_user";
    public static final String ATTR_APACHE_AUTH_PASS = "apache_auth_pass";
    public static final String ATTR_APACHE_AUTH_KEY = "apache_auth_key";
    public static final String ATTR_APACHE_AUTH_TYPE = "apache_auth_type";

    protected RESTConnection(Properties ini) {
        this.ini = ini;

        if (!ini.containsKey(ATTR_SERVICE_URL)) {
            throw new IllegalArgumentException("Required attribute '" + ATTR_SERVICE_URL + "' not found!");
        }

        // Disable auth from self signed certificates
        requireSelfSigned = (ini.containsKey(ATTR_REQUIRE_SELF_CERT)
                && ((String) Objects.requireNonNull(ini.get(ATTR_REQUIRE_SELF_CERT))).equalsIgnoreCase("TRUE"));

        // add proxy http/https to the system
        if (ini.contains(ATTR_PROXY_HOST) && ini.contains(ATTR_PROXY_PORT)) {
            String proxyHost = (String) ini.get(ATTR_PROXY_HOST);
            String proxyPort = (String) ini.get(ATTR_PROXY_PORT);

            System.setProperty("http.proxyHost", proxyHost);
            System.setProperty("http.proxyPort", proxyPort);
        }

        if (ini.contains(ATTR_PROXY_USER) && ini.contains(ATTR_PROXY_PASS)) {
            final String proxyUser = (String) ini.get(ATTR_PROXY_USER);
            final String proxyPassword = (String) ini.get(ATTR_PROXY_PASS);

            System.setProperty("http.proxyUser", proxyUser);
            System.setProperty("http.proxyPassword", proxyPassword);

            Authenticator.setDefault(
                    new Authenticator() {
                        public PasswordAuthentication getPasswordAuthentication() {
                            if (proxyPassword != null) {
                                return new PasswordAuthentication(proxyUser, proxyPassword.toCharArray());
                            } else {
                                return null;
                            }
                        }
                    }
            );
        }

        serviceURL = ini.getProperty(ATTR_SERVICE_URL);
    }

    public void clear() {
        idUser = null;
        authType = null;
        apikey = null;
    }

}
