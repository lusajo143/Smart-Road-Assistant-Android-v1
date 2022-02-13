package com.example.smart_assistant;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.smart_assistant.Mechanic.MechanicLocation;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private EditText fullname, pass, cpass, username;
    private RadioGroup group;
    private RadioButton rOwner, rMechanic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullname = findViewById(R.id.fullname);
        pass = findViewById(R.id.password);
        cpass = findViewById(R.id.cpassword);
        username = findViewById(R.id.username);
        group = findViewById(R.id.typeGroup);
        rOwner = findViewById(R.id.owner);
        rMechanic = findViewById(R.id.mechanic);

    }

    public void signup(View view) {

        if (username.getText().toString().equals("")) username.setError("Enter your username here");
        else if (fullname.getText().toString().equals(""))
            fullname.setError("Enter your full name here");
        else if (pass.getText().toString().equals("")) pass.setError("Enter your password here");
        else if (pass.getText().toString().length() < 2)
            pass.setError("Password must be greater than 4 characters");
        else if (cpass.getText().toString().equals(""))
            cpass.setError("Confirm your password here");
        else if (!pass.getText().toString().equals(cpass.getText().toString()))
            cpass.setError("Password does not match");
        else {

            int id = group.getCheckedRadioButtonId();
            RadioButton type = findViewById(id);

            AlertDialog loading = publicClass.loading(this);
            loading.show();

            StringRequest request = new StringRequest(Request.Method.POST, publicClass.baseUrl + "registration",
                    response -> {
                        loading.dismiss();
                        try {
                            JSONObject object = new JSONObject(response);
                            // Check if registration is success or Username is already taken
                            if (object.getString("status").equals("success")) {

                                if (type.getText().toString().equals("Car owner")) {
                                    startActivity(new Intent(Register.this, SetEmail.class).putExtra("username", username.getText().toString()));
                                } else {
                                    startActivity(new Intent(Register.this, MechanicLocation.class).putExtra("username", username.getText().toString()));
                                }

                            } else {
                                // Username already taken
                                publicClass.alert(Register.this, object.getString("message"));

                            }
                        } catch (Exception e) {
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        loading.dismiss();
                        Log.d("TAG", "signup: " + error.getMessage());
                        Toast.makeText(Register.this, "Connection error! Try again", Toast.LENGTH_SHORT).show();
                    }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("username", username.getText().toString());
                    params.put("fullname", fullname.getText().toString());
                    params.put("role", type.getText().toString());
                    params.put("password", pass.getText().toString());
                    return params;
                }
            };

            Volley.newRequestQueue(Register.this).add(request);
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void changeAccountType(View view) {
        switch (view.getId()) {
            case R.id.owner:
                rOwner.setBackgroundResource(R.drawable.active_left);
                rOwner.setTextColor(Color.WHITE);
                rMechanic.setBackgroundResource(R.drawable.outline_right);
                rMechanic.setTextColor(Color.BLACK);
                break;
            case R.id.mechanic:
                rMechanic.setBackgroundResource(R.drawable.active_right);
                rMechanic.setTextColor(Color.WHITE);
                rOwner.setBackgroundResource(R.drawable.outline_left);
                rOwner.setTextColor(Color.BLACK);
                break;
        }
    }


}