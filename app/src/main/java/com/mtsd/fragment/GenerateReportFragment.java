package com.mtsd.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mtsd.R;

public class GenerateReportFragment extends Fragment {

    public GenerateReportFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_generate_report, container, false);

        CardView monthlyReportCard = view.findViewById(R.id.cardMonthlyReport);
        CardView categoryReportCard = view.findViewById(R.id.cardCategoryReport);

        monthlyReportCard.setOnClickListener(v -> {
        });

        categoryReportCard.setOnClickListener(v -> {
        });

        return view;
    }
}
