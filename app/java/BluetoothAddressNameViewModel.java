package com.example.smart_mode_lampes;


import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;


public class BluetoothAddressNameViewModel extends ViewModel {
    private MutableLiveData<List<Pair<String, String>>> addressNameList = new MutableLiveData<>();
    private MutableLiveData<Integer> status = new MutableLiveData<>();

    public LiveData<List<Pair<String, String>>> getAddressNameList() {
        return addressNameList;
    }

    public void setAddressNameList(List<Pair<String, String>> addressNameList) {
        this.addressNameList.setValue(addressNameList);
    }

    public LiveData<Integer> getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status.setValue(status);
    }
}
