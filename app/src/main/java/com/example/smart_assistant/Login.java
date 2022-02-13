package com.example.smart_assistant;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.smart_assistant.Mechanic.MechanicHome;
import com.example.smart_assistant.Owner.OwnerHome;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener {


    private Button register, login;
    private EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        register = findViewById(R.id.registerBtn);
        login = findViewById(R.id.loginBtn);
        username = findViewById(R.id.loginUsername);
        password = findViewById(R.id.loginpassword);

        register.setOnClickListener(v -> startActivity(new Intent(Login.this, Register.class)));

        login.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.loginBtn) {
            if (username.getText().toString().equals(""))
                username.setError("Enter your username here");
            else if (password.getText().toString().equals(""))
                password.setError("Enter password here");
            else {

                AlertDialog loading = publicClass.loading(Login.this);
                loading.show();

                StringRequest request = new StringRequest(Request.Method.POST, publicClass.baseUrl + "login",
                        response -> {
                            loading.dismiss();
                            try {

                                JSONObject object = new JSONObject(response);
                                if (object.getString("status").equals("success")) {
                                    new dbHelper(Login.this)
                                            .insertUser( username.getText().toString(), object.getJSONObject("data").getString("role"));
                                    if (object.getJSONObject("data").getString("role").equals("Car owner"))
                                        startActivity(new Intent(Login.this, OwnerHome.class));
                                    else
                                        startActivity(new Intent(Login.this, MechanicHome.class));
                                } else {
                                    publicClass.alert(Login.this, object.getString("message"));
                                }
                            } catch (Exception e) {
                                Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        },
                        error -> {
                            loading.dismiss();
                            Toast.makeText(Login.this, "Connection error! Try again", Toast.LENGTH_SHORT).show();
                        }) {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap params = new HashMap();
                        params.put("username", username.getText().toString());
                        params.put("password", password.getText().toString());
                        return params;
                    }
                };

                Volley.newRequestQueue(Login.this).add(request);
            }
        }
    }
}