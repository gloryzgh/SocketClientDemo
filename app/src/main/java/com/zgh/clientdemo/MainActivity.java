package com.zgh.clientdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtIP;
    private EditText mEtContent;
    public static final String TAG = "SocketClient";
    private UESocketClient mSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_connect).setOnClickListener(this);
        findViewById(R.id.btn_send).setOnClickListener(this);
        mEtIP = findViewById(R.id.edit_ip);
        mEtContent= findViewById(R.id.edit_content);
        mSocketClient = UESocketClient.getInstance(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_connect:
                Log.d(TAG, "connect : ");
                String serverIP = mEtIP.getText().toString().trim();
                if (serverIP.isEmpty()) {
                    Toast.makeText(this, "请输入服务器IP", Toast.LENGTH_SHORT).show();
                } else {
                    mSocketClient.connect(serverIP);
                }
                break;
            case R.id.btn_send:
                Log.d(TAG, "send: ");
                String content = mEtContent.getText().toString().trim();
                if (!content.isEmpty()) {
                    mSocketClient.sendMsg(content);
                    mEtContent.getText().clear();
                }
                break;
        }
    }


}