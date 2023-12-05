package com.example.myappli.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myappli.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpActivity extends Activity {//AppCompat
    TextView mTextView;
    String strUserName;
    String strPassWord;
    String strMail;
    String strVcode;
    String strPhoneNum;
    String TrueVcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        EditText userName = (EditText) this.findViewById(R.id.UserNameEdit);
        EditText passWord = (EditText) this.findViewById(R.id.PassWordEdit);
        EditText email = (EditText) this.findViewById(R.id.MailEdit);
        EditText vcode = (EditText) this.findViewById(R.id.v_codeEdit);
        EditText phoneNum = (EditText) this.findViewById(R.id.PhoneEdit);
        Button v_codeGetButton = (Button) this.findViewById(R.id.v_codeSendCommitButton);
        Button SignUpCommitButton = (Button) this.findViewById(R.id.CommitButton);
        Button ReturnLoginButton = (Button) this.findViewById(R.id.ReturnButton);
        //mTextView = findViewById(R.id.textView);

        v_codeGetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(email.getText())) {
                    Toast.makeText(SignUpActivity.this, "请输入邮箱", Toast.LENGTH_SHORT).show();
                } else {
                    strMail = email.getText().toString();
                    okHttpEmail();
                    CountDownTimer timer = new CountDownTimer(60 * 1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            Log.i("TAG", "millisUntilFinished:" + millisUntilFinished);
                            v_codeGetButton.setClickable(false);
                            v_codeGetButton.setEnabled(false);
                            v_codeGetButton.setText(millisUntilFinished / 1000 + "秒后重发");
                        }

                        @Override
                        public void onFinish() {
                            v_codeGetButton.setText("发送验证码");
                            v_codeGetButton.setClickable(true);
                            v_codeGetButton.setEnabled(true);
                        }
                    }.start();
                }
            }
        });


        SignUpCommitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(userName.getText())) {
                    Toast.makeText(SignUpActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(passWord.getText())) {
                    Toast.makeText(SignUpActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(email.getText())) {
                    Toast.makeText(SignUpActivity.this, "请输入邮箱", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(vcode.getText())) {
                    Toast.makeText(SignUpActivity.this, "请输入邮箱验证码", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(phoneNum.getText())) {
                    Toast.makeText(SignUpActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
                }
                else {
                    strUserName = userName.getText().toString();
                    strPassWord = passWord.getText().toString();
                    strMail = email.getText().toString();
                    strVcode = vcode.getText().toString();
                    strPhoneNum = phoneNum.getText().toString();
                    okhttpData();
                }
            }
        });

        ReturnLoginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }
        );

    }

    //post异步请求
    private void okhttpData() {
        Log.i("TAG","--sign_up_Post_begin-");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MediaType JSON = MediaType.parse("application/json");
                OkHttpClient client = new OkHttpClient();
                String jsonObject = String.format("{\"email\": \"%s\",\"name\": \"%s\",\"password\": \"%s\",\"phone\": \"%s\",\"v_code\": \"%s\"}", strMail, strUserName,strPassWord,strPhoneNum,strVcode);
                RequestBody body = RequestBody.create(JSON, jsonObject);
                Request request = new Request.Builder()
                        .url("http://39.105.180.206:9000/register")
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

    private void okHttpEmail(){
        Log.i("TAG","--email_Post_begin-");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient Client = new OkHttpClient();
                HttpUrl.Builder httpBuilder = HttpUrl.parse("http://39.105.180.206:9000/send_email").newBuilder();
                httpBuilder.addQueryParameter("email",strMail);

                RequestBody body = RequestBody.create(null, "");

                Request request = new Request.Builder()
                        .url(httpBuilder.build())
                        .post(body)//Post请求的参数传递
                        .build();
                Client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //此方法运行在子线程中，不能在此方法中进行UI操作。
                        String jsondata = response.body().string();
                        //解析
                        jsonEmailData(jsondata,response);
                        response.body().close();
                    }
                });
            }
        });
    }

    private void jsonJXData(String jsondata,Response response) {//解析JSON
        if (jsondata != null) {
            //JSONObject jsonobject = new JSONObject(jsondata);
            //String token = jsonobject.getString("token");
            if (response.code() == 200) {
                Looper.prepare();
                Toast.makeText(this,"注册成功，请登录", Toast.LENGTH_LONG).show();
                Looper.loop();
            }else if(response.code() == 400){
                Looper.prepare();
                Toast.makeText(this,"验证码错误", Toast.LENGTH_LONG).show();
                //Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                //startActivity(intent);
                Looper.loop();
            }
            else{
                Looper.prepare();
                Toast.makeText(SignUpActivity.this, "错误，请稍候", Toast.LENGTH_LONG).show();
                Looper.loop();
            }

        }
    }
    private void jsonEmailData(String jsondata,Response response) {//解析JSON
        if (jsondata != null) {
            //JSONObject jsonobject = new JSONObject(jsondata);
            if (response.code() == 200) {
                TrueVcode = jsondata;
            } else if(response.code() != 200) {
                Looper.prepare();
                Toast.makeText(SignUpActivity.this, "错误，请稍候", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }
    }
}

