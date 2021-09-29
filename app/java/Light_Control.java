package com.example.smart_mode_lampes;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Light_Control extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.light_control);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.page_home:
                        Intent intent0 = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent0);
                        return true;
                    case R.id.page_control:
                        Intent intent = new Intent(getApplicationContext(), Light_Control.class);
                        startActivity(intent);
                        //getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentLight).commit();
                        return true;
                    case R.id.page_status:
                        Intent intent2 = new Intent(getApplicationContext(), Show_Status.class);
                        startActivity(intent2);
                        //getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentStatus).commit();
                        return true;
                }
                return false;
            }
        });
    }

}
