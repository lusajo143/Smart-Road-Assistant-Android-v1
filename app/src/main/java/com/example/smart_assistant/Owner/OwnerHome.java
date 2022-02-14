package com.example.smart_assistant.Owner;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.smart_assistant.Login;
import com.example.smart_assistant.R;
import com.example.smart_assistant.dbHelper;
import com.example.smart_assistant.publicClass;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OwnerHome extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng latLng;
    private FloatingActionButton logout;
    private TextView ownerNames;
    private Button help;
    private Marker markerClose;
    private LatLng closestLatLng;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_home);

        logout = findViewById(R.id.ownerLogout);
        ownerNames = findViewById(R.id.ownerNames);
        help = findViewById(R.id.help);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.ownerMap);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        logout.setOnClickListener(v -> {
            AlertDialog logoutAlert = new AlertDialog.Builder(this)
                    .setMessage("Are you sure?")
                    .setTitle("Logout")
                    .setCancelable(true)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        new dbHelper(this).logout();
                        startActivity(new Intent(OwnerHome.this, Login.class));
                        finish();
                    })
                    .setNegativeButton("No", ((dialog, which) -> {

                    }))
                    .create();

            logoutAlert.show();
        });

        help.setOnClickListener(v -> {
            getCloseMechanic();
        });

        fetchName();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void getCloseMechanic() {
        AlertDialog loading = publicClass.loading(this);
        loading.show();
        StringRequest request = new StringRequest(Request.Method.GET, publicClass.baseUrl + "getMechanics",
                response -> {
                    loading.dismiss();
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equals("success")) {
                            JSONArray mechanics = object.getJSONArray("Mechanics");
                            if (mechanics.length() == 0) {
                                Toast.makeText(OwnerHome.this, "No mechanic found", Toast.LENGTH_SHORT).show();
                            } else {
                                JSONObject closest = null;
                                float distance = 0;

                                for (int i = 0; i < mechanics.length(); i++) {
                                    JSONObject mechanic = mechanics.getJSONObject(i);
                                    LatLng location = new LatLng(
                                            Double.parseDouble(mechanic.getString("lat")),
                                            Double.parseDouble(mechanic.getString("lng"))
                                    );

                                    float[] result = new float[1];
                                    Location.distanceBetween(latLng.latitude, latLng.longitude, location.latitude, location.longitude, result);

                                    if ((distance >= result[0] && distance != 0) || (distance == 0)) {
                                        distance = result[0];
                                        closest = mechanic;
                                    }

                                }


                                if (closestLatLng != null) {
                                    markerClose.remove();
                                }
                                closestLatLng = new LatLng(
                                        Double.parseDouble(closest.getString("lat")),
                                        Double.parseDouble(closest.getString("lng"))
                                );

                                MarkerOptions markerOptions = new MarkerOptions().position(closestLatLng).title(
                                        closest.getString("fullname")
                                ).icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                gMap.animateCamera(CameraUpdateFactory.newLatLng(closestLatLng));
                                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(closestLatLng, 15));
                                markerClose = gMap.addMarker(markerOptions);
                                markerClose.setVisible(true);

                                // Show Details
                                AlertDialog details = new AlertDialog.Builder(OwnerHome.this)
                                        .setView(R.layout.alert_close_mechanic)
                                        .setCancelable(true)
                                        .create();

                                details.show();

                                TextView fullName = details.findViewById(R.id.fullName);
                                TextView phone = details.findViewById(R.id.phoneNumber);
                                TextView lat = details.findViewById(R.id.latitude);
                                TextView lng = details.findViewById(R.id.longitude);

                                fullName.setText( closest.getString("fullname"));
                                phone.setText(closest.getString("email"));
                                lat.setText(closest.getString("lat"));
                                lng.setText(closest.getString("lng"));

                            }
                        } else {
                            publicClass.alert(OwnerHome.this, object.getString("message"));
                        }
                    } catch (Exception e) {
                        Toast.makeText(OwnerHome.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loading.dismiss();
                    Log.d("TAG", "getCloseMechanic: "+error.getMessage());
                    Toast.makeText(this, "Connection error! Try again", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void fetchName() {
        AlertDialog loading = publicClass.loading(this);
        loading.show();
        StringRequest request = new StringRequest(Request.Method.POST, publicClass.baseUrl + "getOwnerData",
                response -> {
                    loading.dismiss();
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status").equals("success")) {
                            ownerNames.setText(object.getString("fullname"));
                        }
                    } catch (Exception e) {
                        Toast.makeText(OwnerHome.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loading.dismiss();
                    Toast.makeText(OwnerHome.this, "Connection error! Try again", Toast.LENGTH_SHORT).show();
                }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                try {
                    params.put("username", new dbHelper(OwnerHome.this).getData().getString("username"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
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
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here!");
                gMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                gMap.addMarker(markerOptions);
            }
        });
    }
}