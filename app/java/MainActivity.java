package com.example.smart_mode_lampes;

import android.Manifest;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new ViewPagerAdapter(this));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.page_home:
                        viewPager.setCurrentItem(0, false);
                        break;

                    case R.id.page_control:
                        viewPager.setCurrentItem(1, false);
                        break;

                    case R.id.page_status:
                        viewPager.setCurrentItem(2, false);
                        break;
                }
                return true;
            }
        });

        //위치권한 허용 코드
        String[] permission_list = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions(MainActivity.this, permission_list, 1);

        //블루투스 활성화
        activeBluetooth();
    }


    private static class ViewPagerAdapter extends FragmentStateAdapter {

        HomeFragment homeFragment;
        LightControlFragment lightControlFragment;
        ShowStatusFragment showStatusFragment;


        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    if (homeFragment == null) {
                        homeFragment = new HomeFragment();
                    }

                    return homeFragment;

                case 1:
                    if (lightControlFragment == null) {
                        lightControlFragment = new LightControlFragment();
                    }

                    return lightControlFragment;

                default:
                    if (showStatusFragment == null) {
                        showStatusFragment = new ShowStatusFragment();
                    }

                    return showStatusFragment;
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}

/*
package com.example.smart_mode_lampes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static Activity firstActivity;

    //블루투스
    String TAG = "MainActivity";
    UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    int status = 0;
    TextView textStatus;
    Button btnParied, btnSearch, btncontrol;
    ListView listView;

    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> pairedDevices;
    ArrayAdapter<String> btArrayAdapter;
    ArrayList<String> deviceAddressArray;

    private final static int REQUEST_ENABLE_BT = 1;
    BluetoothSocket btSocket = null;
    ConnectedThread connectedThread;

    private BackPressHandler backPressHandler = new BackPressHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstActivity = MainActivity.this;

        //getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentLight).commit();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.page_home:
                        return true;

                    case R.id.page_control:
                        Intent intent = new Intent(getApplicationContext(), Light_Control.class);
                        startActivity(intent);
                        finish();
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

        //위치권한 허용 코드
        String[] permission_list = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions(MainActivity.this, permission_list, 1);

        //블루투스 활성화
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // variablesS
        textStatus = (TextView) findViewById(R.id.text_status);
        btnParied = (Button) findViewById(R.id.btn_paired);
        btnSearch = (Button) findViewById(R.id.btn_search);
        btncontrol = (Button) findViewById(R.id.btn_control);
        listView = (ListView) findViewById(R.id.listview);

        //페어링된 디바이스 보여주기
        btArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        deviceAddressArray = new ArrayList<>();
        listView.setAdapter(btArrayAdapter);

        listView.setOnItemClickListener(new myOnItemClickListener());
    }

    public void onClickButtonPaired(View view){
        btArrayAdapter.clear();
        if(deviceAddressArray!=null && !deviceAddressArray.isEmpty()){ deviceAddressArray.clear(); }
        pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            //기존에 페어링된 기기가 있으면. 페어링된 각 장치의 이름과 주소를 가져옴.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if(deviceName != null){ btArrayAdapter.add(deviceName); }
                deviceAddressArray.add(deviceHardwareAddress);
            }
        }
    }

    public void onClickButtonSearch(View view){
        // 장치가 이미 검색중 인 장치인지 확인
        if(btAdapter.isDiscovering()){
            btAdapter.cancelDiscovery();
        } else {
            if (btAdapter.isEnabled()) {
                btAdapter.startDiscovery();
                btArrayAdapter.clear();
                if (deviceAddressArray != null && !deviceAddressArray.isEmpty()) {
                    deviceAddressArray.clear();
                }
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, filter);

            } else {
                Toast.makeText(getApplicationContext(), "bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }



    //문자 보내기 "자동 조절 중입니다."/"자동 조절을 중지합니다."
    public void onClickButtonSend(View view){
        if(connectedThread!=null && status != 1){
            connectedThread.write("o");
            Toast.makeText(getApplicationContext(), "자동 조절 중입니다.", Toast.LENGTH_SHORT).show();
            status = 1;
        }
        else if(connectedThread!=null && status != 0){
            connectedThread.write("c");
            Toast.makeText(getApplicationContext(), "자동 조절을 중지합니다.", Toast.LENGTH_SHORT).show();
            status = 0;
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if(deviceName != null){ btArrayAdapter.add(deviceName); }
                deviceAddressArray.add(deviceHardwareAddress);
                btArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(receiver);
        }catch (IllegalArgumentException e){
        }catch (Exception e){
        }finally {
        }
            // Don't forget to unregister the ACTION_FOUND receiver.

    }

    public class myOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getApplicationContext(), btArrayAdapter.getItem(position), Toast.LENGTH_SHORT).show();

            textStatus.setText("try...");
            //선택된 기기의 이름과 주소를 가져옴
            final String name = btArrayAdapter.getItem(position); // get name
            final String address = deviceAddressArray.get(position); // get address
            boolean flag = true;
            // 가져온 주소로 BluetoothDevice를 만듬
            BluetoothDevice device = btAdapter.getRemoteDevice(address);

            // 그 기기의 소켓 생성 및 연결 시도
            try {
                btSocket = createBluetoothSocket(device);
                btSocket.connect();
            } catch (IOException e) {
                flag = false;
                textStatus.setText("connection failed!");
                e.printStackTrace();
            }

            // 블루투스 통신 시작
            if(flag){
                textStatus.setText("connected to "+name);
                connectedThread = new ConnectedThread(btSocket);
                connectedThread.start();
            }

        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }

    @Override
    public void onBackPressed() {
        // Default
        backPressHandler.onBackPressed();
    }
}
*/

