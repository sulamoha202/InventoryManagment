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
import com.mtsd.helper.RepositoryManager;
import com.mtsd.model.User;
import com.mtsd.helper.DatabaseHelper;

public class EditProfileFragment extends Fragment {
    private EditText etUsername,etName,etPassword,etEmail,etConfirmPassword;
    private Button btnEtProfileSave;
    private RepositoryManager repositoryManager;

    private SharedPreferences sharedPreferences;



    public EditProfileFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        SQLiteDatabase database = new DatabaseHelper(requireContext()).getWritableDatabaseInstance();
        repositoryManager = new RepositoryManager(database);

        // Inflate the layout for this fragment
        etUsername = view.findViewById(R.id.etUsername);
        etName = view.findViewById(R.id.etName);
        etPassword = view.findViewById(R.id.etPassword);
        etEmail = view.findViewById(R.id.etEmail);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        btnEtProfileSave = view.findViewById(R.id.btnEtProfileSave);

        loadUserData();

        btnEtProfileSave.setOnClickListener(v-> updateUserData());

        return view;
    }

    private void loadUserData(){
        String username = sharedPreferences.getString("username","guest");
        User user = repositoryManager.getUserRepository().getByUsername(username);

        etUsername.setText(user.getUsername());
        etName.setText(user.getName());
        etPassword.setText(user.getPassword());
        etEmail.setText(user.getEmail());
    }


    private void updateUserData() {
        User user = repositoryManager.getUserRepository().getByUsername(etUsername.getText().toString());

        if (!user.getPassword().equals(etConfirmPassword.getText().toString())) {
            Toast.makeText(getContext(), "The two passwords aren't similar :<", Toast.LENGTH_SHORT).show();
            return;
        }

        user.setName(etName.getText().toString());
        user.setEmail(etEmail.getText().toString());
        user.setPassword(etPassword.getText().toString());
        repositoryManager.getUserRepository().update(user);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", user.getUsername());
        editor.putString("name", user.getName());
        editor.putString("email", user.getEmail());
        editor.apply();

        Toast.makeText(getContext(), "User data saved successfully :)", Toast.LENGTH_SHORT).show();
        returnToPrevouisFragment(new ProfileFragment());

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
