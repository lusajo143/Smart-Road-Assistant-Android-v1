package com.example.smart_assistant.Mechanic;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.smart_assistant.R;
import com.example.smart_assistant.publicClass;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MechanicLocation extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap gMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng garageLocation = null;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_location);


        next = findViewById(R.id.chooseLocationNext);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mechanicLocationMap);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        next.setOnClickListener(v -> {
            if (garageLocation != null) {
                setLocation(getIntent().getStringExtra("id"), garageLocation.latitude, garageLocation.longitude);
            }
        });

    }

    private void setLocation(String id, double latitude, double longitude) {
        AlertDialog loading = publicClass.loading(MechanicLocation.this);
        loading.show();
        StringRequest request = new StringRequest(Request.Method.POST, publicClass.baseUrl + "setLocation.php",
                response -> {
                    loading.dismiss();
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("resp").equals("done")) {
                            startActivity(new Intent(MechanicLocation.this, MechanicHome.class).putExtra("id", id));
                        } else {
                            Toast.makeText(MechanicLocation.this, "Server error! Try again", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(MechanicLocation.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loading.dismiss();
                    Toast.makeText(MechanicLocation.this, "Connection error! Try again", Toast.LENGTH_SHORT).show();
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("lat", latitude + "");
                params.put("long", longitude + "");
                return params;
            }
        };

        Volley.newRequestQueue(MechanicLocation.this)
                .add(request);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                garageLocation = new LatLng(location.getLatitude(), location.getLongitude());
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here!");
                gMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                gMap.addMarker(markerOptions).setDraggable(true);
            }
        });

        gMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                garageLocation = marker.getPosition();
            }
        });
    }
}