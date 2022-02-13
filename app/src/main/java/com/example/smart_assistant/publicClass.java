package com.example.smart_assistant;

import android.app.AlertDialog;
import android.content.Context;

public class publicClass {
    public static String baseUrl = "http://10.42.0.1:5000/api/v1/";

    public static AlertDialog loading(Context context) {
        return new AlertDialog.Builder(context)
                .setMessage("Please wait...")
                .setCancelable(false)
                .create();
    }

    public static void alert(Context context, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(true)
                .create();
        alertDialog.show();
    }

}
