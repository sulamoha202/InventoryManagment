package com.mtsd.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtsd.OnProductUpdatedListener;
import com.mtsd.helper.DatabaseHelper;
import com.mtsd.R;
import com.mtsd.adapter.ProductAdapter;
import com.mtsd.helper.RepositoryManager;
import com.mtsd.model.Product;
import com.mtsd.util.ItemSpacingDecoration; // Import the ItemSpacingDecoration

import java.util.List;
public class ProductListFragment extends Fragment implements OnProductUpdatedListener {

    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;
    private RepositoryManager repositoryManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product_list, container, false);

        SQLiteDatabase database = new DatabaseHelper(getContext()).getReadableDatabase();
        repositoryManager = new RepositoryManager(database);

        recyclerViewProducts = rootView.findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Product> productList = repositoryManager.getProductRepository().getAllProducts();//dbHelper.getAllProducts();
        productAdapter = new ProductAdapter(productList, getContext());
        recyclerViewProducts.setAdapter(productAdapter);

        int space = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        recyclerViewProducts.addItemDecoration(new ItemSpacingDecoration(space));

        Button btnAddProduct = rootView.findViewById(R.id.btnAddProduct);
        btnAddProduct.setOnClickListener(v -> redirectToAddProductFragment());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateProductList();
    }

    @Override
    public void onProductUpdated() {
        updateProductList();
    }

    private void updateProductList() {
        List<Product> productList = repositoryManager.getProductRepository().getAllProducts();
        productAdapter.updateProductList(productList);
    }

    // Redirect to AddProductFragment
    private void redirectToAddProductFragment() {
        AddProductFragment addProductFragment = new AddProductFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.contentFrame, addProductFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
