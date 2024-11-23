package com.mtsd.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.mtsd.R;
import com.mtsd.activity.GenerateReportActivity;
import com.mtsd.util.DatabaseHelper;

import java.util.ArrayList;

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

        // Create data entries for the PieChart
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(25f, "Category 1"));
        pieEntries.add(new PieEntry(35f, "Category 2"));
        pieEntries.add(new PieEntry(40f, "Category 3"));

        // Create PieDataSet
        PieDataSet dataSet = new PieDataSet(pieEntries, "Inventory Distribution");
        dataSet.setColors(new int[]{R.color.primary_button_color, R.color.primary_button_color, R.color.primary_button_color}); // Set colors

        // Create PieData and set it on PieChart
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        // Optional: Set chart settings
        pieChart.setCenterText("Inventory");
        pieChart.setUsePercentValues(true);
        pieChart.invalidate();  // Refresh the chart

        TextView tvWelcom = view.findViewById(R.id.tvWelcome);
        TextView tvTotalProducts = view.findViewById(R.id.tvTotalProducts);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        Log.d("SharedPreference","Saved Username: "+ sharedPreferences.getString("username","Not Saved"));
        String username = sharedPreferences.getString("username","Guest");
        String welcomeMessage = getString(R.string.welcoming_user,username);
        tvWelcom.setText(welcomeMessage);

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
        Button btnGenerateReport = view.findViewById(R.id.btnGenerateReport);;
        Button btnStockAlerts = view.findViewById(R.id.btnStockAlerts);;

        btnAddProduct.setOnClickListener(v -> navigateToFragment(new AddProductFragment()));
        btnViewMovements.setOnClickListener(v-> navigateToFragment(new MovementsFragment()));
        btnStockAlerts.setOnClickListener(v-> navigateToFragment(new LowStockFragment()));
        btnGenerateReport.setOnClickListener(v->{
            Intent intent = new Intent(getContext(), GenerateReportActivity.class);
            startActivity(intent);
        });
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
