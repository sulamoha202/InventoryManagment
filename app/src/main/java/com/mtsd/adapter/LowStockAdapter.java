package com.mtsd.adapter;

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

    public LowStockAdapter(List<ReportLowStock> lowStockList) {
        this.lowStockList = lowStockList;
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
        holder.tvQuantity.setText("Low Stock: " + product.getQuantity() + " units remaining");

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
