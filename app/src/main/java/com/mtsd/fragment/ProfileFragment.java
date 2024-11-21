package com.mtsd.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mtsd.R;
import com.mtsd.activity.BaseActivity;
import com.mtsd.activity.LoginActivity;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        LinearLayout logoutLayout = view.findViewById(R.id.logout);
        LinearLayout editProfileLayout = view.findViewById(R.id.editProfile);
        LinearLayout settingsLayout = view.findViewById(R.id.settings);


        editProfileLayout.setOnClickListener(v -> navigateToFragment(new EditProfileFragment()));
        settingsLayout.setOnClickListener(v -> navigateToFragment(new SettingsFragment()));
        logoutLayout.setOnClickListener(v -> logout());

        return view;
    }

    private void logout() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();
        // Clear the back stack and start LoginActivity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        // Confirm logout
        Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();
    }

    private void navigateToFragment(Fragment fragment) {
       if( getActivity() instanceof BaseActivity){
           ((BaseActivity) getActivity()).loadFragmentFromFragment(fragment);
       }
    }


}
