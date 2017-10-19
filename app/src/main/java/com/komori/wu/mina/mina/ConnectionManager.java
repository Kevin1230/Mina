package com.komori.wu.mina.mina;

import android.content.Context;
import android.util.Log;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import com.komori.wu.mina.event.FirstEvent;

/**
 * Created by KomoriWu
 * on 2017-10-19.
 */


public class ConnectionManager {
    public static final String TAG = ConnectionManager.class.getSimpleName();

    private ConnectionConfig mConfig;

    private WeakReference<Context> mContext; //避免内存泄漏

    private NioSocketConnector mConnection;

    private IoSession mSession;

    private InetSocketAddress mAddress;

    public ConnectionManager(ConnectionConfig config) {
        this.mConfig = config;
        this.mContext = new WeakReference<>(config.getContext());

        init();
    }

    //通过构建者模式来进行初始化
    private void init() {

        mAddress = new InetSocketAddress(mConfig.getIp(), mConfig.getPort());

        mConnection = new NioSocketConnector();

        //设置读数据大小
        mConnection.getSessionConfig().setReadBufferSize(mConfig.getReadBufferSize());

        //添加日志过滤
        mConnection.getFilterChain().addLast("Logging", new LoggingFilter());

        //编码过滤
        mConnection.getFilterChain().addLast("codec", new ProtocolCodecFilter(
                new TextLineCodecFactory(Charset.forName("UTF-8"), LineDelimiter.WINDOWS.
                        getValue(),LineDelimiter.WINDOWS.getValue())));

        //事物处理
        mConnection.setHandler(new MinaClientHandler(mContext.get()));

        mConnection.setDefaultRemoteAddress(mAddress);// 设置默认访问地址
        mConnection.setConnectTimeoutMillis(30000); //设置连接超时
    }

    //连接方法（外部调用）
    public boolean connection() {
        try {
            ConnectFuture future = mConnection.connect();
            //一直连接，直至成功
            future.awaitUninterruptibly();// 等待连接创建成功
            mSession = future.getSession();// 获取会话
            Log.d(TAG, "connection");
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            return false;
        }

        return mSession != null;
    }

    //断开连接方法（外部调用）
    public void disConnect() {
        //关闭
        mConnection.dispose();
        //大对象置空
        mConnection = null;
        mSession = null;
        mAddress = null;
        mContext = null;
        Log.d(TAG, "disConnect");
    }


    //内部类实现事物处理
    private class MinaClientHandler extends IoHandlerAdapter {

        private Context mContext;

        MinaClientHandler(Context context) {
            this.mContext = context;
        }

        @Override
        public void sessionCreated(IoSession session) throws Exception {


        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            //将我们的session 保存到我们sessionManager 中，从而可以发送消息到服务器
            session.write("hello");
        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {
            super.sessionClosed(session);
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            //1,EventBus 来进行事件通知
            //2,广播
            if (mContext != null) {
//                Intent intent = new Intent(BROADCAST_ACTION);
//                intent.putExtra(MESSAGE, message.toString());
//                //使用局部广播，保证安全性
//                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                Log.d(TAG, "messageReceived:" + message.toString());
                EventBus.getDefault().post(new FirstEvent(message.toString()));
            }
        }

        @Override
        public void messageSent(IoSession session, Object message) throws Exception {
            super.messageSent(session, message);
        }
    }

    public void sendMess(String mess){
        try {
            new MinaClientHandler(mContext.get()).sessionOpened(mSession);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
