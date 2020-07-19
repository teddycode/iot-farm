package com.example.myfarm.ui.home;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.myfarm.MainActivity;
import com.example.myfarm.R;

import com.example.myfarm.data.Data;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.kongzue.dialog.v2.CustomDialog;
import com.kongzue.dialog.v2.TipDialog;
import com.kongzue.dialog.v2.WaitDialog;

public class HomeFragment extends Fragment {

    private MainActivity  This = null;

    private HomeViewModel homeViewModel;

    private Button btnConnServer = null;
    private Button btnLostServer = null;
    private Button btnSubscribe = null;
    private Button btnSetTh = null;
    private TextInputEditText inIp = null;
    private TextInputEditText inPort = null;
    private TextInputEditText inSub = null;

    private EditText edTemp;  private ProgressBar pbTemp;
    private EditText edHum;   private ProgressBar pbHum;
    private EditText edLight; private ProgressBar pbLight;
    private EditText edPress; private ProgressBar pbPress;
    private EditText edAlt;   private ProgressBar pbAlt;
    private EditText edSoil;  private ProgressBar pbSoil;
    private EditText edRain;  private ProgressBar pbRain;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        inIp = root.findViewById(R.id.inIP);
        inPort = root.findViewById(R.id.inPort);
        inSub = root.findViewById(R.id.inSub);
        btnConnServer = root.findViewById(R.id.btnConnSrv);
        btnLostServer = root.findViewById(R.id.btnLostSrv);
        btnSubscribe = root.findViewById(R.id.btnSub);
        btnSetTh = root.findViewById(R.id.btnTh);
        edTemp = root.findViewById(R.id.edTemp);     pbTemp = root.findViewById(R.id.progressBarTemp);
        edHum = root.findViewById(R.id.edWet);       pbHum = root.findViewById(R.id.progressBarWet);
        edLight = root.findViewById(R.id.edLight);   pbLight = root.findViewById(R.id.progressBarLight);
        edPress = root.findViewById(R.id.edPress);   pbPress = root.findViewById(R.id.progressBarPress);
        edAlt = root.findViewById(R.id.edAlt);       pbAlt = root.findViewById(R.id.progressBarAlt);
        edSoil = root.findViewById(R.id.edSoil);     pbSoil = root.findViewById(R.id.progressBarSoil);
        edRain = root.findViewById(R.id.edRain);     pbRain = root.findViewById(R.id.progressBarRain);

        InitUI();

