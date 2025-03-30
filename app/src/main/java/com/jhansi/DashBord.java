package com.jhansi;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.jhansi.Adapter.ImageSliderAdapter;
import com.jhansi.FontsUitils.BaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DashBord extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    private int currentPage = 0;
    TextView totalcollection,cashcollection,chequecollection,payonline;

    String Token, userIdeditor, currentDateString, previousDateString;

    String URL6 = "https://jklmc.gov.in/LKOPOSAPI/LKOPOSAPI/api/CollectionByUserOnDatewise";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dash_bord);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        totalcollection = findViewById(R.id.totalcollection);
        cashcollection = findViewById(R.id.cashcollection);
        chequecollection = findViewById(R.id.chequecollection);
        payonline = findViewById(R.id.payonline);
        viewPager = findViewById(R.id.viewPager);

        // List of drawable images
        List<Integer> imageList = Arrays.asList(
                R.drawable.jalphoto,
                R.drawable.jalphotortwo
        );
        ImageSliderAdapter adapter = new ImageSliderAdapter(imageList);
        viewPager.setAdapter(adapter);

        // Start auto-slide
        startAutoSlide(imageList.size());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Token = sharedPreferences.getString("TKOEN", "N/A");

        SharedPreferences UesrID = PreferenceManager.getDefaultSharedPreferences(this);
        userIdeditor = UesrID.getString("userId", "N/A");


        // Get the current date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Format: YYYY-MM-DD
        Date currentDate = new Date();
        currentDateString = sdf.format(currentDate);

        // Get the previous date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR, -1); // Subtract 1 day
        previousDateString = sdf.format(calendar.getTime());

        // Print dates
        System.out.println("Current Date: " + currentDateString);
        System.out.println("Previous Date: " + previousDateString);

        RequestQueue queue = Volley.newRequestQueue(this);

        // Create JSON Object for Request Body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("StartDate", previousDateString);
            requestBody.put("EndDate", currentDateString);
            requestBody.put("UserId", userIdeditor);
            requestBody.put("Token", Token);
            Log.d("parameter Dashbord", requestBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, URL6, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            // Loop through the JSON Array
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject obj = response.getJSONObject(i);
                                // Handle null values by providing default "0"
                                String CASHCOLLECTION = obj.isNull("TotalCashCollection") ? "0" : obj.getString("TotalCashCollection");
                                String TOTALCOLLECTION = obj.isNull("TotalCollection") ? "0" : obj.getString("TotalCollection");
                                String CHEQUECOLLECTION = obj.isNull("TotalChequeCollection") ? "0" : obj.getString("TotalChequeCollection");
                                String PAYONLINE = obj.isNull("TotalOnlineCollection") ? "0" : obj.getString("TotalOnlineCollection");

                                // Format the values with ₹ symbol
                                totalcollection.setText("₹" + TOTALCOLLECTION);
                                cashcollection.setText("₹" + CASHCOLLECTION);
                                chequecollection.setText("₹" + CHEQUECOLLECTION);
                                payonline.setText("₹" + PAYONLINE);
//                                String Zone = obj.getString("zone");
//                                String opername = obj.getString("Opername");
//                                String userId = obj.getString("Userid");// Modify based on your API response
                                // Move to NextActivity with Token
//                                Intent intent = new Intent(DashBord.this, HomePage.class);
//                                intent.putExtra("TOKEN", token);
//                                intent.putExtra("ZONE", Zone); // Assuming username is mobile number
//                                startActivity(intent);
//                                finish();


// Save the token val


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                           // Toast.makeText(DashBord.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                        Toast.makeText(DashBord.this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
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

    private void startAutoSlide(int itemCount) {
        Runnable sliderRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentPage >= itemCount) {
                    currentPage = 0; // Reset to first image
                }
                viewPager.setCurrentItem(currentPage++, true);
                sliderHandler.postDelayed(this, 3000); // Change every 3 seconds
            }
        };
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sliderHandler.removeCallbacksAndMessages(null); // Stop auto-slide when activity is destroyed
    }

    public void user(View view) {
        Intent intent = new Intent(DashBord.this, HomePage.class);
        startActivity(intent);
    }
}