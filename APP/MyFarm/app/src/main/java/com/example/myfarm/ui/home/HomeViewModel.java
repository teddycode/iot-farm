package com.example.myfarm.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myfarm.data.Data;

public class HomeViewModel extends ViewModel {
    // 数据集
    private MutableLiveData<Data> rawData = new MutableLiveData<>();
    // 输入框记录
    private MutableLiveData<String> inIp = new MutableLiveData<>();
    private MutableLiveData<Integer> inPort = new MutableLiveData<>();
    private MutableLiveData<String> topic = new MutableLiveData<>();

    //设置默认值
    public HomeViewModel(){
        inIp.setValue("182.61.55.46");
        inPort.setValue(1883);
        topic.setValue("mqtt");

    }

    public LiveData<String> getIpText() {
        return inIp;
    }
    public void setIpText(String s){
        inIp.setValue(s);
    }

    public LiveData<String> getTopic() {
        return topic;
    }
    public void setTopic(String s){
        topic.setValue(s);
    }

    public LiveData<Integer> getPortText() {
        return inPort;
    }
    public void setPortText(Integer p){
        inPort.setValue(p);
    }

    public LiveData<Data> getRawData(){
        return rawData;
    }
    public void setRawData(Data rawData) {
        this.rawData.setValue(rawData);
    }
    public void postRawData(Data data){
        this.rawData.postValue(data);
    }
}