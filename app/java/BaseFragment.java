package com.example.smart_mode_lampes;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Collections;
import java.util.List;


public class BaseFragment extends Fragment {

    protected BluetoothAddressNameViewModel model;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model = new ViewModelProvider(requireActivity()).get(BluetoothAddressNameViewModel.class);
    }

    public List<Pair<String, String>> getBluetoothPairedDevices() {
        Context context = getContext();

        if (context instanceof BaseActivity) {
            return ((BaseActivity) context).getBluetoothPairedDevices();

        } else {
            return Collections.emptyList();
        }
    }

    public void startSearchDevice() {
        Context context = getContext();

        if (context instanceof BaseActivity) {
            ((BaseActivity) context).startSearchDevice();
        }
    }

    public String connect(Pair<String, String> addressName) {
        Context context = getContext();

        if (context instanceof BaseActivity) {
            return ((BaseActivity) context).connect(addressName);

        } else {
            return "Fragment 가 Activity 와 연결이 끊김.";
        }
    }

    @Nullable
    public ConnectedThread getConnectedThread() {
        Context context = getContext();

        if (context instanceof BaseActivity) {
            return ((BaseActivity) context).connectedThread;

        } else {
            return null;
        }
    }
}
