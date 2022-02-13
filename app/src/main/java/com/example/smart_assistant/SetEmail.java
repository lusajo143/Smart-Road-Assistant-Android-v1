package com.example.smart_assistant;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SetEmail extends AppCompatActivity {

    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);

        email = findViewById(R.id.email);

    }

    public void setEmail(View view) {
        if (email.getText().toString().equals(""))  email.setError("Enter your email here");
        else {
            AlertDialog loading = publicClass.loading(this);
            loading.show();

            StringRequest request = new StringRequest(Request.Method.POST, publicClass.baseUrl+"setEmail",
                    response -> {
                        loading.dismiss();
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getString("status").equals("success")) {
                                startActivity(new Intent(SetEmail.this, Setup.class));
                            } else {
                                publicClass.alert(SetEmail.this, object.getString("message"));
                            }
                        }catch (Exception e) {
                            Toast.makeText(SetEmail.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        loading.dismiss();
                        Log.d("TAG", "setEmail: "+error.getMessage());
                        Toast.makeText(SetEmail.this, "Connection Error! Try again", Toast.LENGTH_SHORT).show();
                    }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("username", getIntent().getStringExtra("username"));
                    params.put("email", email.getText().toString());
                    return params;
                }
            };

            Volley.newRequestQueue(this).add(request);

        }
    }

}