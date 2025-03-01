package com.jhansi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.jhansi.Adapter.SearchAdapter;
import com.jhansi.Model.SearchModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchDetails extends AppCompatActivity {

    JSONObject jsonObject;

    String URL3 = "https://jklmc.gov.in/LKOPOSAPI/LKOPOSAPI/api/FetchBillDetailAPINew";

    private RecyclerView recyclerView;
    private SearchAdapter dataAdapter;
    private ArrayList<SearchModel> dataList;

    private String jalId, Token; // Store Jal ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_details);

//        // Get Jal ID from Intent
//        jalId = getIntent().getStringExtra("JAL_ID");


//// token
//        SharedPreferences sharedPreferences2 = getSharedPreferences("MyPrefs", MODE_PRIVATE);
//        Token = sharedPreferences2.getString("TKOEN", "Not Found");


//        // Retrieve Jal ID from SharedPreferences
//        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
//         jalId = sharedPreferences.getString("JAL_ID", "");


///// on back prees button
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Moves to the previous screen
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Retrieve Data from Intent
        dataList = (ArrayList<SearchModel>) getIntent().getSerializableExtra("DATA_LIST");

        if (dataList == null) {
            Toast.makeText(this, "No Data Received", Toast.LENGTH_LONG).show();
            return;
        }
        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataAdapter = new SearchAdapter(this, dataList);
        recyclerView.setAdapter(dataAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();  // Default behavior (closes activity)
    }

    /////  full payment
    //  partial payment
    //  button to proceed for payment
}
