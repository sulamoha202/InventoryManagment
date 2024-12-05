package com.mtsd.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtsd.R;
import com.mtsd.adapter.MovementAdapter;
import com.mtsd.helper.RepositoryManager;
import com.mtsd.model.Movement;
import com.mtsd.helper.DatabaseHelper;
import com.mtsd.repository.MovementRepository;

import java.util.List;

public class MovementsFragment extends Fragment {

    private RepositoryManager repositoryManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movements, container, false);

        SQLiteDatabase database = new DatabaseHelper(requireContext()).getWritableDatabaseInstance();
        repositoryManager = new RepositoryManager(database);
        MovementRepository repositoryMovement = repositoryManager.getMovementRepository();

        RecyclerView rvMovements = view.findViewById(R.id.rvMovements);
        Button btnAddMovement = view.findViewById(R.id.btnAddMovement);

        List<Movement> movementList = repositoryMovement.getAllMovements();
        MovementAdapter adapter = new MovementAdapter(movementList,getContext());
        rvMovements.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMovements.setAdapter(adapter);

        // Navigate to Add Movement Fragment using manual transaction
        btnAddMovement.setOnClickListener(v -> {
            Fragment addMovementFragment = new AddMovementFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contentFrame, addMovementFragment) // Ensure this matches BaseActivity's container ID
                    .addToBackStack(null) // Optional: Adds this transaction to the back stack
                    .commit();
        });

        return view;
    }
}
