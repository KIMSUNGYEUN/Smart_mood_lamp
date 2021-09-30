package com.example.smart_mode_lampes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Light_Control extends AppCompatActivity {
    public static Activity secondActivity;

    private BackPressHandler backPressHandler = new BackPressHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        secondActivity = Light_Control.this;

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
                        finish();
                        return true;

                    case R.id.page_control:
                        return true;

                    case R.id.page_status:
                        Intent intent2 = new Intent(getApplicationContext(), Show_Status.class);
                        startActivity(intent2);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Default
        backPressHandler.onBackPressed();
    }

}
