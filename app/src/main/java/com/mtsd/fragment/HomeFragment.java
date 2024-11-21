package com.mtsd.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.mtsd.R;
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

        return view;
    }
}
