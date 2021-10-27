package com.example.smart_mode_lampes;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class BaseActivity extends AppCompatActivity {

    //region Bluetooth variable
    final static int REQUEST_ENABLE_BT = 1;
    final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> btPairedDevices;

    ArrayList<Pair<String, String>> btDeviceAddressNameList = new ArrayList<>();

    BluetoothSocket btSocket = null;
    ConnectedThread connectedThread;

    private BluetoothAddressNameViewModel model;
    //endregion

    private BackPressHandler backPressHandler;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                btDeviceAddressNameList.add(new Pair<>(deviceHardwareAddress, deviceName));
                model.setAddressNameList(btDeviceAddressNameList);
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        backPressHandler = new BackPressHandler(this);
        model = new ViewModelProvider(this).get(BluetoothAddressNameViewModel.class);
        model.setAddressNameList(btDeviceAddressNameList);
        model.setStatus(0);
    }

    @Override
    public void onBackPressed() {
        backPressHandler.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(receiver);

        } catch (Exception ignore) {
        }

        super.onDestroy();
    }

    public void activeBluetooth() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!btAdapter.isEnabled()) {
            startBluetoothEnableRequest();
        }
    }

    private void startBluetoothEnableRequest() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    private void checkBluetoothAdapterValidation() {
        if (btAdapter == null) {
            activeBluetooth();

        } else if (!btAdapter.isEnabled()) {
            startBluetoothEnableRequest();
        }
    }

    public List<Pair<String, String>> getBluetoothPairedDevices() {
        checkBluetoothAdapterValidation();

        btPairedDevices = btAdapter.getBondedDevices();

        ArrayList<Pair<String, String>> btPairedDeviceAddressNameList = new ArrayList<>();

        if (btPairedDevices.size() > 0) {
            //기존에 페어링된 기기가 있으면. 페어링된 각 장치의 이름과 주소를 가져옴.
            for (BluetoothDevice device : btPairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                btPairedDeviceAddressNameList.add(new Pair<>(deviceHardwareAddress, deviceName));
            }
        }

        return btPairedDeviceAddressNameList;
    }

    public void startSearchDevice() {
        checkBluetoothAdapterValidation();

        if (btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();

        } else {
            if (btAdapter.isEnabled()) {
                btAdapter.startDiscovery();

                btDeviceAddressNameList.clear();
                model.setAddressNameList(btDeviceAddressNameList);

                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, filter);

            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String connect(Pair<String, String> addressName) {
        checkBluetoothAdapterValidation();

        String deviceAddress = addressName.first;
        String deviceName = addressName.second == null ? "Unknown " + addressName.first : addressName.second;

        BluetoothDevice device = btAdapter.getRemoteDevice(deviceAddress);

        // 그 기기의 소켓 생성 및 연결 시도
        try {
            btSocket = createBluetoothSocket(device);
            btSocket.connect();

        } catch (IOException e) {
            e.printStackTrace();
            return "connection failed!";
        }

        // 블루투스 통신 시작
        connectedThread = new ConnectedThread(btSocket);
        connectedThread.start();

        return "connected to " + deviceName;
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("BaseActivity", "Could not create Insecure RFComm Connection", e);
        }

        return device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }
}
