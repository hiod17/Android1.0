package com.example.myappli;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends Activity {//AppCompat

    TextView mTextView;
    String strUserName;
    String strPassWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EditText userName = (EditText) this.findViewById(R.id.UserNameEdit);
        EditText passWord = (EditText) this.findViewById(R.id.PassWordEdit);
        Button loginButton = findViewById(R.id.LoginButton);
        Button signUpButton = (Button) this.findViewById(R.id.SignUpButton);
        //mTextView = findViewById(R.id.textView);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(userName.getText())) {
                    Toast.makeText(MainActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(passWord.getText())) {
                    Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                } else {
                    strUserName = userName.getText().toString();
                    strPassWord = passWord.getText().toString();
                    okhttpData();
                }
            }
        });

        signUpButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                        startActivity(intent);
                    }
                }
        );

    }

    //post异步请求
    private void okhttpData() {
        Log.i("TAG","--ok-");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MediaType JSON = MediaType.parse("application/json");
                OkHttpClient client = new OkHttpClient();
                String jsonObject = String.format("{\"password\": \"%s\",\"username\": \"%s\"}", strPassWord, strUserName);
                RequestBody body = RequestBody.create(JSON, jsonObject);
                Request request = new Request.Builder()
                        .url("http://39.105.180.206:9000/login")
                        .post(body)//Post请求的参数传递
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //此方法运行在子线程中，不能在此方法中进行UI操作。
                        String jsondata = response.body().string();
                        //解析
                        jsonJXData(jsondata,response);
                        response.body().close();
                    }
                });
            }
        });

    }

    private void jsonJXData(String jsondata,Response response) {//解析JSON
        if (jsondata != null) {
            try {
                JSONObject jsonobject = new JSONObject(jsondata);
                String token = jsonobject.getString("token");
                if (token != null) {
                    Looper.prepare();
                    Toast.makeText(this,"登陆成功", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                    startActivity(intent);
                    Looper.loop();
                } else{
                    Looper.prepare();
                    Toast.makeText(MainActivity.this, response.body().string(), Toast.LENGTH_LONG).show();
                    Looper.loop();
                }

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

        }
    }
}



