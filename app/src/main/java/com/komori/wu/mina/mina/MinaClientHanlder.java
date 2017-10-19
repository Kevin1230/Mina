package com.komori.wu.mina.mina;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/**
 * Created by KomoriWu
 * on 2017-10-19.
 */

public class MinaClientHanlder extends IoHandlerAdapter {  
    public void sessionOpened(IoSession session) throws Exception {
        System.out.println("客户端登陆");
        session.write("HelloWorld");  
  
//        messageReceived(session,"");  
        for (int i = 0; i < 10; i++) {  
            session.write("p 412703840,4,1,1410248991,73451566,22615771,1239,125,90,0,0,1,900\r\n"  
                    + "p 412703840,4,1,1410248991,73451566,22615771,1239,125,90,0,0,1,900\r\n"  
                    + "p 412703840,4,1,1410248991,73451566,22615771,1239,125,90,0,0,1,900\r\n"  
                    + "p 412703840,4,1,1410248991,73451566,22615771,1239,125,90,0,0,1,900");  
        }  
    }  
  
    public void sessionClosed(IoSession session)  
    {  
        System.out.println("client close");
    }  
  
    public void messageReceived(IoSession session , Object message)throws Exception
    {  
        System.out.println("客户端接受到了消息"+message) ;
  
//        session.write("Sent by Client1");  
    }  
}