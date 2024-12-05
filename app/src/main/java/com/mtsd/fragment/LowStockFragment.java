package com.mtsd.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtsd.R;
import com.mtsd.adapter.LowStockAdapter;
import com.mtsd.helper.DatabaseHelper;
import com.mtsd.helper.RepositoryManager;
import com.mtsd.model.ReportLowStock;
import com.mtsd.repository.ProductRepository;

import java.util.List;

public class LowStockFragment extends Fragment {

    private RecyclerView rvLowStock;
    private RepositoryManager repositoryManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_low_stock, container, false);

        rvLowStock = view.findViewById(R.id.rvLowStock);
        rvLowStock.setLayoutManager(new LinearLayoutManager(getContext()));

        SQLiteDatabase database = new DatabaseHelper(getContext()).getReadableDatabase();
        repositoryManager = new RepositoryManager(database);

        ProductRepository productRepository = repositoryManager.getProductRepository();

        List<ReportLowStock> lowStockList = productRepository.getLowStockProducts(15);

        LowStockAdapter adapter = new LowStockAdapter(lowStockList,getContext());
        rvLowStock.setAdapter(adapter);

        return view;
    }
}
