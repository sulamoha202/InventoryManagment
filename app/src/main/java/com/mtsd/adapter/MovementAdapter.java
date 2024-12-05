package com.mtsd.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mtsd.R;
import com.mtsd.model.Movement;

import java.util.List;
import java.util.Locale;

public class MovementAdapter extends RecyclerView.Adapter<MovementAdapter.MovementViewHolder> {
    private List<Movement> movementList;
    private  Context context;

    public MovementAdapter(List<Movement> movementList, Context context) {
        this.movementList = movementList;
        this.context = context;
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
        holder.tvProductName.setText(String.valueOf(movement.getProductId()));
        holder.tvMovementType.setText(getMovementType(context,movement.getMovementType()));
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

    private String getMovementType(Context context,String movementType){
        String resourceMovementTypeIn = context.getString(R.string.movement_type_in);
        String resourceMovementTypeOut = context.getString(R.string.movement_type_out);
        if(movementType.equals(resourceMovementTypeIn)){
            return resourceMovementTypeIn;
        }
        if(movementType.equals(resourceMovementTypeOut)){
            return resourceMovementTypeOut;
        }
        return "NONE";
    }

}
