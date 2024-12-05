package com.mtsd.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.mtsd.R;
import com.mtsd.helper.DatabaseHelper;
import com.mtsd.helper.RepositoryManager;
import com.mtsd.model.StoreInfo;
import com.mtsd.model.User;
import com.mtsd.util.StoreInfoUpdateListener;

public class EditStoreInfoFragment extends Fragment {

    private EditText etStoreName, etStoreAddress, etStorePhone, etStoreEmail;
    private StoreInfo currentStoreInfo;
    private RepositoryManager repositoryManager;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_store_info, container, false);

        etStoreName = view.findViewById(R.id.etStoreName);
        etStoreAddress = view.findViewById(R.id.etStoreAddress);
        etStorePhone = view.findViewById(R.id.etStorePhone);
        etStoreEmail = view.findViewById(R.id.etStoreEmail);
        Button btnSave = view.findViewById(R.id.btnSave);

        sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);


        SQLiteDatabase database = new DatabaseHelper(requireContext()).getWritableDatabaseInstance();
        repositoryManager = new RepositoryManager(database);

        loadCurrentStoreInfo();

        btnSave.setOnClickListener(v -> saveStoreInfo());

        return view;
    }

    // Load the current store info (this is just an example, you can use actual data from a database)
    private void loadCurrentStoreInfo() {
        User user = repositoryManager.getUserRepository().getByUsername(sharedPreferences.getString("username","guest"));
        if(user == null){
            Toast.makeText(getContext(),"User not found!",Toast.LENGTH_SHORT).show();
            return;
        }

        currentStoreInfo = repositoryManager.getStoreInfoRepository().getById(user.getStoreInfoId());

        if(currentStoreInfo == null){
            Toast.makeText(getContext(),"Store Info not found!",Toast.LENGTH_SHORT).show();
            return;
        }

        etStoreName.setText(currentStoreInfo.getName());
        etStoreAddress.setText(currentStoreInfo.getAddress());
        etStorePhone.setText(currentStoreInfo.getPhone());
        etStoreEmail.setText(currentStoreInfo.getEmail());
    }

    private void saveStoreInfo() {
        String storeName = etStoreName.getText().toString();
        String storeAddress = etStoreAddress.getText().toString();
        String storePhone = etStorePhone.getText().toString();
        String storeEmail = etStoreEmail.getText().toString();

        if (storeName.isEmpty() || storeAddress.isEmpty() || storePhone.isEmpty() || storeEmail.isEmpty()) {
            Toast.makeText(getContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        currentStoreInfo.setName(storeName);
        currentStoreInfo.setAddress(storeAddress);
        currentStoreInfo.setPhone(storePhone);
        currentStoreInfo.setEmail(storeEmail);

        repositoryManager.getStoreInfoRepository().update(currentStoreInfo);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("store_name", currentStoreInfo.getName());
        editor.putString("store_address", currentStoreInfo.getAddress());
        editor.putString("store_phone", currentStoreInfo.getPhone());
        editor.putString("store_email", currentStoreInfo.getPhone());
        editor.apply();

        if(getActivity() instanceof StoreInfoUpdateListener){
            ((StoreInfoUpdateListener) getActivity()).onStoreInfoUpdated(currentStoreInfo.getName());
        }

        Toast.makeText(getContext(), "Store info updated successfully!", Toast.LENGTH_SHORT).show();

       returnToPrevouisFragment(new StoreDetailsFragment());
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
