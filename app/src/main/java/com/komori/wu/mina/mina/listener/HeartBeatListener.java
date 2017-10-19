package com.komori.wu.mina.mina.listener;

import android.util.Log;

import com.komori.wu.mina.MainActivity;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.IoServiceListener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;

public class HeartBeatListener implements IoServiceListener {
    public static final String TAG = HeartBeatListener.class.getSimpleName();
    public static final int TIMES = 10;
    public NioSocketConnector connector;

    public HeartBeatListener(NioSocketConnector connector) {
        this.connector = connector;
    }

    @Override
    public void serviceActivated(IoService arg0) throws Exception {
    }

    @Override
    public void serviceDeactivated(IoService arg0) throws Exception {
    }

    @Override
    public void serviceIdle(IoService arg0, IdleStatus arg1) throws Exception {
    }

    @Override
    public void sessionClosed(IoSession arg0) throws Exception {
        Log.d(TAG, "sessionClosed");
    }

    @Override
    public void sessionCreated(IoSession arg0) throws Exception {
    }

    @Override
    public void sessionDestroyed(IoSession arg0) {
        repeatConnect("");
    }

    /* 
     * 断线重连操作  
     * @param content 
     */
    public void repeatConnect(String content) {
        // 执行到这里表示Session会话关闭了，需要进行重连,我们设置每隔3s重连一次,如果尝试重连5次
        // 都没成功的话,就认为服务器端出现问题,不再进行重连操作
        int count = 0;// 记录尝试重连的次数  
        while (true) {
            try {
                count++;// 重连次数加1  
                ConnectFuture future = connector.connect(new InetSocketAddress(
                        MainActivity.IP, MainActivity.PORT));
                future.awaitUninterruptibly();// 一直阻塞住等待连接成功  
                IoSession session = future.getSession();// 获取Session对象  
                if (session.isConnected()) {
                    // 表示重连成功  
                    Log.d(TAG, content + " : 断线重连" + count + "次之后成功.....");
                    count = 0;
                    break;
                }
            } catch (Exception e) {
                if (count == TIMES) {
                    Log.d(TAG, "重连失败");
                    break;
                } else {
                    try {
                        Thread.sleep(5000);
                        Log.d(TAG, (count) + "次重连");
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
}  