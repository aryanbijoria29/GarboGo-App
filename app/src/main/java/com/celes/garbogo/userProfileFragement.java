package com.celes.garbogo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class userProfileFragement extends Fragment {
    View view;
    TextView name, email, phoneNO;
    DatabaseReference databaseReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_user_profile_fragement, container, false);
        name = view.findViewById(R.id.nameUser);
        email = view.findViewById(R.id.emailUser);
        phoneNO = view.findViewById(R.id.phoneUser);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        readData(uid);

        return view;
    }

    private void readData(String uid) {
        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String uName = String.valueOf(snapshot.child("name").getValue());
                    String uEmail = String.valueOf(snapshot.child("email").getValue());
                    String uPhone = String.valueOf(snapshot.child("phone_no").getValue());
                    name.setText(uName);
                    email.setText(uEmail);
                    phoneNO.setText(uPhone);
                } else {
                    Toast.makeText(getContext(), "Can't fetch data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Try Again Later", Toast.LENGTH_SHORT).show();
            }
        });
    }
}