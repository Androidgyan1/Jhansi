package com.jhansi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jhansi.Model.SearchModel;
import com.jhansi.R;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private Context context;
    private List<SearchModel> dataList;

    public SearchAdapter(Context context, List<SearchModel> dataList) {
        this.context = context;
        this.dataList = dataList;
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
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, emailTextView,texthousename,textViewownermobile,textViewownerbilldate,isEligibleForDiscount,
                discount_amount,CurrentSurcharge,AmountRecivedCurrentFY,TotalTax;

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
        }
    }
}

