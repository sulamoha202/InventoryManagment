package com.mtsd.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mtsd.R;
import com.mtsd.adapter.MovementAdapter;
import com.mtsd.model.Movement;
import com.mtsd.util.DatabaseHelper;

import java.util.List;

public class MovimentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimientos);

        RecyclerView rvMovements = findViewById(R.id.rvMovements);
        Button btnAddMovement = findViewById(R.id.btnAddMovement);

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Load movements from the database
        List<Movement> movementList = dbHelper.getAllMovements();
        MovementAdapter adapter = new MovementAdapter(movementList);
        rvMovements.setLayoutManager(new LinearLayoutManager(this));
        rvMovements.setAdapter(adapter);

        // Navigate to the Add Movement screen
        btnAddMovement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                Intent intent = new Intent(MovimentsActivity.this, AddMovementActivity.class);
                startActivity(intent);*/
            }
        });
    }
}
