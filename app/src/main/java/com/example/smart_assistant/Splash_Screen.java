package com.example.smart_assistant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smart_assistant.Mechanic.MechanicHome;
import com.example.smart_assistant.Owner.OwnerHome;

import org.json.JSONException;
import org.json.JSONObject;

public class Splash_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {
            try {
                JSONObject data = new dbHelper(Splash_Screen.this).getData();
                if (data.getBoolean("found")) {
                    if (data.getString("role").equals("Mechanic"))
                        startActivity(new Intent(Splash_Screen.this, MechanicHome.class));
                    else if (data.getString("role").equals("Car owner"))
                        startActivity(new Intent(Splash_Screen.this, OwnerHome.class));
                } else startActivity(new Intent(Splash_Screen.this, Login.class));


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, 3000);

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}