package com.example.smart_mode_lampes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Show_Status extends AppCompatActivity {
    ConnectedThread connectedThread;
    public static Activity thirdActivity;
    TextView textView;

    private BackPressHandler backPressHandler = new BackPressHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_status);

        textView = findViewById(R.id.textView);

        SeekBar seekBar = findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textView.setText(String.format("현재 선택된 밝기는 %d 입니다.",seekBar
                .getProgress()));
            }
        });

        thirdActivity = Show_Status.this;

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
                        Intent intent = new Intent(getApplicationContext(), Light_Control.class);
                        startActivity(intent);
                        finish();
                        return true;

                    case R.id.page_status:
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
