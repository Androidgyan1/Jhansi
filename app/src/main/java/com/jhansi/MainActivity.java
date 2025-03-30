package com.jhansi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jhansi.HashUtils.HashUtils;
import com.permissionx.guolindev.PermissionX;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 101; // Unique code for permission request

    ProgressBar progressBar;

    private EditText editTextPassword,edittextUser;
    AppCompatButton Login;
    private static final String SERVER_URL = "https://jklmc.gov.in/LKOPOSAPI/LKOPOSAPI/api/GetRegistrationDetails"; // Replace with your actual API endpoint

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        editTextPassword = findViewById(R.id.password);
        edittextUser = findViewById(R.id.name);
        Login = findViewById(R.id.login);
        progressBar = findViewById(R.id.progress);

        /////Listner

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edittextUser.getText().toString();
                String password = editTextPassword.getText().toString();

                if (!username.isEmpty() && !password.isEmpty()) {
                    String hashedPassword = HashUtils.sha256(password);
                    sendLoginRequest(username, hashedPassword);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendLoginRequest(String username, String hashedPassword) {
        RequestQueue queue = Volley.newRequestQueue(this);
        progressBar.setVisibility(View.VISIBLE);

        // Create JSON Object for Request Body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("userid", username);
            requestBody.put("password", hashedPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, SERVER_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Loop through the JSON Array
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                String token = obj.getString("token");
                                String Zone = obj.getString("zone");
                                String opername = obj.getString("Opername");
                                String userId = obj.getString("Userid");// Modify based on your API response
                                // Move to NextActivity with Token
                                Intent intent = new Intent(MainActivity.this, DashBord.class);
                                intent.putExtra("TOKEN", token);
                                intent.putExtra("ZONE", Zone); // Assuming username is mobile number
                                startActivity(intent);
                                progressBar.setVisibility(View.GONE);
                                finish();

                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

// Save the token valu
                                editor.putString("TKOEN", token);
                                editor.apply(); // Don't forget to apply changes!


                                SharedPreferences Opername = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                                SharedPreferences.Editor opernameeditor = Opername.edit();

// Save the token valu
                                opernameeditor.putString("Opername", opername);
                                opernameeditor.apply(); // Don't forget to apply changes!


                                SharedPreferences UesrID = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                                SharedPreferences.Editor UserIdeditor = UesrID.edit();

// Save the token valu
                                UserIdeditor.putString("userId", userId);
                                UserIdeditor.apply(); // Don't forget to apply changes!

                                SharedPreferences ZoneID = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                                SharedPreferences.Editor ZoneEditor = ZoneID.edit();

// Save the token valu
                                ZoneEditor.putString("ZONE", Zone);
                                ZoneEditor.apply(); // Don't forget to apply changes!


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "JSON Parsing Error", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }) {

            @Override
            public byte[] getBody() {
                return requestBody.toString().getBytes(StandardCharsets.UTF_8);
            }
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json"); // Set JSON request format
                return headers;
            }


        };

        queue.add(jsonArrayRequest);
    }
    }
