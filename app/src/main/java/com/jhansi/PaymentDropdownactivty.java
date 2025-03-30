package com.jhansi;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PaymentDropdownactivty extends AppCompatActivity implements PaymentResultListener {

    TextView paymentpagecurrenttax,current_water_taxpaymentactivity,water_arrerpaymentactivity,current_server_taxpaymentactivity,
            sewer_arrearpaymentactivity,Arrer_surchargepaymentactivity,bill_datepaymentactivity,discount_amountpaymentactivity,
            current_surchargepaymentactivity,total_payable_amountpaymentactivity,AmountToPaypaymentactivity,OTSDiscountamtpaymentactivity,
            AdvanceAmtpaymentactivity,paymenttype,Amountpaidtext;

    ArrayList<String> bankNames = new ArrayList<>();
    HashMap<String, String> bankMap = new HashMap<>();
    String selectedBankCode = "";


    String Token,paymentTypeString,receivedText,total_payable_amount,AmountToPay,bill_date,AdvanceAmt,BankCode,Opernameeditor
            ,userIdeditor,amountToPay;

    FusedLocationProviderClient fusedLocationProviderClient;

    String latitude = String.valueOf(0.0), longitude = String.valueOf(0.0); // Default values

    String URL4 = "https://jklmc.gov.in/LKOPOSAPI/LKOPOSAPI/api/UpdatePaymentApi";
    String URL5 =  "https://jklmc.gov.in/LKOPOSAPI/LKOPOSAPI/api/BankMaster";


    Spinner spinnerPaymentMethod,spinnerBankName;
    EditText editTextChequeNumber, editTextBankName,editTextBankNameBranch, editTextChequeDate;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_dropdownactivty);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
// Initialize Razorpay

