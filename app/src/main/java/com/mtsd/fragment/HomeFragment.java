package com.mtsd.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.mtsd.R;
import com.mtsd.adapter.MovementAdapter;
import com.mtsd.helper.RepositoryManager;
import com.mtsd.model.Movement;
import com.mtsd.model.Revenue;
import com.mtsd.helper.DatabaseHelper;
import com.mtsd.repository.MovementRepository;
import com.mtsd.repository.ProductRepository;
import com.mtsd.repository.RevenueRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final int LOW_STOCK_THRESHOLD = 5;
    private BarChart barChart;
    private RepositoryManager repositoryManager;
    private SharedPreferences sharedPreferences;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);


        SQLiteDatabase database = new DatabaseHelper(getContext()).getReadableDatabase();
        repositoryManager = new RepositoryManager(database);

        initializeUI(view);
        populateRevenueChart(view);
        displayUserInfo(view);
        displayInventoryInfo(view);
        setButtonListeners(view);
        setupRecentMovements(view);

        return view;
    }
    private void initializeUI(View view){
        barChart = view.findViewById(R.id.barChart);
    }

    private void populateRevenueChart(View view){
        RevenueRepository revenueRepository = repositoryManager.getRevenueRepository();
        List<String> lastWeekDates = getLastWeekDates();

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for(int i = 0; i < lastWeekDates.size(); i++){
            String date = lastWeekDates.get(i);
            Revenue revenue = revenueRepository.getRevenueByDate(date);
            if(revenue != null){
                String label = date;
                barEntries.add(new BarEntry(i,(float)revenue.getRevenue(),label));
            }
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, getString(R.string.last_week_revenue));
        barDataSet.setColors(ContextCompat.getColor(getContext(), R.color.primary_button_color));
        barDataSet.setValueTextColor(ContextCompat.getColor(getContext(), R.color.primary_text_color));
        barDataSet.setValueTextSize(12f);

        BarData barData = new BarData(barDataSet);

        // Configure the chart
        barChart.setData(barData);
        configureBarChart(lastWeekDates);
    }


    private void configureBarChart(List<String> labels) {
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.animateY(1000);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_text_color));

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_text_color));

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        Legend legend = barChart.getLegend();
        legend.setTextColor(ContextCompat.getColor(getContext(), R.color.primary_text_color));
        barChart.invalidate();
    }


    private void displayUserInfo(View view){
        String userName = sharedPreferences.getString("name","Guest");
        TextView tvWelcome = view.findViewById(R.id.tvWelcome);
        tvWelcome.setText(getString(R.string.welcoming_user, userName));
    }

    private void displayInventoryInfo(View view) {
        displayTotalProducts(view);
        displayLowStockAlert(view);
        displayTotalInventoryValue(view);
    }

    private void displayTotalProducts(View view) {
        ProductRepository productRepository = repositoryManager.getProductRepository();
        TextView tvTotalProducts = view.findViewById(R.id.tvTotalProducts);
        int totalProducts = productRepository.getQuantitySumOfAllProducts();
        tvTotalProducts.setText(getString(R.string.total_products, totalProducts));
    }

    private void displayLowStockAlert(View view) {
        ProductRepository productRepository = repositoryManager.getProductRepository();
        TextView tvLowStockAlert = view.findViewById(R.id.tvLowStockAlert);
        int lowStockCount = productRepository.getLowStockCount(LOW_STOCK_THRESHOLD);
        tvLowStockAlert.setText(getString(R.string.low_stock_alerts, lowStockCount));
    }

    private void displayTotalInventoryValue(View view) {
        ProductRepository productRepository = repositoryManager.getProductRepository();
        TextView tvTotalInventoryValue = view.findViewById(R.id.tvTotalInventoryValue);
        double totalInventoryValue = productRepository.getTotalInventoryValue();
        tvTotalInventoryValue.setText(getString(R.string.total_inventory_value, totalInventoryValue));
    }

    private void setButtonListeners(View view) {
        Button btnAddProduct = view.findViewById(R.id.btnAddProduct);
        Button btnViewMovements = view.findViewById(R.id.btnViewMovements);
        Button btnGenerateMonthlyReport = view.findViewById(R.id.btnGenerateMonthlyReport);
        Button btnStockAlerts = view.findViewById(R.id.btnStockAlerts);
        Button btnViewReport = view.findViewById(R.id.btnViewReport);

        btnAddProduct.setOnClickListener(v -> navigateToFragment(new AddProductFragment()));
        btnViewMovements.setOnClickListener(v -> navigateToFragment(new MovementsFragment()));
        btnStockAlerts.setOnClickListener(v -> navigateToFragment(new LowStockFragment()));
        btnViewReport.setOnClickListener(v -> navigateToFragment(new ReportsFragment()));
        btnGenerateMonthlyReport.setOnClickListener(v -> navigateToFragment(new GenerateReportFragment()));
    }

    private void setupRecentMovements(View view) {
        MovementRepository movementRepository = repositoryManager.getMovementRepository();
        RecyclerView rvRecentMovements = view.findViewById(R.id.rvRecentMovements);
        rvRecentMovements.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<Movement> lastMovements = movementRepository.getLastMovements(5);
        MovementAdapter adapter = new MovementAdapter(lastMovements, getContext());
        rvRecentMovements.setAdapter(adapter);
    }


    private List<String> getLastWeekDates(){
        List<String> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        for(int i = 0; i < 7 ; i++){
            dates.add(dateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DATE,-1);
        }
        return dates;
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
