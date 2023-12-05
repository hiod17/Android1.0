package com.example.myappli.user;

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
import android.widget.Toast;

import com.example.myappli.MainActivity;
import com.example.myappli.R;
import com.example.myappli.login.LoginActivity;
import com.example.myappli.login.SignUpActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChangePwd_LogoutActivity extends Activity {
    String strOld;
    String strNew;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepwd_logout);
        EditText oldpwd = this.findViewById(R.id.OldpassWord);
        EditText newpwd=this.findViewById(R.id.NewpassWord);
        Button changeCommit = this.findViewById(R.id.ChangeCommitButton);
        Button LogoutButton = this.findViewById(R.id.LogoutButton);

        changeCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(oldpwd.getText())) {
                    Toast.makeText(ChangePwd_LogoutActivity.this, "请输入旧密码", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(newpwd.getText())) {
                    Toast.makeText(ChangePwd_LogoutActivity.this, "请输入新密码", Toast.LENGTH_SHORT).show();
                }
                else {
                    strOld = oldpwd.getText().toString();
                    strNew = newpwd.getText().toString();
                    okhttpData();
                }
            }
        });

        LogoutButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ChangePwd_LogoutActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }
        );

    }

    private void okhttpData() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MediaType JSON = MediaType.parse("application/json");
                OkHttpClient client = new OkHttpClient();
                String jsonObject = String.format("{\"new_password\": \"%s\",\"old_password\": \"%s\"}",  strNew,strOld);
                RequestBody body = RequestBody.create(JSON, jsonObject);
                Request request = new Request.Builder()
                        .url("http://39.105.180.206:9000/change_password")
                        .post(body)//Post请求的参数传递
                        .header("X-Token",token)
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
            //JSONObject jsonobject = new JSONObject(jsondata);
            //String token = jsonobject.getString("token");
            if (response.code() == 200) {
                Looper.prepare();
                Toast.makeText(this,"修改成功", Toast.LENGTH_LONG).show();
                Looper.loop();
            }/*else if(response.code() == 400){
                Looper.prepare();
                Toast.makeText(this,"验证码错误", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
                Looper.loop();
            }*/
            else{
                Looper.prepare();
                Toast.makeText(ChangePwd_LogoutActivity.this, "修改失败，请检查原密码", Toast.LENGTH_LONG).show();
                Looper.loop();
            }

        }
    }
}
