package com.mtsd.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.mtsd.R;
import com.mtsd.activity.MovimentsActivity;

public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment

        Button vMvmntBtn = view.findViewById(R.id.vMvmntBtn);

        vMvmntBtn.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), MovimentsActivity.class);
            startActivity(intent);
        });


        return view;
    }
}
