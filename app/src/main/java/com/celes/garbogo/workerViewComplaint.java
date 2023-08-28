package com.celes.garbogo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class workerViewComplaint extends AppCompatActivity {
    TextView userSubject, userQuery,userAddress, userStatus, userName, userEmail, userPhone, markMap;
    Button deleteComp, navigate, confirm;
    DatabaseReference databaseReference;
    String add;
    String markMapval, latitude, longitude;
    RadioGroup radioGroup;
    RadioButton pendingBtn, completedBtn;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_view_complaint);
        userSubject = findViewById(R.id.subjectComp);
        userQuery = findViewById(R.id.queryComp);
        userAddress = findViewById(R.id.addressComp);
        userStatus = findViewById(R.id.statusComp);
        userName = findViewById(R.id.nameComp);
        userEmail = findViewById(R.id.emailComp);
        userPhone = findViewById(R.id.phoneComp);
        deleteComp = findViewById(R.id.deleteBtn);
        navigate = findViewById(R.id.navigateBtn);
        markMap = findViewById(R.id.markMapComp);
        confirm = findViewById(R.id.conBtn);

        progressDialog = new ProgressDialog(this);

        Intent intent = getIntent();
        String compID = intent.getStringExtra("comID");

        readDataComplaint(compID);

        radioGroupSetUp(compID);

        deleteComp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder deleteConfirmation = new AlertDialog.Builder(workerViewComplaint.this);
                deleteConfirmation.setTitle("Confirm Delete");
                deleteConfirmation.setMessage("Are you sure that you want to delete this request?");
                deleteConfirmation.setPositiveButton("delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference("Complaint").child(compID).removeValue();
                        Intent i = new Intent(workerViewComplaint.this,workerMainActivity.class);
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
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap(add);
            }
        });
    }

    private void openMap(String add) {
        String uriString;
        if(markMapval.equals("yes")){
            //uriString = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + add + ")";
            uriString = "geo:0,0?q="+latitude+","+longitude+"("+add+")";
        } else {
            uriString = "http://maps.google.co.in/maps?q=" + add;
        }
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
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
                    markMapval = String.valueOf(snapshot.child("markMap").getValue());
                    latitude = String.valueOf(snapshot.child("latitude").getValue());
                    longitude = String.valueOf(snapshot.child("longitude").getValue());
                    add=uAddress;

                    defaultRadioBtn(uStatus);

                    userSubject.setText(uSub);
                    userQuery.setText(uQuery);
                    userAddress.setText(uAddress);
                    userStatus.setText(uStatus);
                    markMap.setText(markMapval);
                    if(uStatus.equals("completed")) userStatus.setTextColor(Color.RED);
                    else userStatus.setTextColor(Color.parseColor("#FCA510"));

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
    private void radioGroupSetUp(String compID) {
        radioGroup = findViewById(R.id.radioGrpStatus);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.pendingRB:
                        statusUpdate("pending",compID);
                        break;
                    case R.id.completedRB:
                        statusUpdate("completed",compID);
                        break;
                }
            }
        });
    }

    private void statusUpdate(String val, String compID){
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder confirmation = new AlertDialog.Builder(workerViewComplaint.this);
                confirmation.setTitle("Confirmation");
                confirmation.setMessage("Press ok if you want update status");
                confirmation.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.setMessage("Please wait..");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        FirebaseDatabase.getInstance().getReference("Complaint")
                                .child(compID).child("status").setValue(val).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        if(task.isSuccessful()){
                                            Intent intent = new Intent(workerViewComplaint.this, workerMainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else{
                                            Toast.makeText(workerViewComplaint.this, "Can't change status", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
                confirmation.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                confirmation.create().show();
            }
        });
    }

    private void defaultRadioBtn(String Status) {
        radioGroup.clearCheck();
        pendingBtn = findViewById(R.id.pendingRB);
        completedBtn = findViewById(R.id.completedRB);
        switch (Status) {
            case "pending":
                pendingBtn.setChecked(true);
                break;
            case "completed":
                completedBtn.setChecked(true);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(workerViewComplaint.this, workerMainActivity.class);
        startActivity(intent);
        finish();
    }
}