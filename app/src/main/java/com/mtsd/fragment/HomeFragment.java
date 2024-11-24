package com.mtsd.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.mtsd.R;
import com.mtsd.adapter.MovementAdapter;
import com.mtsd.model.Movement;
import com.mtsd.model.RevenueData;
import com.mtsd.util.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private PieChart pieChart;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        // Initialize PieChart
        pieChart = view.findViewById(R.id.pieChart);

        // Fetch last week's revenue data
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        List<RevenueData> revenueDataList = databaseHelper.getLastWeekRevenue(); // Fetch data from DB

        // Populate the PieChart with revenue data
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for (RevenueData data : revenueDataList) {
            pieEntries.add(new PieEntry(data.getRevenue(), data.getLabel())); // Add revenue and label (e.g., day)
        }

        // Create PieDataSet
        PieDataSet dataSet = new PieDataSet(pieEntries, getString(R.string.last_week_revenue)); // Title for the chart
        dataSet.setColors(new int[]{
                ContextCompat.getColor(getContext(), R.color.primary_button_color), // Orange
                ContextCompat.getColor(getContext(), R.color.accent_text_color),    // Accent Red
                ContextCompat.getColor(getContext(), R.color.secondary_text_color) // Secondary Gray
        });

        // Create PieData
        PieData pieData = new PieData(dataSet);
        Legend legend = pieChart.getLegend();
        legend.setTextColor(getResources().getColor(R.color.primary_text_color));

        // Customize PieChart
        pieChart.setData(pieData);
        pieChart.setCenterText(getString(R.string.revenue_distribution)); // Center text
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false); // Disable description
        pieChart.animateY(1000); // Add animation
        pieChart.invalidate();

        TextView tvWelcom = view.findViewById(R.id.tvWelcome);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name","Guest");
        String welcomeMessage = getString(R.string.welcoming_user,name);
        tvWelcom.setText(welcomeMessage);

        TextView tvTotalProducts = view.findViewById(R.id.tvTotalProducts);
        int TotalProducts = dbHelper.getQuantitySumOfAllProducts();

        String totalProductsText = getString(R.string.total_products,TotalProducts);
        tvTotalProducts.setText(totalProductsText);

        TextView tvLowStockAlert = view.findViewById(R.id.tvLowStockAlert);
        int lowStockCount = dbHelper.getLowStockCount(5);
        String lowStockMessage = getString(R.string.low_stock_alerts,lowStockCount);
        tvLowStockAlert.setText(lowStockMessage);

        TextView tvTotalInventoryValue = view.findViewById(R.id.tvTotalInventoryValue);
        double TotalInventoryValue = dbHelper.getTotalInventoryValue();
        String TotalInventoryValueString = getString(R.string.total_inventory_value,TotalInventoryValue);
        tvTotalInventoryValue.setText(TotalInventoryValueString);

        Button btnAddProduct = view.findViewById(R.id.btnAddProduct);
        Button btnViewMovements = view.findViewById(R.id.btnViewMovements);
        Button btnGenerateMonthlyReport = view.findViewById(R.id.btnGenerateMonthlyReport);;
        Button btnStockAlerts = view.findViewById(R.id.btnStockAlerts);;
        Button btnViewReport = view.findViewById(R.id.btnViewReport);

        btnAddProduct.setOnClickListener(v -> navigateToFragment(new AddProductFragment()));
        btnViewMovements.setOnClickListener(v-> navigateToFragment(new MovementsFragment()));
        btnStockAlerts.setOnClickListener(v-> navigateToFragment(new LowStockFragment()));
        btnViewReport.setOnClickListener(v->navigateToFragment(new ReportsFragment()));
        btnGenerateMonthlyReport.setOnClickListener(v->navigateToFragment(new GenerateReportFragment()));


        RecyclerView rvRecentMovements = view.findViewById(R.id.rvRecentMovements);
        rvRecentMovements.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<Movement> lastMovements = dbHelper.getLastFiveMovements();

        MovementAdapter adapter = new MovementAdapter(lastMovements,getContext());
        rvRecentMovements.setAdapter(adapter);
        return view;
    }
    private void navigateToFragment(Fragment fragment) {
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentFrame, fragment) // `contentFrame` should be the container for fragments
                .addToBackStack(null) // Optional: Adds the transaction to the back stack
                .commit();
    }

}
