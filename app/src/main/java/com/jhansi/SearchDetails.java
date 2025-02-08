package com.jhansi;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jhansi.Adapter.SearchAdapter;
import com.jhansi.Model.SearchModel;

import java.util.ArrayList;

public class SearchDetails extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchAdapter dataAdapter;
    private ArrayList<SearchModel> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_details);

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
    }