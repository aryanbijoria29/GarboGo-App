package com.celes.garbogo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class trackRequestUser extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    AdapterUserRV adapterUserRV;
    ArrayList<Complaint> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_request_user);

        recyclerView = findViewById(R.id.userList);
        databaseReference = FirebaseDatabase.getInstance().getReference("Complaint");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        adapterUserRV = new AdapterUserRV(this,list);
        recyclerView.setAdapter(adapterUserRV);

        String liveUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Complaint complaint = dataSnapshot.getValue(Complaint.class);
                    if(complaint.userUid.equals(liveUserUID)) list.add(complaint);
                }
                adapterUserRV.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(trackRequestUser.this, userMainActivity.class);
        startActivity(intent);
        finish();
    }
}