        //设置ip 监听
        homeViewModel.getIpText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                inIp.setText(s);
            }
        });
        homeViewModel.getPortText().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                inPort.setText(integer.toString());
            }
        });

        homeViewModel.getTopic().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                inSub.setText(s);
            }
        });

        homeViewModel.getRawData().observe(getViewLifecycleOwner(), new Observer<Data>() {
            @Override
            public void onChanged(Data data) {
                LoadUI(data);
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        This = (MainActivity) getActivity();

        btnConnServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (This.mqttInstance==null) {
                    if (This.mqttInstance!=null && This.mqttInstance.isConnected() == true) {
                        Toast.makeText(This, "服务器已连接！", Toast.LENGTH_LONG).show();
                        return;
                    }
                    final String ip = inIp.getText().toString();
                    final int port;
                    if (inPort.getText().toString() != "")
                        port = Integer.parseInt(inPort.getText().toString());
                    else
                        port = 0;
                    if (!ip.isEmpty() && port != 0) {
                        String host = "tcp://" + ip + ":" + port;
                        This.initMQTT(host);
                        final ProgressDialog progressDialog = new ProgressDialog(This,
                                R.style.Theme_AppCompat_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("正在连接...");
                        progressDialog.show();
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                if (This.mqttInstance.isConnected() == true) {
                                    homeViewModel.setIpText(ip);
                                    homeViewModel.setPortText(port);
                                    Toast.makeText(This, "服务器连接成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(This, "服务器连接失败！", Toast.LENGTH_LONG).show();
                                }
                                progressDialog.dismiss();
                            }
                        }, 1000);
                    } else {
                        Toast.makeText(This, "输入参数有误！", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnLostServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (This.mqttInstance==null)
                {
                    Toast.makeText(This,"未连接服务器！",Toast.LENGTH_SHORT).show();
                    return;
                }

                if( This.mqttInstance.isConnected()==false)  // 断开连接
                {
                    Toast.makeText(This, "未连接服务器！", Toast.LENGTH_LONG).show();
                    return;
                }
                This.mqttInstance.disconnect();
                This.mqttInstance.release();
            }
        });

        btnSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (This.mqttInstance==null)
                {
                    Toast.makeText(This,"未连接服务器！",Toast.LENGTH_SHORT).show();
                    return;
                }
                String topic = inSub.getText().toString();
                if (topic.isEmpty()) return;
                homeViewModel.setTopic(topic);
                This.mqttInstance.subscribeMsg(topic,0);
                Toast.makeText(This,"订阅成功！",Toast.LENGTH_SHORT).show();
            }
        });

        btnSetTh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.show(This, R.layout.dialog_input, new CustomDialog.BindView() {
                    @Override
                    public void onBind(final CustomDialog dialog, View rootView) {
                        //绑定布局
                        final EditText edCode= rootView.findViewById(R.id.txt_dialog_code);
                        final EditText edAmoutn= rootView.findViewById(R.id.txt_dialog_password);
                        Button btnOk = rootView.findViewById(R.id.btn_selectPositive);      //确定
                        Button btnCancle = rootView.findViewById(R.id.btn_selectNegative);  //取消
                        final TextView title =rootView.findViewById(R.id.txt_dialog_title);
                        title.setText("设置阈值");
                        edCode.setHint("输入光强阈值（0-9999）");
                        edAmoutn.setHint("输入土壤阈值（0-4096）");
                        //绑定事件
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String code = edCode.getText().toString();
                                if(code.isEmpty()||edAmoutn.getText().toString().isEmpty()){
                                    TipDialog.show(This, "参数错误", TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_WARNING);
                                    dialog.doDismiss();
                                    return;
                                }
                                
                                WaitDialog.show(This, "正在设置...");
                                This.mqttInstance.publish("mqtt",String.format("*APP:G%4sS%4s#",edCode.getText().toString(),edAmoutn.getText().toString()),false,0);
                                WaitDialog.dismiss();

                            }

                        });
                        btnCancle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                TipDialog.show(This, "取消", TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_WARNING);
                                dialog.doDismiss();
                            }
                        });
                    }
                });
            }
        });
    }


    private void  InitUI()
    {
        // 设置范围
        pbTemp.setMax(130);  // -40~85
        pbHum.setMax(100); // 0~100
        pbLight.setMax(188000);  // 0~188000
        pbPress.setMax(110000); // 300 ~1100hpa
        pbAlt.setMax(2000); // haiba
        pbSoil.setMax(4096);
        pbRain.setMax(4096);
    }


    private void LoadUI(Data data)
    {
        edTemp.setText(data.getT()/100.0+"℃");
        edHum.setText((data.getH()/100.0)+"%");
        edLight.setText(data.getL()/100.0+"flux");
        edPress.setText(String.format("%.3fkpa",data.getP()/100000.0));
        edAlt .setText(data.getA()+"m");
        edSoil.setText(String.valueOf(data.getS()));
        edRain.setText(String.valueOf(data.getR()));

        pbTemp.setProgress(data.getT()/100);
        pbHum.setProgress(data.getH()/100);
        pbLight.setProgress(data.getL()/100);
        pbPress.setProgress(data.getP()/100);
        pbAlt.setProgress(data.getA());
        pbSoil.setProgress(data.getS());
        pbRain.setProgress(data.getR());
    }
}
