package com.jhansi.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.jhansi.DetailsShowActivity;
import com.jhansi.Model.SearchModel;
import com.jhansi.R;
import com.jhansi.SearchDetails;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {


    /// token
    // Retrieve value from SharedPreferences
    // Retrieve value from SharedPreferenc

    private List<SearchModel> dataList;

    private Context context;

    private String Token;




    public SearchAdapter(Context context, List<SearchModel> dataList) {
        this.context = context;
        this.dataList = dataList;
        // Retrieve value from SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        Token = sharedPreferences.getString("TKOEN", "Default"); // Default value
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchModel data = dataList.get(position);
        holder.nameTextView.setText(data.getJLalKal());
        holder.emailTextView.setText(data.getEmail());
        holder.texthousename.setText(data.gethousename());
        holder.textViewownermobile.setText(data.getmobileno());
        holder.textViewownerbilldate.setText(data.getbilldate());
        holder.isEligibleForDiscount.setText(data.getisEligibleForDiscount());
        holder.discount_amount.setText(data.discount_amount());
        holder.CurrentSurcharge.setText(data.current_surcharge());
        holder.AmountRecivedCurrentFY.setText(data.AmountRecivedCurrentFY());
        holder.TotalTax.setText(data.TaTotalTax());

        // Handle Full Payment Checkbox Click

        // Proceed Button Click
        holder.Proceed.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailsShowActivity.class);
            intent.putExtra("JLalKal", data.getJLalKal());
            intent.putExtra("Email", data.getEmail());
            intent.putExtra("HouseName", data.gethousename());
            intent.putExtra("OwnerMobile", data.getmobileno());
            intent.putExtra("BillDate", data.getbilldate());
            intent.putExtra("EligibilityForDiscount", data.getisEligibleForDiscount());
            intent.putExtra("DiscountAmount", data.discount_amount());
            intent.putExtra("CurrentSurcharge", data.current_surcharge());
            intent.putExtra("AmountReceivedCurrentFY", data.AmountRecivedCurrentFY());
            intent.putExtra("TotalTax", data.TaTotalTax());
            // Amount entered in case of partial payment

            // Start the next activity with the intent
            context.startActivity(intent);


            // Ensure at least one checkbox is selected




        });


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, emailTextView,texthousename,textViewownermobile,textViewownerbilldate,isEligibleForDiscount,
                discount_amount,CurrentSurcharge,AmountRecivedCurrentFY,TotalTax;

        AppCompatButton Proceed;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewJal);
            emailTextView = itemView.findViewById(R.id.textViewowner);
            texthousename = itemView.findViewById(R.id.textViewownerhousename);
            textViewownermobile = itemView.findViewById(R.id.textViewownermobile);
            textViewownerbilldate = itemView.findViewById(R.id.textViewownerbilldate);
            isEligibleForDiscount = itemView.findViewById(R.id.isEligibleForDiscount);
            discount_amount = itemView.findViewById(R.id.discount_amount);
            CurrentSurcharge = itemView.findViewById(R.id.CurrentSurcharge);
            AmountRecivedCurrentFY = itemView.findViewById(R.id.AmountRecivedCurrentFY);
            TotalTax = itemView.findViewById(R.id.TotalTax);
            Proceed = itemView.findViewById(R.id.buttonProceed);

        }
    }

    // Send payment request to API
    private void sendPaymentRequest(JSONObject jsonBody, Context context) {

         String URL3 = "https://jklmc.gov.in/LKOPOSAPI/LKOPOSAPI/api/FetchBillDetailAPINew";
        RequestQueue queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, URL3, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            String currentWaterTax = obj.getString("current_server_tax");

                            // Navigate to DetailsShowActivity
                            Intent intent = new Intent(context, DetailsShowActivity.class);
                            intent.putExtra("current_water_tax", currentWaterTax);
                            context.startActivity(intent);
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                },
                error -> {
                    Log.e("Volley Error", error.toString());
                    Toast.makeText(context, "API Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {

            @Override
            public byte[] getBody() {
                return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
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

