package com.celes.garbogo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    EditText loginID,password;
    TextView signUp,forgotPassword, fyp, noAcc, emailTextView;
    Button login;
    ProgressDialog progressDialog;
    String emailPattern="^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("workerLogin",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String temp = sharedPreferences.getString("userName", "default :(");
        if(sharedPreferences.getString("isLogin", "no").equals("yes")){
            toWorkerActivityIntent(temp);
        }

        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.loginType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String spinnerVal = parent.getItemAtPosition(position).toString();

                login = findViewById(R.id.buttonLogin);
                signUp = findViewById(R.id.signup);
                forgotPassword = findViewById(R.id.forgotPass);
                loginID = findViewById(R.id.emailEditText);
                password = findViewById(R.id.passwordEditText);
                fyp = findViewById(R.id.fypTextView);
                noAcc = findViewById(R.id.noAcc);
                emailTextView = findViewById(R.id.emailTextView);
                progressDialog = new ProgressDialog(MainActivity.this);
                mAuth = FirebaseAuth.getInstance();

                switch (spinnerVal) {
                    case "User":
                        fyp.setText(R.string.passwordFor);
                        signUp.setText(R.string.create_account); //btn
                        forgotPassword.setText(R.string.forgotPassword); //btn
                        noAcc.setText(R.string.don_t_have_and_account);
                        emailTextView.setText(R.string.email);
                        loginID.setHint(R.string.emailHint);
                        signUp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                signUpIntent();
                            }
                        });
                        forgotPassword.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                forgotPasswordDialog();
                            }
                        });
                        login.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                toUserActivity();
                            }
                        });
                        break;
                    case "Worker":
                        fyp.setText("");
                        signUp.setText("");
                        forgotPassword.setText("");
                        noAcc.setText("");
                        emailTextView.setText(R.string.username);
                        loginID.setHint(R.string.usernameHint);
                        login.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                toWorkerActivity();
                            }
                        });
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void toWorkerActivityIntent(String userName) {
        Intent intent = new Intent(MainActivity.this,workerMainActivity.class);
        intent.putExtra("userName",userName);
        startActivity(intent);
        finish();
    }

    private void toWorkerActivity() {
        String username = loginID.getText().toString().trim();
        String pass = password.getText().toString();

        if(username.isEmpty() || username.contains(".") || username.contains("#") || username.contains("$")
                || username.contains("[")  || username.contains("]")){
            loginID.setError("Please enter a valid username");
            loginID.requestFocus();
        } else if (pass.isEmpty()) {
            password.setError("Enter a valid password");
            password.requestFocus();
        } else {
            databaseReference = FirebaseDatabase.getInstance().getReference("Worker");
            databaseReference.child(username).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        Worker worker = snapshot.getValue(Worker.class);
                        String passCheck = worker.password;
                        if(passCheck.equals(pass)){
                            editor.putString("isLogin","yes");
                            editor.putString("userName",worker.username);
                            editor.commit();
                            toWorkerActivityIntent(worker.username);
                        } else {
                            Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void toUserActivity() {
        String email = loginID.getText().toString().trim();
        String pass = password.getText().toString();

        if(!email.matches(emailPattern)){
            loginID.setError("Invalid Email");
            loginID.requestFocus();
        }
        else if(pass.isEmpty() || pass.length()<8){
            password.setError("Enter a valid password");
            password.requestFocus();
        }
        else{
            progressDialog.setMessage("Please wait..");
            progressDialog.setTitle("Logging In");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if(task.isSuccessful()){
                        if(mAuth.getCurrentUser().isEmailVerified()){
                            Toast.makeText(MainActivity.this, "Successful Login", Toast.LENGTH_SHORT).show();
                            toUserMain();
                        }
                        else{
                            loginID.setError("Please verify your email");
                            loginID.requestFocus();
                            fyp.setText(R.string.not_received_email);
                            forgotPassword.setText(R.string.resend);
                            forgotPassword.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(MainActivity.this, "Verification Email Sent", Toast.LENGTH_LONG).show();
                                            }
                                            else{
                                                Toast.makeText(MainActivity.this, "Can't send verification email", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void toUserMain() {
        Intent intent = new Intent(MainActivity.this,userMainActivity.class);
        startActivity(intent);
        finish();
    }

    private void signUpIntent() {
        Intent intent = new Intent(this,signupUserActivity.class);
        startActivity(intent);
    }

    private void forgotPasswordDialog() {
        EditText resetMail = new EditText(this);
        resetMail.setHint("Registered email id");
        AlertDialog.Builder passwordresetDialog = new AlertDialog.Builder(this);
        passwordresetDialog.setTitle("Reset Password?");
        passwordresetDialog.setMessage("Enter your registered email");
        passwordresetDialog.setView(resetMail);
        passwordresetDialog.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mail=resetMail.getText().toString();
                if(!(mail.matches(emailPattern))){
                    resetMail.setError("Enter a valid email");
                    resetMail.requestFocus();
                }
                mAuth.sendPasswordResetEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Try Again after some time", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        passwordresetDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        passwordresetDialog.create().show();
    }
}