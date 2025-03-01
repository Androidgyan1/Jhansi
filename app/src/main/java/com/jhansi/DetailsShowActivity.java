package com.jhansi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.BreakIterator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetailsShowActivity extends AppCompatActivity {

   TextView textViewJalDetails,textViewownerDetails,textViewownerhousenameDetails,textViewownermobileDetails,textViewownerbilldateDetails
           ,isEligibleForDiscountDetails,discount_amountDetails,CurrentSurchargeDetails,AmountRecivedCurrentFYDetails
           ,TotalTaxDetails;
String jalkalId,Token;
    private static final String SERVER_URL3 = "https://jklmc.gov.in/LKOPOSAPI/LKOPOSAPI/api/FetchBillDetailAPINew"; // Replace with your actual API endpoint

    CheckBox checkBoxFullPayment, checkBoxPartialPayment;
    EditText editTextPaymentAmount;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details_show);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView btnBackDetails = findViewById(R.id.btnBack);
        btnBackDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Moves to the previous screen
            }
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Token = sharedPreferences.getString("TKOEN", "N/A");






        textViewJalDetails = findViewById(R.id.textViewJalDetails);
        textViewownerDetails = findViewById(R.id.textViewownerDetails);
        textViewownerhousenameDetails = findViewById(R.id.textViewownerhousenameDetails);
        textViewownermobileDetails = findViewById(R.id.textViewownermobileDetails);
        textViewownerbilldateDetails = findViewById(R.id.textViewownerbilldateDetails);
        isEligibleForDiscountDetails = findViewById(R.id.isEligibleForDiscountDetails);
        discount_amountDetails = findViewById(R.id.discount_amountDetails);
        CurrentSurchargeDetails = findViewById(R.id.CurrentSurchargeDetails);
        AmountRecivedCurrentFYDetails = findViewById(R.id.AmountRecivedCurrentFYDetails);
        TotalTaxDetails = findViewById(R.id.TotalTaxDetails);


        checkBoxFullPayment = findViewById(R.id.checkBoxFullPayment);
        checkBoxPartialPayment = findViewById(R.id.checkBoxPartialPayment);
        editTextPaymentAmount = findViewById(R.id.editTextPaymentAmount);

