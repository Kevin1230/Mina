package com.komori.wu.mina.mina;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.komori.wu.mina.MainActivity;

/**
 * Created by KomoriWu
 * on 2017-10-19.
 */

public class MinaService extends Service {
    public static final String TAG = MinaService.class.getSimpleName();
    private ConnectionThread thread;

    @Override
    public void onCreate() {
        super.onCreate();
        thread = new ConnectionThread("mina", getApplicationContext());
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //负责调用ConnectionManager类来完成与服务器连接

    class ConnectionThread extends HandlerThread {

        private Context context;

        boolean isConnection;

        ConnectionManager mManager;

        public ConnectionThread(String name, Context context) {
            super(name);
            this.context = context;

            ConnectionConfig config = new ConnectionConfig.Builder(context)
                    .setIp(MainActivity.IP)
                    .setPort(MainActivity.PORT)
                    .setReadBuilder(10240)
                    .setConnectionTimeout(10000).builder();
            mManager = new ConnectionManager(config);
        }

        //run 开始连接我们的服务器
        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            //死循环
            for (; ; ) {
                isConnection = mManager.connection();
                Log.d(TAG, "isConnection:" + isConnection);
                if (isConnection) {
                    break;
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        //断开连接
        public void disConnection() {
            mManager.disConnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.disConnection();
        thread = null;
    }

}
