package com.example.myappli.login;

import static com.example.myappli.MainActivity.token;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myappli.MainActivity;
import com.example.myappli.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends Activity {//AppCompat

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
        TextView resetPwd = findViewById(R.id.textView);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(userName.getText())) {
                    Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(passWord.getText())) {
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
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
                        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                        startActivity(intent);
                    }
                }
        );

        resetPwd.setOnClickListener(
                v -> {
                    Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                    startActivity(intent);
                }
        );

    }

    //post异步请求
    private void okhttpData() {
        //Log.i("TAG","--ok-");
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
                        //Map<String, Object> map = new HashMap<String, Object>();

                        //JSONObject json = JSONObject.parseObject(jsondata);//解析
                        jsonJXData(jsondata,response);
                        //response.body().close();
                    }
                });
            }
        });

    }

    private void jsonJXData(String json,Response response) throws IOException {//解析JSON
        if (json != null) {
            //JSONObject jsonobject = new JSONObject(jsondata);
            Looper.prepare();//String token = jsonobject.getString("token");
            if (response.code()==200) {

                Toast.makeText(this,"登录成功", Toast.LENGTH_LONG).show();
                try {
                    JSONObject j = new JSONObject(json);
                    //token tok= j.get("token").toString();//response.header()
                    String token=j.get("token").toString();
                    //request=request.newBuilder()
                    //        .addHeader()
                    //        .build();
                    Log.v("TAG",token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }//String token=
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

            } else{
                //Looper.prepare();
                Toast.makeText(LoginActivity.this, json, Toast.LENGTH_LONG).show();
                //Looper.loop();
            }Looper.loop();
            //JSONException |

        }
    }
}



