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
import com.mtsd.adapter.StockSummaryAdapter;
import com.mtsd.model.ReportStockSummary;
import com.mtsd.util.DatabaseHelper;

import java.util.List;

public class StockSummaryFragment extends Fragment {

    private RecyclerView rvStockSummary;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_summary, container, false);

        rvStockSummary = view.findViewById(R.id.rvStockSummary);
        rvStockSummary.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseHelper = new DatabaseHelper(getContext());
        List<ReportStockSummary> stockSummaryList = databaseHelper.getStockSummary();

        StockSummaryAdapter adapter = new StockSummaryAdapter(stockSummaryList);
        rvStockSummary.setAdapter(adapter);

        return view;
    }
}
