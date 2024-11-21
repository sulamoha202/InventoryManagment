package com.mtsd.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mtsd.R;
import com.mtsd.util.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddMovementFragment extends Fragment {
    private Spinner spProduct, spMovementType;
    private EditText etQuantity;
    private Button btnSaveMovement;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_add_movement, container, false);

        // Initialize UI components
        spProduct = view.findViewById(R.id.spProduct);
        spMovementType = view.findViewById(R.id.spMovementType);
        etQuantity = view.findViewById(R.id.etQuantity);
        btnSaveMovement = view.findViewById(R.id.btnSaveMovement);

        // Initialize database helper
        dbHelper = new DatabaseHelper(requireContext());

        // Load products into spinner
        List<String> productNames = dbHelper.getProductNames();
        ArrayAdapter<String> productAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, productNames);
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProduct.setAdapter(productAdapter);

        // Setup movement type spinner
        String[] movementTypes = {"ADD", "REMOVE"};
        ArrayAdapter<String> movementAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, movementTypes);
        movementAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMovementType.setAdapter(movementAdapter);

        // Save movement on button click
        btnSaveMovement.setOnClickListener(v -> saveMovement());

        return view;
    }

    private void saveMovement() {
        String productName = spProduct.getSelectedItem().toString();
        String movementType = spMovementType.getSelectedItem().toString();
        String quantityStr = etQuantity.getText().toString();

        if (productName.isEmpty() || movementType.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);
        if (quantity <= 0) {
            Toast.makeText(requireContext(), "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current date and time
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Get product ID from product name
        int productId = dbHelper.getProductIdByName(productName);
        int currentQuantity = dbHelper.getProductQuantityById(productId);

        int newQuantity = movementType.equals("ADD") ? currentQuantity + quantity : currentQuantity - quantity;

        if (newQuantity < 0) {
            Toast.makeText(requireContext(), "Insufficient stock for this operation", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isProductUpdated = dbHelper.updateProductQuantity(productId, newQuantity);
        if (!isProductUpdated) {
            Toast.makeText(requireContext(), "Failed to update product quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert into database
        boolean success = dbHelper.addInventoryMovement(productId, movementType, quantity, currentDate);

        if (success) {
            Toast.makeText(requireContext(), "Movement saved successfully", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed(); // Navigate back
        } else {
            Toast.makeText(requireContext(), "Failed to save movement", Toast.LENGTH_SHORT).show();
        }
    }
}
