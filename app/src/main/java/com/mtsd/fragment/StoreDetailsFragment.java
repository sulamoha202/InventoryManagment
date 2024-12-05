package com.mtsd.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.mtsd.R;
import com.mtsd.activity.BaseActivity;

public class StoreDetailsFragment extends Fragment {

    private TextView tvStoreName, tvStoreAddress, tvStorePhone,tvStoreEmail;
    private Button btnEditStoreInfo;

    public StoreDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store_details, container, false);

        // Initialize Views
        tvStoreName = view.findViewById(R.id.tvStoreName);
        tvStoreAddress = view.findViewById(R.id.tvStoreAddress);
        tvStorePhone = view.findViewById(R.id.tvStorePhone);
        btnEditStoreInfo = view.findViewById(R.id.btnEditStoreInfo);
        tvStoreEmail = view.findViewById(R.id.tvStoreEmail);


        // Get store_id from SharedPreferences
        SharedPreferences preferences = getActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);
        String storeName = preferences.getString("store_name","Null");
        String storeAddress = preferences.getString("store_address","Null");
        String storePhone = preferences.getString("store_phone","Null");
        String storeEmail = preferences.getString("store_email","Null");

        tvStoreAddress.setText(storeAddress);
        tvStoreName.setText(storeName);
        tvStorePhone.setText(storePhone);
        tvStoreEmail.setText(storeEmail);



        btnEditStoreInfo.setOnClickListener(v -> navigateToFragment(new EditStoreInfoFragment()));

        return view;
    }


    private void navigateToFragment(Fragment fragment) {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).loadFragmentFromFragment(fragment);
        }
    }
}
