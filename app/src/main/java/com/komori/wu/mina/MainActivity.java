package com.komori.wu.mina;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.InetSocketAddress;

import com.komori.wu.mina.event.FirstEvent;
import com.komori.wu.mina.mina.MinaClientHanlder;
import com.komori.wu.mina.mina.MinaService;

public class MainActivity extends AppCompatActivity {
    public static final String IP = "182.61.53.117";
    public static final int PORT = 22001;
    private TextView tv;
    CameraManager manager;
    private int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        tv = (TextView) findViewById(R.id.tv);
        Intent intent = new Intent(this, MinaService.class);
        startService(intent);
        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                conn();
//            }
//        }).start();

    }

    private void conn() {
        // 创建Socket
        NioSocketConnector connector = new NioSocketConnector();
        //设置传输方式
        DefaultIoFilterChainBuilder chain = connector.getFilterChain();
        ProtocolCodecFilter filter = new ProtocolCodecFilter(new ObjectSerializationCodecFactory());
        chain.addLast("objectFilter", filter);

        //设置消息处理
        connector.setHandler(new MinaClientHanlder());
        //超时设置
        connector.setConnectTimeoutCheckInterval(30);
        //连接
        ConnectFuture cf = connector.connect(new InetSocketAddress(IP, PORT));
        cf.awaitUninterruptibly();
        cf.getSession().getCloseFuture().awaitUninterruptibly();
        connector.dispose();
    }

    @Subscribe
    public void onEventMainThread(final FirstEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(tv.getText() + "\n" + event.getMsg()+count++);
                if (event.getMsg().contains("on")) {
                    Toast.makeText(MainActivity.this, "设备开灯", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        try {
                            manager.setTorchMode("0", true);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }else if (event.getMsg().contains("off")){
                    Toast.makeText(MainActivity.this, "设备关灯", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        try {
                            manager.setTorchMode("0", false);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}
