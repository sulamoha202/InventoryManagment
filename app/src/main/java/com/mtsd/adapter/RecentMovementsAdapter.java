package com.mtsd.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mtsd.R;
import com.mtsd.model.Movement;
import com.mtsd.model.Product;
import com.mtsd.model.ReportRecentMovement;
import com.mtsd.repository.ProductRepository;

import java.util.List;

public class RecentMovementsAdapter extends RecyclerView.Adapter<RecentMovementsAdapter.ViewHolder> {

    private final List<Movement> movementList;
    private ProductRepository productRepository;

    public RecentMovementsAdapter(List<Movement> movementList, ProductRepository productRepository) {
        this.movementList = movementList;
        this.productRepository = productRepository;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_movement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movement movement = movementList.get(position);
        Product product  = productRepository.getProductById(movement.getProductId());
        holder.tvProductName.setText(product.getName());
        holder.tvMovementType.setText("Movement Type: " + movement.getMovementType());
        holder.tvQuantity.setText("Quantity: " + movement.getQuantity());
        holder.tvDate.setText("Date: " + movement.getDate());
    }

    @Override
    public int getItemCount() {
        return movementList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvMovementType, tvQuantity, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvMovementType = itemView.findViewById(R.id.tvMovementType);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
