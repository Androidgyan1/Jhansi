package com.jhansi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PaymentGateway extends AppCompatActivity implements PaymentResultListener {

    private String amountToPay;
    private String receivedText;
    private String paymentTypeString;
    private String total_payable_amount;
    private String AmountToPay;
    private String userIdeditor;
    private String Opernameeditor;
    private String latitude;
    private String longitude;
    private String Token;
    private String AdvanceAmt;

    // TextViews to display data
    private TextView textViewClientId, textViewPaymentType, textViewTotalAmount, textViewAmountToPay,
            textViewUserId, textViewOpername, textViewLatitude, textViewLongitude, textViewToken, textViewAdvanceAmt;

    private static final String URL4 = "https://jklmc.gov.in/LKOPOSAPI/LKOPOSAPI/api/UpdatePaymentApi";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_gateway);

        // Initialize TextViews
        textViewClientId = findViewById(R.id.textViewClientId);
        textViewPaymentType = findViewById(R.id.textViewPaymentType);
        textViewTotalAmount = findViewById(R.id.textViewTotalAmount);
        textViewAmountToPay = findViewById(R.id.textViewAmountToPay);
        textViewUserId = findViewById(R.id.textViewUserId);
        textViewOpername = findViewById(R.id.textViewOpername);
        textViewLatitude = findViewById(R.id.textViewLatitude);
        textViewLongitude = findViewById(R.id.textViewLongitude);
        textViewToken = findViewById(R.id.textViewToken);
        textViewAdvanceAmt = findViewById(R.id.textViewAdvanceAmt);

        // Retrieve data from Intent
        receivedText = getIntent().getStringExtra("clientId");
        paymentTypeString = getIntent().getStringExtra("paymentType");
        total_payable_amount = getIntent().getStringExtra("total_payable_amount");
        AmountToPay = getIntent().getStringExtra("AmountToPay");
        userIdeditor = getIntent().getStringExtra("userId");
        Opernameeditor = getIntent().getStringExtra("Opername");
        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");
        Token = getIntent().getStringExtra("Token");
        AdvanceAmt = getIntent().getStringExtra("AdvanceAmt");

        // Display data in TextViews
        textViewClientId.setText("Client ID: " + receivedText);
        textViewPaymentType.setText("Payment Type: " + paymentTypeString);
        textViewTotalAmount.setText("Total Payable Amount: " + total_payable_amount);
        textViewAmountToPay.setText("Amount to Pay: " + AmountToPay);
        textViewUserId.setText("User ID: " + userIdeditor);
        textViewOpername.setText("Operator Name: " + Opernameeditor);
        textViewLatitude.setText("Latitude: " + latitude);
        textViewLongitude.setText("Longitude: " + longitude);
        textViewToken.setText("Token: " + Token);
        textViewAdvanceAmt.setText("Advance Amount: " + AdvanceAmt);

        amountToPay = AmountToPay; // Set the amount to pay

       startRazorpayPayment();
    }

    private void startRazorpayPayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_itLjGcNUvNy5FE"); // Replace with your actual Razorpay Key ID

        try {
            // Ensure amountToPay is not null
            if (amountToPay == null || amountToPay.trim().isEmpty()) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountToPay);
            int amountInPaise = (int) (amount * 100);

            JSONObject options = new JSONObject();
            options.put("name", "Jhansi");
            options.put("description", "Payment for Services");
            options.put("currency", "INR");
            options.put("amount", amountInPaise); // Amount in paise
            options.put("prefill.email", "customer@example.com");
            options.put("prefill.contact", "8299722943");

            checkout.open(this, options);
        } catch (Exception e) {
            Toast.makeText(this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("payment", e.getMessage());
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        // Handle successful payment
        Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
        callUpdatePaymentApi(razorpayPaymentID);
    }

    @Override
    public void onPaymentError(int code, String response) {
        setResult(RESULT_CANCELED);
        finish();
        // Handle payment error
        Toast.makeText(this, "Payment Failed: " + response, Toast.LENGTH_SHORT).show();
    }

    private void callUpdatePaymentApi(String razorpayPaymentID) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RequestQueue queue = Volley.newRequestQueue(PaymentGateway.this);

                // Prepare JSON Object for API request
                JSONObject paymentData = new JSONObject();

                try {
                    paymentData.put("TrxnCode", "12");
                    paymentData.put("TrxnStatus", paymentTypeString);
                    paymentData.put("txn_error", "success");
                    paymentData.put("client_txn_ref_no", receivedText);
                    paymentData.put("bank_code", "999");  // Send bankcode as 999 for online payment
                    paymentData.put("TrxnId", razorpayPaymentID); // Use Razorpay Payment ID
                    paymentData.put("total_amount", total_payable_amount);
                    paymentData.put("payment_amount", AmountToPay);
                    paymentData.put("TrxnTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
                    paymentData.put("device_serial_no", "5a1bebcfc92cd931");
                    paymentData.put("collection_by_id", userIdeditor);
                    paymentData.put("collection_by_name", Opernameeditor);
                    paymentData.put("latitude", latitude);
                    paymentData.put("longitude", longitude);
                    paymentData.put("tid", "7617864718");
                    paymentData.put("payment_mode", "Online Payment");
                    paymentData.put("Token", Token);
                    paymentData.put("chequeNO", "NA");
                    paymentData.put("BankName", "NA");
                    paymentData.put("BankBranchName", "NA");
                    paymentData.put("ChequeDate", "NA");
                    paymentData.put("advAmt", AdvanceAmt);
                    paymentData.put("payAmtType", "0");
                    paymentData.put("remAdv", "0.00");

                    Log.d("parameterPayment", paymentData.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, URL4, null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    // Handle API response
                                    for (int i = 0; i < response.length(); i++) {
                                        JSONObject obj = response.getJSONObject(i);
                                        String ClientID = obj.getString("client_ref_no");
                                        String payment_amount_receptPage = obj.getString("payment_amount");
                                        String payment_time_receptPage = obj.getString("payment_time");
                                        String BalanceAmount_receptPage = obj.getString("BalanceAmount");

                                        Intent intent = new Intent(PaymentGateway.this, RecieptPage.class);
                                        intent.putExtra("client_ref_no", ClientID);
                                        intent.putExtra("payment_amount", payment_amount_receptPage);
                                        intent.putExtra("payment_time", payment_time_receptPage);
                                        intent.putExtra("BalanceAmount", BalanceAmount_receptPage);
                                        startActivity(intent);
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(PaymentGateway.this, "JSON Parsing Error", Toast.LENGTH_LONG).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley Error", error.toString());
                                Toast.makeText(PaymentGateway.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }) {

                    @Override
                    public byte[] getBody() {
                        return paymentData.toString().getBytes(StandardCharsets.UTF_8);
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
        },1);

    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

}