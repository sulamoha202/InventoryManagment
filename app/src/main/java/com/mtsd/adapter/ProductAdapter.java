package com.mtsd.adapter;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mtsd.helper.DatabaseHelper;
import com.mtsd.R;
import com.mtsd.fragment.EditProductFragment;
import com.mtsd.helper.RepositoryManager;
import com.mtsd.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<Product> productList;
    private Context context;

    private RepositoryManager repositoryManager;
    public ProductAdapter(List<Product> productList,Context context) {
        this.productList = productList;
        this.context = context;
        SQLiteDatabase database = new DatabaseHelper(context).getWritableDatabaseInstance();
        repositoryManager = new RepositoryManager(database);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    public void updateProductList(List<Product> newProductList) {
        this.productList.clear();
        this.productList.addAll(newProductList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        String formatedPrice = String.format("%s %.2f", "€",product.getPrice());
        // Set product details
        holder.tvProductId.setText(String.valueOf(product.getId()));
        holder.tvProductName.setText(product.getName());
        holder.tvProductQuantity.setText(String.valueOf(product.getQuantity()));
        holder.tvProductPrice.setText(formatedPrice);
        holder.tvProductDescription.setText(product.getDescription());

        // Load image if available, otherwise set fallback image
        if (product.getImageReference() != null && !product.getImageReference().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getImageReference())
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.baseline_image_24); // Fallback image if no image
        }

        // Edit button logic
        holder.btnEditProduct.setOnClickListener(view -> {
            Toast.makeText(context, "Edit product: " + product.getName(), Toast.LENGTH_SHORT).show();

            // Create EditProductFragment and pass the product ID as an argument
            Fragment editProductFragment = new EditProductFragment();
            Bundle args = new Bundle();
            args.putInt("productId", product.getId());
            editProductFragment.setArguments(args);

            // Load EditProductFragment
            ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.contentFrame, editProductFragment)
                    .addToBackStack(null)
                    .commit();
        });

        holder.btnDeleteProduct.setOnClickListener(view -> {

            repositoryManager.getProductRepository().deleteProductById(product.getId());

            productList.remove(position);
            notifyItemRemoved(position);

            Toast.makeText(context, "Deleted product: " + product.getName(), Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView ;
        TextView tvProductId, tvProductName, tvProductQuantity, tvProductPrice, tvProductDescription;
        ImageButton btnEditProduct, btnDeleteProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductId = itemView.findViewById(R.id.tvProductId);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductDescription = itemView.findViewById(R.id.tvProductDescription);
            imageView = itemView.findViewById(R.id.imageProductView);
            btnDeleteProduct = itemView.findViewById(R.id.btnDeleteProduct);
            btnEditProduct = itemView.findViewById(R.id.btnEditProduct);
        }
    }
}