// Initially disable EditText
        editTextPaymentAmount.setEnabled(false);

        // Retrieve data from the Intent
        Intent intent = getIntent();
        jalkalId = intent.getStringExtra("JLalKal");
        String email = intent.getStringExtra("Email");
        String houseName = intent.getStringExtra("HouseName");
        String ownerMobile = intent.getStringExtra("OwnerMobile");
        String billDate = intent.getStringExtra("BillDate");
        String eligibilityForDiscount = intent.getStringExtra("EligibilityForDiscount");
        String discountAmount = intent.getStringExtra("DiscountAmount");
        String currentSurcharge = intent.getStringExtra("CurrentSurcharge");
        String amountReceivedCurrentFY = intent.getStringExtra("AmountReceivedCurrentFY");
        String totalTax = intent.getStringExtra("TotalTax");
        // Amount entered for partial payment

        textViewJalDetails.setText(jalkalId);
        textViewownerDetails.setText(email);
        textViewownerhousenameDetails.setText(houseName);
        textViewownermobileDetails.setText(ownerMobile);
        textViewownerbilldateDetails.setText(billDate);
        isEligibleForDiscountDetails.setText(eligibilityForDiscount);
        discount_amountDetails.setText(discountAmount);
        CurrentSurchargeDetails.setText(currentSurcharge);
        AmountRecivedCurrentFYDetails.setText(amountReceivedCurrentFY);
        TotalTaxDetails.setText(totalTax);


        checkBoxFullPayment.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxPartialPayment.setChecked(false);
                editTextPaymentAmount.setText(totalTax);
                editTextPaymentAmount.setEnabled(false);
            } else {
                editTextPaymentAmount.setText("");
            }
        });

        checkBoxPartialPayment.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxFullPayment.setChecked(false);
                editTextPaymentAmount.setEnabled(true);
                editTextPaymentAmount.setText("");
            } else {
                editTextPaymentAmount.setEnabled(false);
            }
        });



    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();  // Default behavior (closes activity)
    }





    public void Proceed(View view) {


        String paymentAmount = editTextPaymentAmount.getText().toString();

        if (paymentAmount.isEmpty()) {
            Toast.makeText(this, "Please enter a payment amount", Toast.LENGTH_SHORT).show();
            return;
        }



        // Validate input length before sending the request
        if (jalkalId.length() > 64 || paymentAmount.length() > 64 || Token.length() > 64) {
            Toast.makeText(this, "Input cannot exceed 64 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        String paymentType = checkBoxFullPayment.isChecked() ? "full" : "partial";
        RequestQueue queue = Volley.newRequestQueue(this);

        // Create JSON Object for Request Body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("jalkal_id", jalkalId);
            requestBody.put("PayAmt", paymentAmount);
            requestBody.put("payment_type", paymentType);
            requestBody.put("Token", Token);
            Log.d("parameter",requestBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, SERVER_URL3, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() > 0) {
                                JSONObject obj = response.getJSONObject(0); // Get first object in array
                                String status = obj.getString("status");

                                if (status.equals("904")) {
                                    String reason = obj.getString("reason");
                                    Toast.makeText(DetailsShowActivity.this, "Error: " + reason, Toast.LENGTH_LONG).show();
                                } else {
                                    String clientId = obj.getString("client_ref_no");
                                    String current_water_tax = obj.getString("current_water_tax");
                                    String water_arrer = obj.getString("water_arrer");
                                    String current_server_tax = obj.getString("current_server_tax");
                                    String sewer_arrear = obj.getString("sewer_arrear");
                                    String Arrer_surcharge = obj.getString("Arrer_surcharge");
                                    String bill_date = obj.getString("bill_date");
                                    // Convert bill_date to dd/MM/yyyy format
                                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US); // Adjust format based on server response
                                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                                    try {
                                        Date date = inputFormat.parse(bill_date);
                                        if (date != null) {
                                            bill_date = outputFormat.format(date);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    String discount_amount = obj.getString("discount_amount");
                                    String current_surcharge = obj.getString("current_surcharge");
                                    String total_payable_amount = obj.getString("total_payable_amount");
                                    String AmountToPay = obj.getString("AmountToPay");
                                    String OTSDiscountamt = obj.getString("OTSDiscountamt");
                                    String AdvanceAmt = obj.getString("AdvanceAmt");
                                    Intent intent = new Intent(DetailsShowActivity.this, PaymentDropdownactivty.class);
                                    intent.putExtra("paymentType", paymentType); // Pass payment type to next screen
                                    intent.putExtra("clientId", clientId);
                                    intent.putExtra("current_water_tax",current_water_tax);
                                    intent.putExtra("water_arrer",water_arrer);
                                    intent.putExtra("current_server_tax",current_server_tax);
                                    intent.putExtra("sewer_arrear",sewer_arrear);
                                    intent.putExtra("Arrer_surcharge",Arrer_surcharge);
                                    intent.putExtra("bill_date",bill_date);
                                    intent.putExtra("discount_amount",discount_amount);
                                    intent.putExtra("current_surcharge",current_surcharge);
                                    intent.putExtra("total_payable_amount",total_payable_amount);
                                    intent.putExtra("AmountToPay",AmountToPay);
                                    intent.putExtra("OTSDiscountamt",OTSDiscountamt);
                                    intent.putExtra("AdvanceAmt",AdvanceAmt);
                                    startActivity(intent);
                                }
                            } else {
                                Toast.makeText(DetailsShowActivity.this, "Empty Response", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DetailsShowActivity.this, response.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        Toast.makeText(DetailsShowActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
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
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        queue.add(jsonArrayRequest);
    }
    }