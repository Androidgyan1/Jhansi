package com.jhansi.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.jhansi.Model.SearchModel;
import com.jhansi.PaymentDropdownactivty;
import com.jhansi.R;
import com.jhansi.RecieptPage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<SearchModel> dataList;
    private Context context;
    private String Token;

    public SearchAdapter(Context context, List<SearchModel> dataList) {
        this.context = context;
        this.dataList = dataList;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Token = sharedPreferences.getString("TKOEN", "N/A");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Clear previous selections first
        holder.clearSelections();

        SearchModel data = dataList.get(position);

        // Set all text views
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

        // Calculate and display due balance
        try {
            double totalTax = Double.parseDouble(data.TaTotalTax());
            double amountReceived = Double.parseDouble(data.AmountRecivedCurrentFY());
            double dueBalance = totalTax - amountReceived;

            String formattedDueBalance = String.format(Locale.US, "₹%.2f", dueBalance);
            holder.textViewDueBalance.setText(formattedDueBalance);

            // Set color based on balance
            if (dueBalance > 0) {
                holder.textViewDueBalance.setTextColor(Color.RED);
            } else if (dueBalance < 0) {
                holder.textViewDueBalance.setTextColor(Color.BLUE);
            } else {
                holder.textViewDueBalance.setTextColor(Color.GREEN);
            }
        } catch (NumberFormatException e) {
            holder.textViewDueBalance.setText("Due Balance: N/A");
            Log.e("NumberFormat", "Error parsing amounts", e);
        }

        // Set up checkbox listeners
        holder.checkBoxFullPayment.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                holder.checkBoxPartialPayment.setChecked(false);
                try {
                    double totalTax = Double.parseDouble(data.TaTotalTax());
                    double amountReceived = Double.parseDouble(data.AmountRecivedCurrentFY());
                    double dueBalance = totalTax - amountReceived;
                    holder.editTextPaymentAmount.setText(String.format(Locale.US, "%.2f", dueBalance));
                } catch (NumberFormatException e) {
                    holder.editTextPaymentAmount.setText(data.TaTotalTax());
                }
                holder.editTextPaymentAmount.setEnabled(false);
            } else {
                holder.editTextPaymentAmount.setText("");
            }
        });

        holder.checkBoxPartialPayment.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                holder.checkBoxFullPayment.setChecked(false);
                holder.editTextPaymentAmount.setEnabled(true);
                holder.editTextPaymentAmount.setText("");
                holder.editTextPaymentAmount.requestFocus();
            } else {
                holder.editTextPaymentAmount.setEnabled(false);
            }
        });

        // Proceed Button Click
        holder.Proceed.setOnClickListener(v -> {
            // Validate payment selection
            if (!holder.checkBoxFullPayment.isChecked() && !holder.checkBoxPartialPayment.isChecked()) {
                Toast.makeText(context, "Please select payment type", Toast.LENGTH_SHORT).show();
                return;
            }

            String paymentAmount;
            if (holder.checkBoxFullPayment.isChecked()) {
                try {
                    double totalTax = Double.parseDouble(data.TaTotalTax());
                    double amountReceived = Double.parseDouble(data.AmountRecivedCurrentFY());
                    double dueBalance = totalTax - amountReceived;
                    paymentAmount = String.format(Locale.US, "%.2f", dueBalance);
                } catch (NumberFormatException e) {
                    paymentAmount = data.TaTotalTax();
                }
            } else {
                paymentAmount = holder.editTextPaymentAmount.getText().toString();
            }

            if (paymentAmount.isEmpty()) {
                Toast.makeText(context, "Please enter payment amount", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate partial payment amount
            if (holder.checkBoxPartialPayment.isChecked()) {
                try {
                    double partialAmount = Double.parseDouble(paymentAmount);
                    double totalTax = Double.parseDouble(data.TaTotalTax());
                    double amountReceived = Double.parseDouble(data.AmountRecivedCurrentFY());
                    double dueBalance = totalTax - amountReceived;

                    if (partialAmount <= 0) {
                        Toast.makeText(context, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (partialAmount > dueBalance) {
                        Toast.makeText(context, "Payment amount cannot exceed due balance", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(context, "Invalid amount format", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // Call API
            callPaymentAPI(data.getJLalKal(), paymentAmount,
                    holder.checkBoxFullPayment.isChecked() ? "full" : "partial");

            // Clear selections after navigation
            new Handler().postDelayed(() -> {
                holder.clearSelections();
            }, 300); // Small delay to ensure navigation happens first
        });
    }

    private void callPaymentAPI(String jalkalId, String paymentAmount, String paymentType) {
        String URL3 = "https://jklmc.gov.in/LKOPOSAPI/LKOPOSAPI/api/FetchBillDetailAPINew";
        RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("jalkal_id", jalkalId);
            requestBody.put("PayAmt", paymentAmount);
            requestBody.put("payment_type", paymentType);
            requestBody.put("Token", Token);
            Log.d("API Parameters", requestBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, URL3, null,
                response -> {
                    try {
                        if (response.length() > 0) {
                            JSONObject obj = response.getJSONObject(0);
                            String status = obj.getString("status");

                            if (status.equals("904")) {
                                String reason = obj.getString("reason");
                                Toast.makeText(context, "Error: " + reason, Toast.LENGTH_LONG).show();
                            } else {
                                // Extract all response data
                                String clientId = obj.getString("client_ref_no");
                                String current_water_tax = obj.optString("current_water_tax", "0");
                                String water_arrer = obj.optString("water_arrer", "0");
                                String current_server_tax = obj.optString("current_server_tax", "0");
                                String sewer_arrear = obj.optString("sewer_arrear", "0");
                                String Arrer_surcharge = obj.optString("Arrer_surcharge", "0");
                                String bill_date = obj.optString("bill_date", "");
                                String discount_amount = obj.optString("discount_amount", "0");
                                String current_surcharge = obj.optString("current_surcharge", "0");
                                String total_payable_amount = obj.optString("total_payable_amount", "0");
                                String AmountToPay = obj.optString("AmountToPay", "0");
                                String OTSDiscountamt = obj.optString("OTSDiscountamt", "0");
                                String AdvanceAmt = obj.optString("AdvanceAmt", "0");

                                // Format bill date if available
                                if (!bill_date.isEmpty()) {
                                    try {
                                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                                        Date date = inputFormat.parse(bill_date);
                                        if (date != null) {
                                            bill_date = outputFormat.format(date);
                                        }
                                    } catch (ParseException e) {
                                        Log.e("DateParse", "Error parsing bill date", e);
                                    }
                                }

                                // Start PaymentDropdownactivty with all data
                                Intent intent = new Intent(context, PaymentDropdownactivty.class);
                                intent.putExtra("clientId", clientId);
                                intent.putExtra("paymentType", paymentType);
                                intent.putExtra("current_water_tax", current_water_tax);
                                intent.putExtra("water_arrer", water_arrer);
                                intent.putExtra("current_server_tax", current_server_tax);
                                intent.putExtra("sewer_arrear", sewer_arrear);
                                intent.putExtra("Arrer_surcharge", Arrer_surcharge);
                                intent.putExtra("bill_date", bill_date);
                                intent.putExtra("discount_amount", discount_amount);
                                intent.putExtra("current_surcharge", current_surcharge);
                                intent.putExtra("total_payable_amount", total_payable_amount);
                                intent.putExtra("AmountToPay", AmountToPay);
                                intent.putExtra("OTSDiscountamt", OTSDiscountamt);
                                intent.putExtra("AdvanceAmt", AdvanceAmt);
                                context.startActivity(intent);
                            }
                        } else {
                            Toast.makeText(context, "Empty Response", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("Volley Error", error.toString());
                    Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
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

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // Add this method to clear all fields


        TextView nameTextView, emailTextView, texthousename, textViewownermobile,
                textViewownerbilldate, isEligibleForDiscount, discount_amount,
                CurrentSurcharge, AmountRecivedCurrentFY, TotalTax, textViewDueBalance;
        AppCompatButton Proceed;
        CheckBox checkBoxFullPayment, checkBoxPartialPayment;
        EditText editTextPaymentAmount;

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
            textViewDueBalance = itemView.findViewById(R.id.dueblance);
            Proceed = itemView.findViewById(R.id.buttonProceed);
            checkBoxFullPayment = itemView.findViewById(R.id.checkBoxFullPayment);
            checkBoxPartialPayment = itemView.findViewById(R.id.checkBoxPartialPayment);
            editTextPaymentAmount = itemView.findViewById(R.id.editTextPaymentAmount);
        }

        public void clearSelections() {
            checkBoxFullPayment.setChecked(false);
            checkBoxPartialPayment.setChecked(false);
            editTextPaymentAmount.setText("");
            editTextPaymentAmount.setEnabled(false);
        }


    }

}