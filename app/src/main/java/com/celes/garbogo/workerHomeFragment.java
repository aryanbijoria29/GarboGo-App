package com.celes.garbogo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class workerHomeFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    AdapterWorkerRV adapterWorkerRV;
    ArrayList<Complaint> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_worker_home, container, false);

        recyclerView = view.findViewById(R.id.userCompList);
        databaseReference = FirebaseDatabase.getInstance().getReference("Complaint");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
        adapterWorkerRV = new AdapterWorkerRV(getContext(),list);
        recyclerView.setAdapter(adapterWorkerRV);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Complaint complaint = dataSnapshot.getValue(Complaint.class);
                    list.add(complaint);
                }
                adapterWorkerRV.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}