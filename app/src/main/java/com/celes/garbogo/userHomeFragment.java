package com.celes.garbogo;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class userHomeFragment extends Fragment {
    View view;
    ImageButton truckBtn, trackBtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_home, container, false);
        truckBtn = view.findViewById(R.id.imageButtonTruck);
        trackBtn = view.findViewById(R.id.imageButtonTrack);

        truckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), requestService.class);
                startActivity(intent);
            }
        });

        trackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),trackRequestUser.class);
                startActivity(intent);
            }
        });



        return view;
    }
}