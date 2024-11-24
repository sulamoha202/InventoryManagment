package com.mtsd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mtsd.R;
import com.mtsd.model.ReportLowStock;

import java.util.List;

public class LowStockAdapter extends RecyclerView.Adapter<LowStockAdapter.ViewHolder> {

    private List<ReportLowStock> lowStockList;
    private Context context;

    public LowStockAdapter(List<ReportLowStock> lowStockList,Context context) {
        this.lowStockList = lowStockList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_low_stock, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReportLowStock product = lowStockList.get(position);
        holder.tvProductName.setText(product.getProductName());
        String quantityRemaining = context.getString(R.string.low_stock_remaining,product.getQuantity());
        holder.tvQuantity.setText(quantityRemaining);

        // Handle dismiss button
        holder.btnDismissAlert.setOnClickListener(v -> {
            lowStockList.remove(position); // Remove the alert
            notifyItemRemoved(position);  // Notify RecyclerView of the change
        });
    }

    @Override
    public int getItemCount() {
        return lowStockList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvQuantity;
        Button btnDismissAlert;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnDismissAlert = itemView.findViewById(R.id.btnDismissAlert);
        }
    }
}
