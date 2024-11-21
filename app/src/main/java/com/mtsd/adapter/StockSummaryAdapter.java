package com.mtsd.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mtsd.R;
import com.mtsd.model.ReportStockSummary;

import java.util.List;

public class StockSummaryAdapter extends RecyclerView.Adapter<StockSummaryAdapter.ViewHolder> {

    private final List<ReportStockSummary> stockSummaryList;

    public StockSummaryAdapter(List<ReportStockSummary> stockSummaryList) {
        this.stockSummaryList = stockSummaryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stock_summary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReportStockSummary product = stockSummaryList.get(position);

        holder.tvProductName.setText(product.getProductName());
        holder.tvQuantity.setText("Quantity: " + product.getQuantity());
        holder.tvPrice.setText("Price: $" + product.getPrice());
    }

    @Override
    public int getItemCount() {
        return stockSummaryList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvQuantity, tvPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
