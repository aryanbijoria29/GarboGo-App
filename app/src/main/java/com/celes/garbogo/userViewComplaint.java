package com.celes.garbogo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class userViewComplaint extends AppCompatActivity {
    TextView userSubject, userQuery,userAddress, userStatus, userName, userEmail, userPhone;
    Button deleteComp;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view_complaint);
        userSubject = findViewById(R.id.subjectUserComp);
        userQuery = findViewById(R.id.queryUserComp);
        userAddress = findViewById(R.id.addressUserComp);
        userStatus = findViewById(R.id.statusUserComp);
        userName = findViewById(R.id.nameUserComp);
        userEmail = findViewById(R.id.emailUserComp);
        userPhone = findViewById(R.id.phoneUserComp);
        deleteComp = findViewById(R.id.deleteCompBtn);


        Intent intent = getIntent();
        String compID = intent.getStringExtra("comID");

        readDataComplaint(compID);

        deleteComp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder deleteConfirmation = new AlertDialog.Builder(userViewComplaint.this);
                deleteConfirmation.setTitle("Confirm Delete");
                deleteConfirmation.setMessage("Are you sure that you want to delete this request?");
                deleteConfirmation.setPositiveButton("delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference("Complaint").child(compID).removeValue();
                        Intent i = new Intent(userViewComplaint.this,trackRequestUser.class);
                        startActivity(i);
                        finish();
                    }
                });
                deleteConfirmation.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                deleteConfirmation.create().show();
            }
        });

    }

    private void readDataComplaint(String compID) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Complaint");
        databaseReference.child(compID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String uSub = String.valueOf(snapshot.child("subject").getValue());
                    String uQuery = String.valueOf(snapshot.child("query").getValue());
                    String uAddress = String.valueOf(snapshot.child("address").getValue());
                    String uStatus = String.valueOf(snapshot.child("status").getValue());
                    String userUID = String.valueOf(snapshot.child("userUid").getValue());

                    userSubject.setText(uSub);
                    userQuery.setText(uQuery);
                    userAddress.setText(uAddress);
                    userStatus.setText(uStatus);
                    if(uStatus.equals("completed")) userStatus.setTextColor(Color.RED);

                    readDataUser(userUID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readDataUser(String userUID) {
        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.child(userUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uName = String.valueOf(snapshot.child("name").getValue());
                String uEmail = String.valueOf(snapshot.child("email").getValue());
                String uPhone = String.valueOf(snapshot.child("phone_no").getValue());

                userName.setText(uName);
                userEmail.setText(uEmail);
                userPhone.setText(uPhone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}