////////////////////////////////////////////////////
/*package com.example.smart_mode_lampes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    String TAG = "MainActivity";
    UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    int status = 0;
    TextView textStatus;
    Button btnParied, btnSearch, btnSend;
    ListView listView;

    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> pairedDevices;
    ArrayAdapter<String> btArrayAdapter;
    ArrayList<String> deviceAddressArray;

    private final static int REQUEST_ENABLE_BT = 1;
    BluetoothSocket btSocket = null;
    ConnectedThread connectedThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //위치권한 허용 코드
        String[] permission_list = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions(MainActivity.this, permission_list, 1);

        //블루투스 활성화
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // variablesS
        textStatus = (TextView) findViewById(R.id.text_status);
        btnParied = (Button) findViewById(R.id.btn_paired);
        btnSearch = (Button) findViewById(R.id.btn_search);
        btnSend = (Button) findViewById(R.id.btn_send);
        listView = (ListView) findViewById(R.id.listview);

        //페어링된 디바이스 보여주기
        btArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        deviceAddressArray = new ArrayList<>();
        listView.setAdapter(btArrayAdapter);

        listView.setOnItemClickListener(new myOnItemClickListener());
    }

    public void onClickButtonPaired(View view){
        btArrayAdapter.clear();
        if(deviceAddressArray!=null && !deviceAddressArray.isEmpty()){ deviceAddressArray.clear(); }
        pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            //기존에 페어링된 기기가 있으면. 페어링된 각 장치의 이름과 주소를 가져옴.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if(deviceName != null){ btArrayAdapter.add(deviceName); }
                deviceAddressArray.add(deviceHardwareAddress);
            }
        }
    }

    public void onClickButtonSearch(View view){
        // 장치가 이미 검색중 인 장치인지 확인
        if(btAdapter.isDiscovering()){
            btAdapter.cancelDiscovery();
        } else {
            if (btAdapter.isEnabled()) {
                btAdapter.startDiscovery();
                btArrayAdapter.clear();
                if (deviceAddressArray != null && !deviceAddressArray.isEmpty()) {
                    deviceAddressArray.clear();
                }
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, filter);

            } else {
                Toast.makeText(getApplicationContext(), "bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //문자 보내기 "문이 닫혔습니다."/"문이 열렸습니다."
    public void onClickButtonSend(View view){
        if(connectedThread!=null && status != 1){
            connectedThread.write("c");
            Toast.makeText(getApplicationContext(), "문이 닫혔습니다.", Toast.LENGTH_SHORT).show();
            status = 1;
        }
        else if(connectedThread!=null && status != 0){
            connectedThread.write("o");
            Toast.makeText(getApplicationContext(), "문이 열렸습니다.", Toast.LENGTH_SHORT).show();
            status = 0;
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if(deviceName != null){ btArrayAdapter.add(deviceName); }
                deviceAddressArray.add(deviceHardwareAddress);
                btArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }

    public class myOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getApplicationContext(), btArrayAdapter.getItem(position), Toast.LENGTH_SHORT).show();

            textStatus.setText("try...");
            //선택된 기기의 이름과 주소를 가져옴
            final String name = btArrayAdapter.getItem(position); // get name
            final String address = deviceAddressArray.get(position); // get address
            boolean flag = true;
            // 가져온 주소로 BluetoothDevice를 만듬
            BluetoothDevice device = btAdapter.getRemoteDevice(address);

            // 그 기기의 소켓 생성 및 연결 시도
            try {
                btSocket = createBluetoothSocket(device);
                btSocket.connect();
            } catch (IOException e) {
                flag = false;
                textStatus.setText("connection failed!");
                e.printStackTrace();
            }

            // 블루투스 통신 시작
            if(flag){
                textStatus.setText("connected to "+name);
                connectedThread = new ConnectedThread(btSocket);
                connectedThread.start();
            }

        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }
}
*/
