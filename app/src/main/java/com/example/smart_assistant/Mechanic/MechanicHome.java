package com.example.smart_assistant.Mechanic;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.smart_assistant.Login;
import com.example.smart_assistant.R;
import com.example.smart_assistant.dbHelper;
import com.example.smart_assistant.publicClass;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MechanicHome extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private FloatingActionButton logout;

    private TextView fullname, phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_home);

        fullname = findViewById(R.id.mechaNames);
        logout = findViewById(R.id.mechaLogout);
        phone = findViewById(R.id.mechaEmail);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mechanicHomeMap);
        mapFragment.getMapAsync(this);

        logout.setOnClickListener(v -> {
            AlertDialog logoutAlert = new AlertDialog.Builder(this)
                    .setMessage("Are you sure?")
                    .setTitle("Logout")
                    .setCancelable(true)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        new dbHelper(this).logout();
                        startActivity(new Intent(MechanicHome.this, Login.class));
                        finish();
                    })
                    .setNegativeButton("No", ((dialog, which) -> {

                    }))
                    .create();

            logoutAlert.show();
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);


        fetchData();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
            return;
        }
    }

    private void fetchData() {
        AlertDialog loading = publicClass.loading(MechanicHome.this);
        loading.show();
        StringRequest request = new StringRequest(Request.Method.POST, publicClass.baseUrl + "getMechanicData",
                response -> {
                    loading.dismiss();
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equals("success")) {
                            fullname.setText(object.getString("fullname"));
                            phone.setText(object.getString("email"));
                            LatLng latLng = new LatLng(
                                    Double.parseDouble(object.getString("lat")),
                                    Double.parseDouble(object.getString("lng"))
                            );
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Garage location");
                            gMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            gMap.addMarker(markerOptions).setDraggable(true);
                        }
                    } catch (Exception e) {
                        Toast.makeText(MechanicHome.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loading.dismiss();
                    Toast.makeText(MechanicHome.this, "Connection error! Try again", Toast.LENGTH_SHORT).show();
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                try {
                    params.put("username", new dbHelper(MechanicHome.this).getData().getString("username"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };

        Volley.newRequestQueue(MechanicHome.this).add(request);
    }
}