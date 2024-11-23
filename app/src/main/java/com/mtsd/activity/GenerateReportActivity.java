package com.mtsd.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.mtsd.R;

public class GenerateReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_report);

        // Example listeners for cards
        CardView monthlyReportCard = findViewById(R.id.cardMonthlyReport);
        CardView categoryReportCard = findViewById(R.id.cardCategoryReport);

        monthlyReportCard.setOnClickListener(v -> {
            // Handle monthly report generation
        });

        categoryReportCard.setOnClickListener(v -> {
            // Handle category-wise report generation
        });
    }
}
