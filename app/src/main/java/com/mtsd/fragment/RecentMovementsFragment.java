package com.mtsd.fragment;

import android.database.sqlite.SQLiteDatabase;
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
import com.mtsd.helper.RepositoryManager;
import com.mtsd.model.Movement;
import com.mtsd.model.ReportRecentMovement;
import com.mtsd.helper.DatabaseHelper;

import java.util.List;

public class RecentMovementsFragment extends Fragment {

    private RecyclerView rvRecentMovements;
    private RepositoryManager repositoryManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent_movements, container, false);

        rvRecentMovements = view.findViewById(R.id.rvRecentMovements);
        rvRecentMovements.setLayoutManager(new LinearLayoutManager(getContext()));

        SQLiteDatabase database = new DatabaseHelper(getContext()).getReadableDatabase();
        repositoryManager = new RepositoryManager(database);
        List<Movement> movementList = repositoryManager.getMovementRepository().getLastMovements(10);//databaseHelper.getRecentMovements();

        RecentMovementsAdapter adapter = new RecentMovementsAdapter(movementList, repositoryManager.getProductRepository());
        rvRecentMovements.setAdapter(adapter);

        return view;
    }
}
