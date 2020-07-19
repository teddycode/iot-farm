package com.example.myfarm;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.myfarm.data.Data;
import com.example.myfarm.socket.MQTTManager;
import com.example.myfarm.socket.MessageHandlerCallBack;
import com.example.myfarm.ui.charts.ChartsViewModel;
import com.example.myfarm.ui.home.HomeViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private static final String TAG="MainActivity";

    public static Context context;

    public MQTTManager mqttInstance;

    public boolean mIsConnected=false;

    private HomeViewModel homeViewModel;
    private ChartsViewModel chartsViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 配置导航栏
        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
    }

    public  void initMQTT(String server)
    {
        mqttInstance = MQTTManager.getInstance(context,server);
        mqttInstance.setMessageHandlerCallBack(new MessageHandlerCallBack() {
            @Override
            public void messageSuccess(String topicName, String s) {
                Log.e(TAG, "messageSuccess: " + s);
                processMessages(s);
            }
        });
        mqttInstance.connect();
    }

    //解析
    private void processMessages(String msg){
        try {
            Gson gson = new Gson();
            Data data = gson.fromJson(msg,Data.class);
            homeViewModel.postRawData(data);//
        }catch (Exception e){
            System.out.println("接收数据出错："+e.toString());
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

}
