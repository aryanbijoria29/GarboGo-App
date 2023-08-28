package com.celes.garbogo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class signupUserActivity extends AppCompatActivity {
    EditText fname,emailEditText,passwordEditText,repasswordEditText, phoneNo;
    Button signup;
    TextView loginInstead;
    String emailPattern="^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_user);

        fname = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        repasswordEditText = findViewById(R.id.conPasswordEditText);
        signup = findViewById(R.id.buttonSignUp);
        loginInstead = findViewById(R.id.loginPg);
        phoneNo = findViewById(R.id.mobileNoEditText);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        loginInstead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(signupUserActivity.this, MainActivity.class));
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAuth();
            }
        });
    }

    private void toLoginPage() {
        startActivity(new Intent(signupUserActivity.this, MainActivity.class));
        finish();
    }

    private void performAuth() {
        String fullname = fname.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneNo.getText().toString();
        String password = passwordEditText.getText().toString();
        String repassword = repasswordEditText.getText().toString();

        if(!fullname.matches("[a-zA-Z]+\\s[a-zA-Z]+")){
            fname.setError("Please enter your Full Name");
            fname.requestFocus();
        }
        else if(!email.matches(emailPattern)){
            emailEditText.setError("Invalid Email");
            emailEditText.requestFocus();
        }
        else if(phone.isEmpty() || phone.length()<10){
            phoneNo.setError("Invale Phone No.");
            phoneNo.requestFocus();
        }
        else if(password.isEmpty() || password.length()<8){
            passwordEditText.setError("Password should be at least of 8 characters");
            passwordEditText.requestFocus();
        }
        else if(!repassword.equals(password)){
            repasswordEditText.setError("Password doesn't match");
            repasswordEditText.requestFocus();
        }
        else{
            progressDialog.setMessage("Please wait..");
            progressDialog.setTitle("Creating Account");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if(task.isSuccessful()){
                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(signupUserActivity.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(signupUserActivity.this, "Can't send verification email", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        User user = new User(fullname , email, phone);
                        FirebaseDatabase.getInstance().getReference("User")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            toLoginPage();
                                        }
                                        else{
                                            Toast.makeText(signupUserActivity.this, "Email Already Exists", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    else{
                        Toast.makeText(signupUserActivity.this, "Email Already Exists", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }
}