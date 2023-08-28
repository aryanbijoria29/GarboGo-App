package com.celes.garbogo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //multithreading for splash screen time limit
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

                if(mUser!=null && mUser.isEmailVerified()){
                    Intent intent = new Intent(SplashScreen.this, userMainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                    startActivity(intent);
                    finish(); //pop activity from stack
                }
            }
        },750);


    }
}