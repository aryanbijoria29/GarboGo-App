package com.celes.garbogo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class workerProfileFragment extends Fragment {
    View view;
    TextView workerName, userName;
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_wroker_profile, container, false);

        workerName = view.findViewById(R.id.nameWorker);
        userName = view.findViewById(R.id.usernameWorker);

        databaseReference = FirebaseDatabase.getInstance().getReference("Worker");

        sharedPreferences = getActivity().getSharedPreferences("workerLogin", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String userID = sharedPreferences.getString("userName", "default :(");

        databaseReference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String wName = String.valueOf(snapshot.child("name").getValue());
                    String wUsername = String.valueOf(snapshot.child("username").getValue());

                    workerName.setText(wName);
                    userName.setText(wUsername);
                } else {
                    Toast.makeText(getContext(), "Can't fetch data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}