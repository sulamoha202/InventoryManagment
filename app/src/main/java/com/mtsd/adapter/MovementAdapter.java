package com.mtsd.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mtsd.R;
import com.mtsd.model.Movement;

import java.util.List;

public class MovementAdapter extends RecyclerView.Adapter<MovementAdapter.MovementViewHolder> {
    private List<Movement> movementList;

    public MovementAdapter(List<Movement> movementList) {
        this.movementList = movementList;
    }

    @NonNull
    @Override
    public MovementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movement, parent, false);
        return new MovementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovementViewHolder holder, int position) {
        Movement movement = movementList.get(position);
        holder.tvProductName.setText(movement.getProductName());
        holder.tvMovementType.setText(movement.getMovementType());
        holder.tvQuantity.setText(String.valueOf(movement.getQuantity()));
        holder.tvDate.setText(movement.getDate());
    }

    @Override
    public int getItemCount() {
        return movementList.size();
    }

    public static class MovementViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvMovementType, tvQuantity, tvDate;

        public MovementViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvMovementType = itemView.findViewById(R.id.tvMovementType);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
