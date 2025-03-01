package com.jhansi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jhansi.Model.SearchModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomePage extends AppCompatActivity {
    ProgressBar progressBar;

    private ArrayList<SearchModel> dataList;
    private TextView textViewMobileZone;

    private String token, mobileNumberZone,usersearch;

    private String URL = "https://jklmc.gov.in/LKOPOSAPI/LKOPOSAPI/api/GetDetail";

    EditText edittextSearch;

    private AppCompatButton search;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        search = findViewById(R.id.search);

        edittextSearch = findViewById(R.id.edittextSearch);

        // Initialize TextView
        textViewMobileZone = findViewById(R.id.textViewMobileZone);

        progressBar = findViewById(R.id.progress);

        // Retrieve Token & Mobile Number from Intent
        token = getIntent().getStringExtra("TOKEN");
        mobileNumberZone = getIntent().getStringExtra("ZONE");

        // Set Mobile Number to TextView
        textViewMobileZone.setText("Zone ID: " + mobileNumberZone);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobileNumber = edittextSearch.getText().toString().trim();
                if (mobileNumber.isEmpty()) {
                    Toast.makeText(HomePage.this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
                } else {
                    fetchUserData(mobileNumber);
                }
            }
        });

    }

    private void fetchUserData(String mobileNumber) {
        RequestQueue queue = Volley.newRequestQueue(this);
        progressBar.setVisibility(View.VISIBLE);
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("Token", token);
            requestBody.put("search_text", mobileNumber);
            requestBody.put("ZoneNo", mobileNumberZone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.POST, URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            dataList = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                String name = obj.getString("jalkal_id");
                                String email = obj.getString("owner_name");
                                String housename = obj.getString("house_no");
                                String housemobile = obj.getString("mobile_no");
                                String Billdate = obj.getString("bill_date");
                                // Convert bill_date to dd/MM/yyyy format
                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US); // Adjust format based on server response
                                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                                try {
                                    Date date = inputFormat.parse(Billdate);
                                    if (date != null) {
                                        Billdate = outputFormat.format(date);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                String isEligibleForDiscount = obj.getString("isEligibleForDiscount");
                                String discount_amount = obj.getString("discount_amount");
                                String CurrentSurcharge = obj.getString("CurrentSurcharge");
                                String AmountRecivedCurrentFY = obj.getString("AmountRecivedCurrentFY");
                                String TotalTax = obj.getString("TotalTax");

                                // Add Data to List
                                dataList.add(new SearchModel(name, email,housename,housemobile,Billdate,isEligibleForDiscount,discount_amount,CurrentSurcharge,
                                        AmountRecivedCurrentFY,TotalTax));
                            }

                            // Move to DisplayActivity with Data
                            Intent intent = new Intent(HomePage.this, SearchDetails.class);
                            intent.putExtra("DATA_LIST", dataList);
                            startActivity(intent);
                            progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(HomePage.this, "JSON Parsing Error", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.getLocalizedMessage());
                        Toast.makeText(HomePage.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
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
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + token); // If API requires Authorization Header
                return headers;
            }
        };

        queue.add(jsonRequest);
    }

    }
