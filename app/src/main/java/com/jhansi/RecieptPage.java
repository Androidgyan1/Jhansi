package com.jhansi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RecieptPage extends AppCompatActivity {

    private TextView amountremain, payment_amount_receptPage, payment_time_receptPage, BalanceAmount_receptPage;

    String Token, CLIENTID;
    String url5 = "https://jklmc.gov.in/LKOPOSAPI/LKOPOSAPI/api/GetReceiptDetail";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reciept_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Token = sharedPreferences.getString("TKOEN", "N/A");


        amountremain = findViewById(R.id.client_ref_no_receptpage);
        payment_amount_receptPage = findViewById(R.id.payment_amount_receptPage);
        payment_time_receptPage = findViewById(R.id.payment_time_receptPage);
        BalanceAmount_receptPage = findViewById(R.id.BalanceAmount_receptPage);

        // Retrieve data from Intent
        CLIENTID = getIntent().getStringExtra("client_ref_no");

        String PAYMENTAMOUNTRECEPT = getIntent().getStringExtra("payment_amount");
        String PAYMENTTIME = getIntent().getStringExtra("payment_time");
        String BALANCEAMOUNT = getIntent().getStringExtra("BalanceAmount");
        String PAYMENTMODE = getIntent().getStringExtra("payment_mode"); // Retrieve payment mode

        // Display the received data
        amountremain.setText(CLIENTID);
        payment_amount_receptPage.setText(PAYMENTAMOUNTRECEPT);
        payment_time_receptPage.setText(PAYMENTTIME);
        BalanceAmount_receptPage.setText(BALANCEAMOUNT);


        // If payment mode is "Online Payment", skip the API call and directly move to the print screen
        if ("Online Payment".equals(PAYMENTMODE)) {
            Intent intent = new Intent(RecieptPage.this, PrintReceptScreen.class);
            intent.putExtra("ClntTxnReference", CLIENTID);
            intent.putExtra("TxnMsg", "Payment Successful");
            intent.putExtra("TxnTimere", PAYMENTTIME);
            intent.putExtra("RRN", "/A"); // Replace with actual RRN if available
            intent.putExtra("Name", "N/A"); // Replace with actual name if available
            intent.putExtra("FName", "N/A"); // Replace with actual father's name if available
            intent.putExtra("EmailID", "N/A"); // Replace with actual email if available
            intent.putExtra("Mobile", "N/A"); // Replace with actual mobile if available
            intent.putExtra("ReceivedBy", "N/A"); // Replace with actual received by if available
            intent.putExtra("JalkalId", "N/A"); // Replace with actual Jalkal ID if available
            intent.putExtra("Zone_Name", "N/A"); // Replace with actual zone name if available
            intent.putExtra("Ward_Name", "N/A"); // Replace with actual ward name if available
            intent.putExtra("Mohlla_Name", "N/A"); // Replace with actual mohalla name if available
            intent.putExtra("AmountRecived", PAYMENTAMOUNTRECEPT);
            intent.putExtra("Paymentmode", PAYMENTMODE);
            intent.putExtra("TotalTax", "N/A"); // Replace with actual total tax if available
            startActivity(intent);
            finish();
        } else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    RequestQueue queue = Volley.newRequestQueue(RecieptPage.this);

                    // Create JSON Object for Request Body
                    JSONObject requestBody = new JSONObject();
                    try {
                        requestBody.put("Token", Token);
                        requestBody.put("client_txn_ref_no", CLIENTID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url5, null,
                            new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    try {
                                        // Loop through the JSON Array
                                        for (int i = 0; i < response.length(); i++) {
                                            JSONObject obj = response.getJSONObject(i);
                                            String ClntTxnReference = obj.getString("ClntTxnReference");
                                            String TxnMsg = obj.getString("TxnMsg");
                                            String TxnTime = obj.getString("TxnTime");
                                            String RRN = obj.getString("RRN");
                                            String Name = obj.getString("Name");
                                            String FName = obj.getString("FName");
                                            String EmailID = obj.getString("EmailID");
                                            String Mobile = obj.getString("Mobile");
                                            String ReceivedBy = obj.getString("ReceivedBy");
                                            String JalkalId = obj.getString("JalkalId");
                                            String Zone_Name = obj.getString("Zone_Name");
                                            String Ward_Name = obj.getString("Ward_Name");
                                            String Mohlla_Name = obj.getString("Mohlla_Name");
                                            String AmountRecived = obj.getString("AmountRecived");
                                            String Paymentmode = obj.getString("Paymentmode");
                                            String TotalTax = obj.getString("TotalTax");
                                            // String Zone = obj.getString("zone");// Modify based on your API response
                                            // Move to NextActivity with Token
                                            Intent intent = new Intent(RecieptPage.this, PrintReceptScreen.class);
                                            intent.putExtra("ClntTxnReference", ClntTxnReference);
                                            intent.putExtra("TxnMsg", TxnMsg);
                                            intent.putExtra("TxnTimere", TxnTime);
                                            intent.putExtra("RRN", RRN);
                                            intent.putExtra("Name", Name);
                                            intent.putExtra("FName", FName);
                                            intent.putExtra("EmailID", EmailID);
                                            intent.putExtra("Mobile", Mobile);
                                            intent.putExtra("ReceivedBy", ReceivedBy);
                                            intent.putExtra("JalkalId", JalkalId);
                                            intent.putExtra("Zone_Name", Zone_Name);
                                            intent.putExtra("Ward_Name", Ward_Name);
                                            intent.putExtra("Mohlla_Name", Mohlla_Name);
                                            intent.putExtra("AmountRecived", AmountRecived);
                                            intent.putExtra("Paymentmode", Paymentmode);
                                            intent.putExtra("TotalTax", TotalTax);
                                            // intent.putExtra("ZONE", Zone); // Assuming username is mobile number
                                            startActivity(intent);
                                            finish();


                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(RecieptPage.this, "JSON Parsing Error", Toast.LENGTH_LONG).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("Volley Error", error.toString());
                                    Toast.makeText(RecieptPage.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
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
            }, 1);

        }
    }
}