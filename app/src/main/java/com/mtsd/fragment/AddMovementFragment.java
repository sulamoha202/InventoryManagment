package com.mtsd.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
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

import com.bumptech.glide.load.engine.Resource;
import com.mtsd.R;
import com.mtsd.helper.RepositoryManager;
import com.mtsd.model.Movement;
import com.mtsd.model.Product;
import com.mtsd.repository.MovementRepository;
import com.mtsd.repository.ProductRepository;
import com.mtsd.helper.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddMovementFragment extends Fragment {
    private Spinner spProduct, spMovementType;
    private EditText etQuantity;
    private Button btnSaveMovement;
    private RepositoryManager repositoryManager;
    private List<Product> products;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_movement, container, false);

        spProduct = view.findViewById(R.id.spProduct);
        spMovementType = view.findViewById(R.id.spMovementType);
        etQuantity = view.findViewById(R.id.etQuantity);
        btnSaveMovement = view.findViewById(R.id.btnSaveMovement);

        SQLiteDatabase database = new DatabaseHelper(requireContext()).getWritableDatabaseInstance();
        repositoryManager = new RepositoryManager(database);

        loadProductNames();

        String[] movementTypes = {getString(R.string.movement_type_in), getString(R.string.movement_type_out)};
        ArrayAdapter<String> movementAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, movementTypes);
        movementAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMovementType.setAdapter(movementAdapter);

        btnSaveMovement.setOnClickListener(v -> saveMovement());

        return view;
    }

    private void loadProductNames(){
        ProductRepository productRepository = repositoryManager.getProductRepository();
        products = productRepository.getAllProducts();
        List<String> productsName = new ArrayList<>();
        for(Product p : products){
            productsName.add(p.getName());
        }
        ArrayAdapter<String> productAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, productsName);
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProduct.setAdapter(productAdapter);
    }

    private void saveMovement() {
        ProductRepository productRepository = repositoryManager.getProductRepository();

        String productName = spProduct.getSelectedItem().toString();
        String movementType = spMovementType.getSelectedItem().toString();
        String quantityStr = etQuantity.getText().toString();

        if(!validateInput(spProduct, spMovementType, etQuantity )) return;

        String currentDate = getCurrentDateTime();


        // Get product ID from product name
        int productId = getProductIdByName(productRepository, productName);

        int currentQuantity = productRepository.getProductQuantityById(productId);
        int quantity = Integer.parseInt(quantityStr);

        int newQuantity = movementType.equals(getString(R.string.movement_type_in))
                ? currentQuantity + quantity
                : currentQuantity - quantity;

        if (newQuantity < 0) {
            Toast.makeText(requireContext(), "Insufficient stock for this operation", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isProductUpdated = productRepository.updateProductQuantity(productId, newQuantity);

        if (!isProductUpdated) {
            Toast.makeText(requireContext(), "Failed to update product quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        MovementRepository movementRepository = repositoryManager.getMovementRepository();
        Movement movement = new Movement(0,productId,movementType,quantity,currentDate);
        boolean success = movementRepository.insertMovement(movement);

        if (success) {
            Toast.makeText(requireContext(), "Movement saved successfully", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed(); // Navigate back
        } else {
            Toast.makeText(requireContext(), "Failed to save movement", Toast.LENGTH_SHORT).show();
        }

        returnToPrevouisFragment(new MovementsFragment());
    }

    private boolean validateInput(Object ...objs){
        for(Object obj : objs){
            if(obj instanceof EditText){
                try {
                    int quantity = Integer.parseInt(((EditText) obj).getText().toString());
                    if( quantity <= 0 ){
                        Toast.makeText(requireContext(), "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    if(quantity == Integer.MAX_VALUE){
                        Toast.makeText(requireContext(), "Quantity exceeds maximum allowed value", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }catch (NumberFormatException e){
                    Toast.makeText(requireContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            if(obj instanceof Spinner){
                if(((Spinner) obj).getSelectedItem().toString().isEmpty()){
                    return false;
                }
            }
        }
        return true;
    }

    private String getCurrentDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }
    private int getProductIdByName(ProductRepository productRepository, String productName) {
        Product product = productRepository.getProductByName(productName);
        if (product == null) {
            Toast.makeText(requireContext(), "Product not found", Toast.LENGTH_SHORT).show();
            throw new IllegalStateException("Product not found");
        }
        return product.getId();
    }

    private void returnToPrevouisFragment(Fragment fragment){
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentFrame, fragment) // `contentFrame` should be the container for fragments
                .addToBackStack(null) // Optional: Adds the transaction to the back stack
                .commit();
    }

}
