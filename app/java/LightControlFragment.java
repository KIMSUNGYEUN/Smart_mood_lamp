package com.example.smart_mode_lampes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LightControlFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_light_control, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_control1).setOnClickListener(this::onClickButtonSend1);
        view.findViewById(R.id.btn_control2).setOnClickListener(this::onClickButtonSend2);
        view.findViewById(R.id.btn_control3).setOnClickListener(this::onClickButtonSend3);
    }

    public void onClickButtonSend1(View view) {
        ConnectedThread connectedThread = getConnectedThread();

        if (connectedThread != null && model.getStatus().getValue() != 1) {
            connectedThread.write("m");
            Toast.makeText(requireActivity(), "캠핑모드로 설정합니다.", Toast.LENGTH_SHORT).show();
            model.setStatus(1);

        } else if (connectedThread != null && model.getStatus().getValue() != 0) {
            connectedThread.write("mc");
            Toast.makeText(requireActivity(), "캠핑모드를 중지합니다.", Toast.LENGTH_SHORT).show();
            model.setStatus(0);
        }
    }

    public void onClickButtonSend2(View view) {
        ConnectedThread connectedThread = getConnectedThread();

        if (connectedThread != null && model.getStatus().getValue() != 1) {
            connectedThread.write("s");
            Toast.makeText(requireActivity(), "수면모드로 설정합니다.", Toast.LENGTH_SHORT).show();
            model.setStatus(1);

        } else if (connectedThread != null && model.getStatus().getValue() != 0) {
            connectedThread.write("sc");
            Toast.makeText(requireActivity(), "수면모드를 중지합니다.", Toast.LENGTH_SHORT).show();
            model.setStatus(0);
        }
    }

    public void onClickButtonSend3(View view) {
        ConnectedThread connectedThread = getConnectedThread();

        if (connectedThread != null && model.getStatus().getValue() != 1) {
            connectedThread.write("r");
            Toast.makeText(requireActivity(), "독서모드로 설정합니다.", Toast.LENGTH_SHORT).show();
            model.setStatus(1);

        } else if (connectedThread != null && model.getStatus().getValue() != 0) {
            connectedThread.write("rc");
            Toast.makeText(requireActivity(), "독서모드를 중지합니다.", Toast.LENGTH_SHORT).show();
            model.setStatus(0);
        }
    }
}



/*
package com.example.smart_mode_lampes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Light_Control extends AppCompatActivity {
    int status = 0;
    ConnectedThread connectedThread;
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

    public void onClickButtonSend1(View view){
        if(connectedThread!=null && status != 1){
            connectedThread.write("m");
            Toast.makeText(getApplicationContext(), "캠핑모드로 설정합니다.", Toast.LENGTH_SHORT).show();
            status = 1;
        }
        else if(connectedThread!=null && status != 0){
            connectedThread.write("c");
            Toast.makeText(getApplicationContext(), "캠핑모드를 중지합니다.", Toast.LENGTH_SHORT).show();
            status = 0;
        }
    }

    public void onClickButtonSend2(View view){
        if(connectedThread!=null && status != 1){
            connectedThread.write("s");
            Toast.makeText(getApplicationContext(), "수면모드로 설정합니다.", Toast.LENGTH_SHORT).show();
            status = 1;
        }
        else if(connectedThread!=null && status != 0){
            connectedThread.write("c");
            Toast.makeText(getApplicationContext(), "수면모드를 중지합니다.", Toast.LENGTH_SHORT).show();
            status = 0;
        }
    }

    public void onClickButtonSend3(View view){
        if(connectedThread!=null && status != 1){
            connectedThread.write("r");
            Toast.makeText(getApplicationContext(), "독서모드로 설정합니다.", Toast.LENGTH_SHORT).show();
            status = 1;
        }
        else if(connectedThread!=null && status != 0){
            connectedThread.write("c");
            Toast.makeText(getApplicationContext(), "독서모드를 중지합니다.", Toast.LENGTH_SHORT).show();
            status = 0;
        }
    }

}
*/
