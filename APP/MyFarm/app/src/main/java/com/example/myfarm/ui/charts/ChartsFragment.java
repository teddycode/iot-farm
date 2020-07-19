package com.example.myfarm.ui.charts;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.myfarm.MainActivity;
import com.example.myfarm.R;
import com.example.myfarm.data.Data;
import com.example.myfarm.ui.home.HomeFragment;
import com.example.myfarm.ui.home.HomeViewModel;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.List;


public class ChartsFragment extends Fragment {

    private ChartsViewModel chartsViewModel;
    private HomeViewModel homeViewModel;

    private MainActivity This;

    private Spinner spSelector;
    private LineChart lineChart;
    private EditText edMax;
    private EditText edAver;
    private EditText edMin;

    private static final String names[] = {"温度","湿度","光强","气压","海拔","土壤","雨水"};
    private static final Integer[] limitArrar={90,100,1880,110000,2000,4096,4096}; // 温度、湿度、光强、气压、海拔、土壤、雨水
    private static final Integer[] miniArrar={-40,0,  0,     300,   -2000,0,0}; // 温度、湿度、光强、气压、海拔、土壤、雨水

    private static  DynamicLineChartManager myChart;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chart, container, false);

        chartsViewModel = ViewModelProviders.of(getActivity()).get(ChartsViewModel.class);
        homeViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);

        InitUI(root);

        chartsViewModel.getSpCurrentSelected().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer s) {
                spSelector.setSelection(s);
            }
        });

        chartsViewModel.getMax().observe(getViewLifecycleOwner(), new Observer<Integer[]>() {
            @Override
            public void onChanged(Integer[] integers) {
                edMax.setText(String.valueOf(integers[spSelector.getSelectedItemPosition()]));
            }
        });

        chartsViewModel.getMin().observe(getViewLifecycleOwner(), new Observer<Integer[]>() {
            @Override
            public void onChanged(Integer[] integers) {
                edMin.setText(String.valueOf(integers[spSelector.getSelectedItemPosition()]));
            }
        });

        chartsViewModel.getAver().observe(getViewLifecycleOwner(), new Observer<Integer[]>() {
            @Override
            public void onChanged(Integer[] integers) {
                edAver.setText(String.valueOf(integers[spSelector.getSelectedItemPosition()]));
            }
        });

        homeViewModel.getRawData().observe(getViewLifecycleOwner(), new Observer<Data>() {
            @Override
            public void onChanged(Data data) {
                UpdateChart(data);
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        This = (MainActivity) getActivity();

        spSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chartsViewModel.setSpCurrentSelected(position);
                try {
                    myChart = null;
                    myChart = new DynamicLineChartManager(lineChart, names[position], R.color.red);
                    myChart.setYAxis(limitArrar[position], miniArrar[position], 20);
                    myChart.setDescription(names[position]+"值");
                }catch (Exception e) {
                    Toast.makeText(This,"DrawTable ERROR："+e.toString(),Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(This,"未选择任何数据",Toast.LENGTH_SHORT);
            }
        });
    }

    private  void InitUI(View root){
        spSelector = root.findViewById(R.id.spSelector);
        lineChart = root.findViewById(R.id.charts);
        edAver = root.findViewById(R.id.edAver);
        edMax = root.findViewById(R.id.edMax);
        edMin  = root.findViewById(R.id.edMin);
        myChart = new DynamicLineChartManager(lineChart, names[0], R.color.gray);
        myChart.setYAxis(limitArrar[0], miniArrar[0], 100);
    }

    private  void UpdateChart(Data data)
    {
        if(myChart!=null) {
            int value;
            switch (spSelector.getSelectedItemPosition()) {
                case 0:
                    value = data.getT() / 100;
                    break;
                case 1:
                    value = data.getH() / 100;
                    break;
                case 2:
                    value = data.getL() / 100;
                    break;
                case 3:
                    value = data.getP() / 100;
                    break;
                case 4:
                    value = data.getA();
                    break;
                case 5:
                    value = data.getS();
                    break;
                case 6:
                    value = data.getR();
                    break;
                default:
                    value = 100;
            }
            chartsViewModel.setMax(spSelector.getSelectedItemPosition(),value);
            chartsViewModel.setMin(spSelector.getSelectedItemPosition(),value);
            chartsViewModel.setAver(spSelector.getSelectedItemPosition(),value);
            myChart.addEntry(value);
        }
    }

}

