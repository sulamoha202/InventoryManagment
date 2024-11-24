package com.mtsd.fragment;

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
import com.mtsd.util.DatabaseHelper;
import com.mtsd.model.ReportLowStock;

import java.util.List;

public class LowStockFragment extends Fragment {

    private RecyclerView rvLowStock;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_low_stock, container, false);

        rvLowStock = view.findViewById(R.id.rvLowStock);
        rvLowStock.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseHelper = new DatabaseHelper(getContext());
        List<ReportLowStock> lowStockList = databaseHelper.getLowStockProducts(15);

        LowStockAdapter adapter = new LowStockAdapter(lowStockList,getContext());
        rvLowStock.setAdapter(adapter);

        return view;
    }
}
