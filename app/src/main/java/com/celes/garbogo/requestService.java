package com.celes.garbogo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class requestService extends AppCompatActivity implements LocationSendActivity {
    EditText subject, query, addressField;
    Button submitBtn;
    String add;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    String lati, longi;
    SwitchCompat switchCompat;
    String markMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_service);

        replaceFragment(new MapsFragmentUser());

        subject = findViewById(R.id.subjectET);
        query = findViewById(R.id.queryET);
        addressField = findViewById(R.id.address_et);
        submitBtn = findViewById(R.id.submitBtn);
        progressDialog = new ProgressDialog(requestService.this);
        switchCompat = findViewById(R.id.switch1);

        markMap="yes";
        addressField.setFocusableInTouchMode(false);
        addressField.clearFocus();
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(switchCompat.isChecked()){
                    markMap="yes";
                    addressField.setFocusableInTouchMode(false);
                    addressField.clearFocus();
                } else {
                    markMap="no";
                    addressField.setFocusableInTouchMode(true);
                }
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add=addressField.getText().toString().trim();
                submitComplaint(add);
            }
        });

    }

    private void submitComplaint(String add) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String sub = subject.getText().toString().trim();
        String que = query.getText().toString().trim();

        if(sub.isEmpty()){
            subject.setError("Please enter a subject");
            subject.requestFocus();
        } else if(sub.length()>50){
            subject.setError("Subject should of less than 50 characters, including spaces");
            subject.requestFocus();
        } else if (que.isEmpty()) {
            query.setError("Please enter a valid query");
            query.requestFocus();
        } else if (add.isEmpty()){
            addressField.setError("Please enter a valid address");
            addressField.requestFocus();
        } else{
            AlertDialog.Builder confirmation = new AlertDialog.Builder(requestService.this);
            confirmation.setTitle("Confirmation");
            confirmation.setMessage(R.string.confirmMsg);
            confirmation.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    databaseReference= FirebaseDatabase.getInstance().getReference("Complaint");
                    String compID = databaseReference.push().getKey();

                    progressDialog.setMessage("Please wait..");
                    progressDialog.setTitle("Submitting");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    Complaint complaint = new Complaint(uid,sub,que,add,"pending",compID,lati,longi,markMap);
                    databaseReference.child(compID).setValue(complaint).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(requestService.this, "Request Registered", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                Intent intent = new Intent(requestService.this,userMainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(requestService.this, "Try again later", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
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
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mapImp,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void locationSend(String address, String latitude, String longitude) {
        lati = latitude;
        longi = longitude;
        addressField = findViewById(R.id.address_et);
        addressField.setText(address);
    }
}