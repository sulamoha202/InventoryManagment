package com.mtsd.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mtsd.R;
import com.mtsd.activity.BaseActivity;


public class ReportsFragment extends Fragment {
    private Button btnStockSummary, btnRecentMovements, btnLowStock;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        btnStockSummary = view.findViewById(R.id.btnStockSummary);
        btnRecentMovements = view.findViewById(R.id.btnRecentMovements);
        btnLowStock = view.findViewById(R.id.btnLowStock);

        btnStockSummary.setOnClickListener(v -> navigateToFragment(new StockSummaryFragment()));
        btnRecentMovements.setOnClickListener(v -> navigateToFragment(new RecentMovementsFragment()));
        btnLowStock.setOnClickListener(v -> navigateToFragment(new LowStockFragment()));



        return view;
    }
    private void navigateToFragment(Fragment fragment) {
        if( getActivity() instanceof BaseActivity){
            ((BaseActivity) getActivity()).loadFragmentFromFragment(fragment);
        }
    }
}
