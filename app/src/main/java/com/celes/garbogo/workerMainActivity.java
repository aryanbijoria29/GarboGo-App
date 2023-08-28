package com.celes.garbogo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class workerMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    //String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_main);

        drawerLayout = findViewById(R.id.drawerLayoutWorker);
        navigationView = findViewById(R.id.navigationViewWorker);
        toolbar = findViewById(R.id.toolBarWorker);

        setSupportActionBar(toolbar);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.navOpen,R.string.navClose);

        drawerLayout.addDrawerListener(toggle);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        toggle.syncState();

        if(savedInstanceState==null){
            replaceFragement(new workerHomeFragment());
            navigationView.setCheckedItem(R.id.home);
        }

        sharedPreferences = getSharedPreferences("workerLogin",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if(sharedPreferences.getString("isLogin","no").equals("no")){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
        switch(item.getItemId()){
            case R.id.home:
                replaceFragement(new workerHomeFragment());
                return true;
            case R.id.profile:
                replaceFragement(new workerProfileFragment());
                return true;
            case R.id.logoutMenu:
                Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(workerMainActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        return false;
    }
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else{
            //super.onBackPressed();
            finishAffinity();
        }
    }
    private void replaceFragement(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_worker,fragment);
        fragmentTransaction.commit();
    }
}