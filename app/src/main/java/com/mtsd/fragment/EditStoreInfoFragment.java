package com.mtsd.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.mtsd.R;
import com.mtsd.model.StoreInfo;

public class EditStoreInfoFragment extends Fragment {

    private EditText etStoreName, etStoreAddress, etStorePhone, etStoreEmail;
    private Button btnSave;
    private StoreInfo currentStoreInfo; // Assuming you will load current store info from the database

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_store_info, container, false);

        // Initialize EditTexts and Button
        etStoreName = view.findViewById(R.id.etStoreName);
        etStoreAddress = view.findViewById(R.id.etStoreAddress);
        etStorePhone = view.findViewById(R.id.etStorePhone);
        etStoreEmail = view.findViewById(R.id.etStoreEmail);
        btnSave = view.findViewById(R.id.btnSave);

        // Load current store info (this could be from a database or an API call)
        loadCurrentStoreInfo();

        // Save button click listener
        btnSave.setOnClickListener(v -> saveStoreInfo());

        return view;
    }

    // Load the current store info (this is just an example, you can use actual data from a database)
    private void loadCurrentStoreInfo() {
        // Simulate fetching data from the database
        currentStoreInfo = new StoreInfo(1,"Store Name", "123 Address St", "123-456-7890", "store@example.com");

        // Set current store info into the EditTexts
        etStoreName.setText(currentStoreInfo.getName());
        etStoreAddress.setText(currentStoreInfo.getAddress());
        etStorePhone.setText(currentStoreInfo.getPhone());
        etStoreEmail.setText(currentStoreInfo.getEmail());
    }

    // Save the store information (you can save this data into your database)
    private void saveStoreInfo() {
        String storeName = etStoreName.getText().toString();
        String storeAddress = etStoreAddress.getText().toString();
        String storePhone = etStorePhone.getText().toString();
        String storeEmail = etStoreEmail.getText().toString();

        // Update the store info object (you may want to save this to a database)
        currentStoreInfo.setName(storeName);
        currentStoreInfo.setAddress(storeAddress);
        currentStoreInfo.setPhone(storePhone);
        currentStoreInfo.setEmail(storeEmail);

        // Save the updated store info (this is just an example)
        // You can use SQLite, SharedPreferences, or an API to save the data
        // For example:
        // DatabaseHelper.saveStoreInfo(currentStoreInfo);

        // Show a Toast or other confirmation message
        Toast.makeText(getContext(), "Store info updated successfully!", Toast.LENGTH_SHORT).show();
    }
}
