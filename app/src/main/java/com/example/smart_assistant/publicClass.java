package com.example.smart_assistant;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

public class publicClass {
    public static String baseUrl = "http://10.42.0.1:5000/api/v1/";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static AlertDialog loading(Context context) {
        return new AlertDialog.Builder(context)
                .setView(R.layout.alert_loading)
                .setCancelable(false)
                .create();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void alert(Context context, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(R.layout.alert_alert)
                .setCancelable(true)
                .create();
        alertDialog.show();

        TextView Message = alertDialog.findViewById(R.id.alertMessage);
        Message.setText(message);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void error(Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(R.layout.alert_error)
                .setCancelable(true)
                .create();
        alertDialog.show();
    }

}
