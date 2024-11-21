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
import com.mtsd.adapter.RecentMovementsAdapter;
import com.mtsd.model.ReportRecentMovement;
import com.mtsd.util.DatabaseHelper;

import java.util.List;

public class RecentMovementsFragment extends Fragment {

    private RecyclerView rvRecentMovements;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_movements, container, false);

        rvRecentMovements = view.findViewById(R.id.rvRecentMovements);
        rvRecentMovements.setLayoutManager(new LinearLayoutManager(getContext()));

        databaseHelper = new DatabaseHelper(getContext());
        List<ReportRecentMovement> movementList = databaseHelper.getRecentMovements();

        RecentMovementsAdapter adapter = new RecentMovementsAdapter(movementList);
        rvRecentMovements.setAdapter(adapter);

        return view;
    }
}
