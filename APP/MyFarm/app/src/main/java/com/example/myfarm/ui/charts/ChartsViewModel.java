package com.example.myfarm.ui.charts;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myfarm.data.Data;

import java.lang.reflect.Array;

public class ChartsViewModel extends ViewModel {

    private MutableLiveData<Integer> spCurrentSelected = new MutableLiveData<>();
    private MutableLiveData<Integer[]> max = new MutableLiveData<>();
    private MutableLiveData<Integer[]> min  = new MutableLiveData<>();
    private MutableLiveData<Integer[]> aver = new MutableLiveData<>();

    public ChartsViewModel(){
        Integer[] maxs = {90,100,1880,110000,2000,4096,4096};
        Integer[] avers = {0,0,0,0,0,0,0};
        Integer[] mins = {-40,0,  0,     300,   -2000,0,0};
        max.setValue(mins);
        min.setValue(maxs);
        aver.setValue(avers);
    }

    public MutableLiveData<Integer> getSpCurrentSelected(){
        return  spCurrentSelected;
    }
    public void setSpCurrentSelected(Integer s){
        spCurrentSelected.setValue(s);
    }

    public MutableLiveData<Integer[]> getMax() {return  max;}

    public void setMax(int pos,Integer max) {
        Integer[] ints = this.max.getValue();
        if (max > ints[pos]) {
            ints[pos] = max;
            this.max.setValue(ints);
        }
    }

    public MutableLiveData<Integer[]> getAver() {
        return aver;
    }

    public void setAver(int pos, Integer aver) {
        Integer[] ints = this.aver.getValue();
        ints[pos] = (aver+ints[pos])/2;
        this.aver.setValue(ints);
    }

    public MutableLiveData<Integer[]> getMin() {
        return min;
    }

    public void setMin(int pos,Integer min) {
        Integer[] ints = this.min.getValue();
        if (min < ints[pos]) {
            ints[pos] = min;
            this.min.setValue(ints);
        }
    }

}