/////// insilization ID
        editTextBankName = findViewById(R.id.editTextSearch);
        paymentpagecurrenttax = findViewById(R.id.paymentpagecurrenttax);
        current_water_taxpaymentactivity = findViewById(R.id.current_water_taxpaymentactivity);
        water_arrerpaymentactivity = findViewById(R.id.water_arrerpaymentactivity);
        spinnerBankName = findViewById(R.id.spinnerBankName);
        current_server_taxpaymentactivity = findViewById(R.id.current_server_taxpaymentactivity);
        sewer_arrearpaymentactivity = findViewById(R.id.sewer_arrearpaymentactivity);
        Arrer_surchargepaymentactivity = findViewById(R.id.Arrer_surchargepaymentactivity);
        bill_datepaymentactivity = findViewById(R.id.bill_datepaymentactivity);
        discount_amountpaymentactivity = findViewById(R.id.discount_amountpaymentactivity);
        current_surchargepaymentactivity = findViewById(R.id.current_surchargepaymentactivity);
        total_payable_amountpaymentactivity = findViewById(R.id.total_payable_amountpaymentactivity);
        AmountToPaypaymentactivity = findViewById(R.id.AmountToPaypaymentactivity);
        OTSDiscountamtpaymentactivity = findViewById(R.id.OTSDiscountamtpaymentactivity);
        AdvanceAmtpaymentactivity = findViewById(R.id.AdvanceAmtpaymentactivity);
        paymenttype = findViewById(R.id.paymenttype);
        Amountpaidtext = findViewById(R.id.Amountpaidtext);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Token = sharedPreferences.getString("TKOEN", "N/A");

        SharedPreferences Opername = PreferenceManager.getDefaultSharedPreferences(this);
        Opernameeditor = Opername.getString("Opername", "N/A");


        SharedPreferences UesrID = PreferenceManager.getDefaultSharedPreferences(this);
        userIdeditor = UesrID.getString("userId", "N/A");


        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Moves to the previous screen
            }
        });



        // Initialize location provider
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocationservices();  // Fetch device location

        ////Spinner Edittext

        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod);
        editTextChequeNumber = findViewById(R.id.editTextChequeNumber);
        editTextChequeDate = findViewById(R.id.editTextChequeDate);
        editTextBankNameBranch = findViewById(R.id.editTextBankNameBranch);

        ////listiner

        spinnerPaymentMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPaymentMethod = parent.getItemAtPosition(position).toString();

                if (selectedPaymentMethod.equalsIgnoreCase("Cheque")) {
                    editTextChequeNumber.setVisibility(View.VISIBLE);
                    spinnerBankName.setVisibility(View.VISIBLE);
                    editTextChequeDate.setVisibility(View.VISIBLE);
                    editTextBankNameBranch.setVisibility(View.VISIBLE);
                    fetchBankList();
                } else if (selectedPaymentMethod.equalsIgnoreCase("Online Payment")) {

                } else {
                    editTextChequeNumber.setVisibility(View.GONE);
                    spinnerBankName.setVisibility(View.GONE);
                    editTextChequeDate.setVisibility(View.GONE);
                    editTextBankNameBranch.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });




        // Handle Spinner Click
        spinnerBankName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    showSearchableBankDialog();
                }
                return true; // Prevents default Spinner behavior
            }
        });


        /////  editTextChequeNumber limit to 6 digit only

        editTextChequeNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 6) {
                    editTextChequeNumber.setError("Enter a valid 6-digit cheque number");
                }
            }
        });


        // Date Picker for Cheque Date
        editTextChequeDate.setOnClickListener(v -> showDatePicker());




        // Retrieve data from Intent
        receivedText = getIntent().getStringExtra("clientId").toString();
        String currentwatertax = getIntent().getStringExtra("current_water_tax");
        String water_arrer = getIntent().getStringExtra("water_arrer");
        String current_server_tax = getIntent().getStringExtra("current_server_tax");
        String sewer_arrear = getIntent().getStringExtra("sewer_arrear");
        String Arrer_surcharge = getIntent().getStringExtra("Arrer_surcharge");
        bill_date = getIntent().getStringExtra("bill_date");
        String discount_amount = getIntent().getStringExtra("discount_amount");
        String current_surcharge = getIntent().getStringExtra("current_surcharge");
        total_payable_amount = getIntent().getStringExtra("total_payable_amount");
        AmountToPay = getIntent().getStringExtra("AmountToPay");
        String OTSDiscountamt = getIntent().getStringExtra("OTSDiscountamt");
        AdvanceAmt = getIntent().getStringExtra("AdvanceAmt");
        paymentTypeString = getIntent().getStringExtra("paymentType");
        // int userId = getIntent().getIntExtra("USER_ID", 0); // Default value is 0 if not found

        // Display the received data
        paymentpagecurrenttax.setText(receivedText.toString());
        current_water_taxpaymentactivity.setText(currentwatertax);
        water_arrerpaymentactivity.setText(water_arrer);
        current_server_taxpaymentactivity.setText(current_server_tax);
        sewer_arrearpaymentactivity.setText(sewer_arrear);
        Arrer_surchargepaymentactivity.setText(Arrer_surcharge);
        bill_datepaymentactivity.setText(bill_date);
        discount_amountpaymentactivity.setText(discount_amount);
        current_surchargepaymentactivity.setText(current_surcharge);
        total_payable_amountpaymentactivity.setText(total_payable_amount);
        AmountToPaypaymentactivity.setText(AmountToPay);
        OTSDiscountamtpaymentactivity.setText(OTSDiscountamt);
        Amountpaidtext.setText("  Amount to Pay ₹" + AmountToPay);
        AdvanceAmtpaymentactivity.setText(AdvanceAmt);
        if (paymentTypeString != null) {
            paymenttype.setText(paymentTypeString);
        }

    }

    private void showSearchableBankDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Bank");

        View view = getLayoutInflater().inflate(R.layout.custom_dialogbox, null);
        EditText editTextSearch = view.findViewById(R.id.editTextSearch);
        ListView listViewBanks = view.findViewById(R.id.listViewBanks);

        // Create List Adapter
        ArrayAdapter<String> dialogAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bankNames);
        listViewBanks.setAdapter(dialogAdapter);

        // Filter the list when typing
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dialogAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        // Handle List Item Click
        listViewBanks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedBankCode = dialogAdapter.getItem(position);
                spinnerBankName.setSelection(bankNames.indexOf(selectedBankCode)); // Update Spinner
                dialog.dismiss();
            }
        });
    }


    private void getCurrentLocationservices() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                return;
            }

        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
                Log.d("LOCATION", "Lat: " + latitude + ", Long: " + longitude);
            } else {
                Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            // Format the date as YYYY-MM-DD, adding leading zeros
            String formattedDate = String.format("%04d-%02d-%02d", year1, (month1 + 1), dayOfMonth);
            editTextChequeDate.setText(formattedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    // Handle permission request result
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocationservices();
            } else {
                Toast.makeText(this, "Permission denied! Cannot fetch location.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    ////// Button method
    public void payout(View view) {
        String selectedPaymentMethod = spinnerPaymentMethod.getSelectedItem().toString();
        String chequeNumber = editTextChequeNumber.getText().toString().trim();
        String bankName = ""; // Initialize bankName
        String bankBranch = ""; // Initialize bankBranch
        String chequeDate = ""; // Initialize chequeDate
        // Prepare data for receipt page
        // Get values from EditTexts
        chequeNumber = editTextChequeNumber.getText().toString().trim();
        bankName = spinnerBankName.getSelectedItem().toString().trim();
        bankBranch = editTextBankNameBranch.getText().toString().trim();
        chequeDate = editTextChequeDate.getText().toString().trim();

        // If payment method is Cash, send "NA" for cheque details
        if (selectedPaymentMethod.equalsIgnoreCase("cash")) {
            chequeNumber = "NA";
            bankName = "NA";
            bankBranch = "NA";
            chequeDate = "NA";
        } else if (selectedPaymentMethod.equalsIgnoreCase("Online Payment")) {
            Intent intent = new Intent(PaymentDropdownactivty.this, PaymentGateway.class);
            intent.putExtra("clientId", receivedText);
            intent.putExtra("paymentType", paymentTypeString);
            intent.putExtra("total_payable_amount", total_payable_amount);
            intent.putExtra("AmountToPay", AmountToPay);
            intent.putExtra("userId", userIdeditor);
            intent.putExtra("Opername", Opernameeditor);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            intent.putExtra("Token", Token);
            intent.putExtra("AdvanceAmt", AdvanceAmt);
            // Start PaymentGateway activity for result
            startActivityForResult(intent, 1); // Use requestCode 1
            return; // Exit the method to avoid further processing
           // startRazorpayPayment();
           // return; // Exit the method to avoid further processing

        }else {

            // For Cheque payment, get values from the spinner and EditText fields
            if (spinnerBankName.getSelectedItem() != null) {
                bankName = spinnerBankName.getSelectedItem().toString();
            } else {
                bankName = "NA"; // Default value if spinner is empty
            }
            bankBranch = editTextBankNameBranch.getText().toString().trim();
            chequeDate = editTextChequeDate.getText().toString().trim();

            // Validation for Cheque Details
            if (chequeNumber.length() < 6) {
                editTextChequeNumber.setError("Enter a valid 6-digit cheque number");
                return;
            }
            if (bankName.isEmpty()) {
                editTextBankName.setError("Enter bank name");
                return;
            }
            if (bankBranch.isEmpty()) {
                editTextBankNameBranch.setError("Enter bank branch");
                return;
            }
            if (chequeDate.isEmpty()) {
                editTextChequeDate.setError("Select a cheque date");
                return;
            }
        }

        // Get current device date and time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date()); // Format current time
        RequestQueue queue = Volley.newRequestQueue(this);


        // Prepare JSON Object for API request
        JSONObject paymentData = new JSONObject();


        try {


            paymentData.put("TrxnCode", "12");
            paymentData.put("TrxnStatus", paymentTypeString);
            paymentData.put("txn_error", "success");
            paymentData.put("client_txn_ref_no", receivedText); // Removes only backslashes
            if (selectedPaymentMethod.equalsIgnoreCase("cash")||selectedPaymentMethod.equalsIgnoreCase("Online Payment")) {
                paymentData.put("bank_code", "999");  // Send bankcode as 999
            } else {
                paymentData.put("bank_code", BankCode);  // Optional: Send empty or actual bank code
            }
            paymentData.put("TrxnId", receivedText); // Removes only backslashes
            paymentData.put("total_amount", total_payable_amount);
            paymentData.put("payment_amount", AmountToPay);
            paymentData.put("TrxnTime", currentDateTime);
            paymentData.put("device_serial_no", "5a1bebcfc92cd931");
            paymentData.put("collection_by_id", userIdeditor);
            paymentData.put("collection_by_name", Opernameeditor);
            paymentData.put("latitude", latitude);
            paymentData.put("longitude", longitude);
            paymentData.put("tid", "7617864718");
            paymentData.put("payment_mode", selectedPaymentMethod);
            if (selectedPaymentMethod.equalsIgnoreCase("Online Payment")) {
                paymentData.put("payment_mode", "Online Payment");  // Send bankcode as 999
            } else {
                paymentData.put("payment_mode", selectedPaymentMethod);  // Optional: Send empty or actual bank code
            }

            paymentData.put("Token", Token);
            paymentData.put("chequeNO", chequeNumber);
            paymentData.put("BankName", bankName);
            paymentData.put("BankBranchName", bankBranch);
            paymentData.put("ChequeDate", chequeDate);
            paymentData.put("advAmt", AdvanceAmt);
            paymentData.put("payAmtType", "0");
            paymentData.put("remAdv", "0.00");
            Log.d("parameterPayment",paymentData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, URL4, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Loop through the JSON Array
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                String ClientID = obj.getString("client_ref_no");
                                String payment_amount_receptPage = obj.getString("payment_amount");
                                String payment_time_receptPage = obj.getString("payment_time");
                                String BalanceAmount_receptPage = obj.getString("BalanceAmount");

                                Intent intent = new Intent(PaymentDropdownactivty.this, RecieptPage.class);
                                intent.putExtra("client_ref_no", ClientID);
                                intent.putExtra("payment_amount", payment_amount_receptPage);// Pass payment type to next screen
                                intent.putExtra("payment_time", payment_time_receptPage);
                                intent.putExtra("BalanceAmount", BalanceAmount_receptPage);
                                startActivity(intent);
                                // Don't forget to apply changes!
                                SharedPreferences sharedPreferencesclient = PreferenceManager.getDefaultSharedPreferences(PaymentDropdownactivty.this);
                                SharedPreferences.Editor editorcleient = sharedPreferencesclient.edit();
                                editorcleient.putString("client_ref_no", ClientID);
                                editorcleient.apply();
                                Log.d("ref",ClientID);
                                //String Zone = obj.getString("zone");// Modify based on your API response
                                // Move to NextActivity with Token
//                                Intent intent = new Intent(PaymentDropdownactivty.this, HomePage.class);
//                                intent.putExtra("TOKEN", token);
//                                intent.putExtra("ZONE", Zone); // Assuming username is mobile number
//                                startActivity(intent);

//                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
//                                SharedPreferences.Editor editor = sharedPreferences.edit();
//
//// Save the token valu
//                                editor.putString("TKOEN", token);
//                                editor.apply(); // Don't forget to apply changes!


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("error payment",e.toString());
                            Toast.makeText(PaymentDropdownactivty.this, response.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        Toast.makeText(PaymentDropdownactivty.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public byte[] getBody() {
                String jsonString = paymentData.toString().replace("\\/", "/");
                Log.d("Debug", "Final JSON: " + jsonString);
                return jsonString.getBytes(StandardCharsets.UTF_8);
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

    private void fetchBankList() {
        RequestQueue queue = Volley.newRequestQueue(this);
        // Create JSON Object for Request Body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("Type", "Bank");
            requestBody.put("Token", Token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, URL5, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Loop through the JSON Array
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                BankCode = obj.getString("BankCode");
                                String BankName = obj.getString("BankName");// Modify based on your API response

                                bankNames.add(BankName);
                                bankMap.put(BankName, BankCode);
                            }

                            // Update the spinner with bank names
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(PaymentDropdownactivty.this, android.R.layout.simple_spinner_dropdown_item, bankNames);
                            spinnerBankName.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PaymentDropdownactivty.this, "JSON Parsing Error", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        Toast.makeText(PaymentDropdownactivty.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String paymentMethod = data.getStringExtra("payment_method");
                if (paymentMethod != null && paymentMethod.equals("Online Payment")) {
                    // Update the payment method to "Online Payment"
                    spinnerPaymentMethod.setSelection(getIndex(spinnerPaymentMethod, "Online Payment"));
                    // You can also update other UI elements or proceed with further actions
                }
            } else if (resultCode == RESULT_CANCELED) {
              //  Toast.makeText(this, "Payment was cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Helper method to get the index of an item in the spinner
    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }
        return 0;
    }

    private void startRazorpayPayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_itLjGcNUvNy5FE"); // Replace with your actual Razorpay Key ID

        try {
            // Ensure amountToPay is not null
            if (amountToPay == null || amountToPay.trim().isEmpty()) {
                Toast.makeText(this,amountToPay, Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountToPay);
            int amountInPaise = (int) (amount * 100);

            JSONObject options = new JSONObject();
            options.put("name", "Jhansi");
            options.put("description", "Payment for Services");
            options.put("currency", "INR");
            options.put("amount", amount); // Amount in paise
            options.put("prefill.email", "customer@example.com");
            options.put("prefill.contact", "8299722943");

            checkout.open(this, options);
        } catch (Exception e) {
            Toast.makeText(this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("payment", e.getMessage());
        }

    }





    @Override
    public void onPaymentSuccess(String s) {

    }

    @Override
    public void onPaymentError(int i, String s) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();  // Default behavior (closes activity)
    }

}
