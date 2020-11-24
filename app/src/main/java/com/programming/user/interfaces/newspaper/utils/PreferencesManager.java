package com.programming.user.interfaces.newspaper.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

    private static final String AUTHENTICATION_PREFERENCES_NAME = "s4r3dpr3f3r3nc3sn4m3";
    private static final String USER_LOGGED_IN = "us3rl0gg3d1n";
    private static final String USER_ID = "us3r1d";
    private static final String USER_NAME = "us3rn4m3";
    private static final String USER_API_KEY = "us3r4p1k3y";
    private static final String USER_AUTH_TYPE = "us3r4uthtyp3";
    private static final String USER_ADMIN = "us3r4dm1n";

    public static void setUserLoggedIn(Context context, boolean logged) {
        SharedPreferences preferences = context.getSharedPreferences(AUTHENTICATION_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(USER_LOGGED_IN, logged);
        editor.apply();
    }

    public static boolean getUserLoggedIn(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AUTHENTICATION_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        return preferences.getBoolean(USER_LOGGED_IN, false);
    }

    public static void setUserID(Context context, String id) {
        SharedPreferences preferences = context.getSharedPreferences(AUTHENTICATION_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_ID, id);
        editor.apply();
    }

    public static String getUserID(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AUTHENTICATION_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        return preferences.getString(USER_ID, "");
    }

    public static void setUserName(Context context, String username) {
        SharedPreferences preferences = context.getSharedPreferences(AUTHENTICATION_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_NAME, username);
        editor.apply();
    }

    public static String getUserName(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AUTHENTICATION_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        return preferences.getString(USER_NAME, "");
    }

    public static void setUserApiKey(Context context, String apiKey) {
        SharedPreferences preferences = context.getSharedPreferences(AUTHENTICATION_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_API_KEY, apiKey);
        editor.apply();
    }

    public static String getUserApiKey(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AUTHENTICATION_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        return preferences.getString(USER_API_KEY, "");
    }

    public static void setUserAuthType(Context context, String authType) {
        SharedPreferences preferences = context.getSharedPreferences(AUTHENTICATION_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_AUTH_TYPE, authType);
        editor.apply();
    }

    public static String getUserAuthType(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AUTHENTICATION_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        return preferences.getString(USER_AUTH_TYPE, "");
    }

    public static void setUserAdmin(Context context, boolean admin) {
        SharedPreferences preferences = context.getSharedPreferences(AUTHENTICATION_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(USER_ADMIN, admin);
        editor.apply();
    }

    public static boolean getUserAdmin(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AUTHENTICATION_PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        return preferences.getBoolean(USER_ADMIN, false);
    }

}
