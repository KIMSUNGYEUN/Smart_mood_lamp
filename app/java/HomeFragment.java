package com.example.smart_mode_lampes;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private TextView textStatus;
    private Button btnParied, btnSearch, btncontrol;
    private ListView listView;

    private ArrayAdapter<String> btArrayAdapter;
    private List<Pair<String, String>> btAddressNameList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model.getAddressNameList().observe(getViewLifecycleOwner(), value -> {
            btAddressNameList = value;

            btArrayAdapter.clear();

            ArrayList<String> deviceNames = new ArrayList<>();

            for (Pair<String, String> addressName : value) {
                String name = addressName.second == null ? "Unknown " + addressName.first : addressName.second;
                deviceNames.add(name);
            }

            btArrayAdapter.addAll(deviceNames);
            btArrayAdapter.notifyDataSetChanged();
        });

        textStatus = (TextView) view.findViewById(R.id.text_status);

        btnParied = (Button) view.findViewById(R.id.btn_paired);
        btnParied.setOnClickListener(this::onClickButtonPaired);

        btnSearch = (Button) view.findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(this::onClickButtonSearch);

        btncontrol = (Button) view.findViewById(R.id.btn_control);
        btncontrol.setOnClickListener(this::onClickButtonSend);

        listView = (ListView) view.findViewById(R.id.listview);

        //페어링된 디바이스 보여주기
        btArrayAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1);
        listView.setAdapter(btArrayAdapter);
        listView.setOnItemClickListener(this);
    }

    public void onClickButtonPaired(View view) {
        btAddressNameList = getBluetoothPairedDevices();

        btArrayAdapter.clear();

        ArrayList<String> deviceNames = new ArrayList<>();

        for (Pair<String, String> addressName : btAddressNameList) {
            String name = addressName.second == null ? "Unknown " + addressName.first : addressName.second;
            deviceNames.add(name);
        }

        btArrayAdapter.addAll(deviceNames);
        btArrayAdapter.notifyDataSetChanged();
    }

    public void onClickButtonSearch(View view) {
        startSearchDevice();
    }

    public void onClickButtonSend(View view) {
        ConnectedThread connectedThread = getConnectedThread();

        if (connectedThread != null && model.getStatus().getValue() != 1) {
            connectedThread.write("o");
            Toast.makeText(requireActivity(), "자동 조절 중입니다.", Toast.LENGTH_SHORT).show();
            model.setStatus(1);

        } else if (connectedThread != null && model.getStatus().getValue() != 0) {
            connectedThread.write("c");
            Toast.makeText(requireActivity(), "자동 조절을 중지합니다.", Toast.LENGTH_SHORT).show();
            model.setStatus(0);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(requireActivity(), btArrayAdapter.getItem(position), Toast.LENGTH_SHORT).show();

        textStatus.setText("try...");

        //선택된 기기의 이름과 주소를 가져옴
        textStatus.setText(connect(btAddressNameList.get(position)));
    }
}
