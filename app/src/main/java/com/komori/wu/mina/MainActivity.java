package com.komori.wu.mina;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.komori.wu.mina.event.FirstEvent;
import com.komori.wu.mina.mina.MinaClientHanlder;
import com.komori.wu.mina.mina.MinaService;

public class MainActivity extends AppCompatActivity {
    public static final String IP = "182.61.24.26";
    public static final int PORT = 22001;
    private TextView tv;
    private Button btn, btn1;
    CameraManager manager;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        tv = (TextView) findViewById(R.id.tv);
        btn = (Button) findViewById(R.id.btn);
        btn1 = (Button) findViewById(R.id.btn1);
        Intent intent = new Intent(this, MinaService.class);
        startService(intent);
        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                conn();
//            }
//        }).start();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String str = readAssetsTxt("json");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            postJsonToServer(str);
                        } catch (UnsupportedEncodingException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        postJson1ToServer("grant_type=authorization_code" +
                                "&code=ANrjYeDtOsYcMnltfciW" +
                                "&client_id=amzn1.application-oa2-client.e339b059b0b6442fa9bcba6e64745d7d&" +
                                "client_secret=7c4fbe31e175d6a597fc878d6651692b5410cee33e4f0c440274148b194c5fe8");
                    }
                }).start();
            }
        });

    }

    private void postJson1ToServer(String str) {
        try {
            URL url = new URL("https://api.amazon.com/auth/o2/token");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(20000);
            httpURLConnection.setReadTimeout(50000);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            //设置请求体的类型是文本类型
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            PrintWriter out = new PrintWriter(httpURLConnection.getOutputStream());
            out.print(str);//写入输出流
            out.flush();//立即刷新

            out.close();

            int code = httpURLConnection.getResponseCode();
            if (code == 200) {
                InputStream is = httpURLConnection.getInputStream();
                //连接服务器后，服务器做出响应返回的数据
                String respStr = dealResponseResult(is);
                Log.d("respStr:", "" + respStr);
                is.close();
            }
            Log.d("code:", "" + code);

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }

    public static String dealResponseResult(InputStream inputStream) {
        String resultData = null;      //存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while ((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
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
        Log.d("MainActivity", event.getMsg());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(tv.getText() + "\n" + event.getMsg() + count++);
                if (event.getMsg().contains("On")) {
                    Toast.makeText(MainActivity.this, "设备开灯", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        try {
                            manager.setTorchMode("0", true);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (event.getMsg().contains("Off")) {
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

    /**
     * 将Json对象上传服务器
     *
     * @param str
     * @throws JSONException
     */
    private void postJsonToServer(String str) throws JSONException,
            UnsupportedEncodingException {
        try {
            URL url = new URL("https://api.amazonalexa.com/v3/events");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(20000);
            httpURLConnection.setReadTimeout(50000);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            PrintWriter out = new PrintWriter(httpURLConnection.getOutputStream());
            out.print(str);//写入输出流
            out.flush();//立即刷新

            out.close();

            int code = httpURLConnection.getResponseCode();
            if (code == 202) {
                InputStream is = httpURLConnection.getInputStream();
                //连接服务器后，服务器做出响应返回的数据
                String respStr = dealResponseResult(is);
                Log.d("respStr1:", "" + respStr);
                is.close();
            }
            Log.d("code:", "" + code);

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 读取assets下的txt文件，返回utf-8 String
     *
     * @param fileName 不包括后缀
     * @return
     */
    public String readAssetsTxt(String fileName) {
        try {
            //Return an AssetManager instance for your application's package
            InputStream is = getAssets().open(fileName + ".txt");
            int size = is.available();
            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            // Convert the buffer into a string.
            String text = new String(buffer, "utf-8");
            // Finally stick the string into the text view.
            return text;
        } catch (IOException e) {
            // Should never happen!
//            throw new RuntimeException(e);
            e.printStackTrace();
        }
        return "读取错误，请检查文件名";
    }
}
