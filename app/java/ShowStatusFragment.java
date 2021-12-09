package com.example.smart_mode_lampes;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ShowStatusFragment extends BaseFragment implements SensorEventListener{//implements SensorEventListener {

    TextView textView;
    //TextView textView2;
    ///조도
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private String light = "";
    ///
    private int imageIndex;
    private ImageView imageView1, imageView2;

    Toast mToast;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_status, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView1 = view.findViewById(R.id.afternoon);
        imageView2 = view.findViewById(R.id.night);
        imageIndex = 0;

        view.findViewById(R.id.setting).setOnClickListener(this::onClickButtonSend4);
        //view.findViewById(R.id.check).setOnClickListener(this::onClickButtonSend5);

        textView = view.findViewById(R.id.textView);
        //textView2 = view.findViewById(R.id.textView2);

        SeekBar seekBar = view.findViewById(R.id.seekBar);

        ///조도센서
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if( lightSensor == null )
            Toast.makeText(requireActivity(), "No Light Sensor Found!", Toast.LENGTH_SHORT).show();
        ///
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textView.setText(String.format("현재 선택된 밝기는 %d 입니다.", seekBar
                        .getProgress()));
            }
        });
    }

    ///조도
    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    ///
    public void onClickButtonSend4(View view) {
        ConnectedThread connectedThread = getConnectedThread();
        if (connectedThread != null && model.getStatus().getValue() != 1) {
            if( mToast != null ) { mToast.cancel(); }
            connectedThread.write("cb");
            mToast.makeText(requireActivity(), "해당밝기로 설정합니다.", Toast.LENGTH_SHORT).show();
            model.setStatus(1);

        } else if (connectedThread != null && model.getStatus().getValue() != 0) {
            if( mToast != null ) { mToast.cancel(); }
            connectedThread.write("cbc");
            mToast.makeText(requireActivity(), "해당밝기 설정을 중지합니다.", Toast.LENGTH_SHORT).show();
            model.setStatus(0);
        }
    }

    public void onClickButtonSend1(View view) {
        ConnectedThread connectedThread = getConnectedThread();
        if(connectedThread != null){
            imageView1.setVisibility(View.VISIBLE);
            imageView2.setVisibility(View.INVISIBLE);
            model.setStatus(1);

        } else{
            imageView1.setVisibility(View.INVISIBLE);
            imageView2.setVisibility(View.VISIBLE);
            model.setStatus(0);
        }
    }
    /*
    public void onClickButtonSend5(View view) {
        ConnectedThread connectedThread = getConnectedThread();

        if (connectedThread != null && model.getStatus().getValue() != 1) {
            if( mToast != null ) { mToast.cancel(); }
            connectedThread.write(light);
            textView2.setText(String.format("현재 주위의 밝기는 %d 입니다.", light));
        }

        else if (connectedThread != null && model.getStatus().getValue() != 0) {
            if( mToast != null ) { mToast.cancel(); }
            textView2.setText(String.format("주위 밝기 측정을 중단합니다.", light));
        }
    }*/
        ////조도
    @Override
    public void onSensorChanged(SensorEvent event) {
        if( event.sensor.getType() == Sensor.TYPE_LIGHT){
            light = String.valueOf(event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    /////
}




/*
package com.example.smart_mode_lampes;
//사용자 밝기 설정
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
*/
