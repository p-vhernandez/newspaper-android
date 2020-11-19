package com.programming.user.interfaces.newspaper.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Base64;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SerializationUtils {

    private static final String DATE_FORMAT_MYSQL = "yyyy-MM-dd hh:mm:ss";

    public static Date dateFromString(String stringDate) throws java.text.ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_MYSQL);
        return sdf.parse(stringDate);
    }

    public static String dateToString(Date date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_MYSQL);
        //ALGM 2017/09/28 if date is null then use the current date
        return sdf.format((date != null) ? date : Calendar.getInstance().getTime());
    }

    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    public static String imgToBase64String(Bitmap image) {
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.PNG;
        int quality = 100;
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.NO_WRAP);
    }

    // Added try catch so it works
    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    public static Bitmap base64StringToImg(String input) {
        try {
            byte[] decodedBytes = Base64.decode(input, Base64.NO_WRAP);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    public static String encodeImage(Bitmap img) {
        // System.out.println("<img src=\"data:image/png;base64,"+base64String+"\">");
        return imgToBase64String(img);
    }

    public static Bitmap createScaledImage(Bitmap src, int w, int h) {
        int finalw = w;
        int finalh = h;
        double factor = 1.0d;

        if (src.getWidth() > src.getHeight()) {
            factor = ((double) src.getHeight() / (double) src.getWidth());
            finalh = (int) (finalw * factor);
        } else {
            factor = ((double) src.getWidth() / (double) src.getHeight());
            finalw = (int) (finalh * factor);
        }

        return Bitmap.createScaledBitmap(src, finalw, finalh, true);
    }

    public static String createScaledStrImage(String strSrc, int w, int h) {
        Bitmap src = base64StringToImg(strSrc);
        int finalw = w;
        int finalh = h;
        double factor = 1.0d;

        if (src != null) {
            if (src.getWidth() > src.getHeight()) {
                factor = ((double) src.getHeight() / (double) src.getWidth());
                finalh = (int) (finalw * factor);
            } else {
                factor = ((double) src.getWidth() / (double) src.getHeight());
                finalw = (int) (finalh * factor);
            }
        }

        Bitmap resizedImg = Bitmap.createScaledBitmap(src, finalw, finalh, true);
        return imgToBase64String(resizedImg);
    }

}
