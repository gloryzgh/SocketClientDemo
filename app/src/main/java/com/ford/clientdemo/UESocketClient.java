package com.ford.clientdemo;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UESocketClient {
    private static final Object GET_INSTANCE_LOCK = new Object();
    private static volatile UESocketClient instance;
    private static final String TAG = UESocketClient.class.getSimpleName();
    private String LOCAL_IP = "192.168.2.100";
    private int LOCAL_SERVER_PORT = 5656;
    private Handler mHandler = new Handler();
    private ConnectedThread mConnectedThread;
    private Context mContext;
    private ExecutorService mSingleThreadExecutor;


    private UESocketClient() {
    }

    private UESocketClient(Context context) {
        this.mContext = context;
        mSingleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    public static UESocketClient getInstance(Context context) {
        if (instance == null) {
            synchronized (GET_INSTANCE_LOCK) {
                if (instance == null) {
                    instance = new UESocketClient(context);
                }
            }
        }
        return instance;
    }

    public void sendMsg(String content) {
        mSingleThreadExecutor.execute(() -> {
            if (mConnectedThread != null) {
                mConnectedThread.write(content.getBytes());
            }
        });
    }

    public void connect() {
       new ConnectThread().start();
    }

    public void connect(String ip) {
        LOCAL_IP = ip;
        new ConnectThread().start();
    }

    class ConnectThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                Log.d(TAG, "connect ip : "+LOCAL_IP);
                Socket socket = new Socket(InetAddress.getByName(LOCAL_IP), LOCAL_SERVER_PORT);
                connected(socket);
            } catch (IOException e) {
                Log.d(TAG, " connect error");
            }
        }
    }

    private void connected(Socket socket) {
        Log.d(TAG, "connected: ");
        mHandler.post(() -> Toast.makeText(mContext, "connect success", Toast.LENGTH_SHORT).show());
        if (mConnectedThread != null) {
            mConnectedThread.close();
            mConnectedThread = null;
        }
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
    }

    class ConnectedThread extends Thread {
        private Socket socket;
        private InputStream inputstream;
        private OutputStream outputstream;

        public ConnectedThread(Socket socket) {
            this.socket = socket;
            try {
                inputstream = socket.getInputStream();
                outputstream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            super.run();
            byte[] buffer = new byte[1024];
            int len;
            while (true) {
                try {
                    if ((len = inputstream.read(buffer)) != -1) {
                        String msg = new String(buffer, 0, len);
                        Log.d(TAG, "read data from server: " + msg);
                        mHandler.post(() -> Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

       public void write(byte[] data) {
            try {
                outputstream.write(data);
                outputstream.flush();
                Log.d(TAG, "write: " + new String(data));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void close() {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
