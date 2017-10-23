package com.komori.wu.mina.mina.listener;

import android.util.Log;

import com.komori.wu.mina.event.FirstEvent;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by KomoriWu
 * on 2017-10-23.
 */

public class KeepAliveFactoryImpl implements KeepAliveMessageFactory {
    public static final String TAG = KeepAliveFactoryImpl.class.getSimpleName();
    //心跳包内容
    private static final String HEART_BEAT_REQUEST = "yes";
    private static final String HEART_BEAT_RESPONSE = "heart";

    @Override
    public boolean isRequest(IoSession ioSession, Object message) {
        Log.d(TAG, "isRequest:" + message);
        if (message.equals(HEART_BEAT_REQUEST)) {
            EventBus.getDefault().post(new FirstEvent(message.toString()));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isResponse(IoSession ioSession, Object message) {
        if (message.equals(HEART_BEAT_RESPONSE)){
            return true;
        }
        Log.d(TAG, "isResponse:" + message);
        return false;
    }

    @Override
    public Object getRequest(IoSession ioSession) {
        return HEART_BEAT_RESPONSE;
    }

    @Override
    public Object getResponse(IoSession ioSession, Object o) {
        return HEART_BEAT_RESPONSE;
    }
}
