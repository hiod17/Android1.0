package com.example.myappli.user;

import static com.example.myappli.MainActivity.token;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myappli.R;
import com.example.myappli.login.LoginActivity;
import com.example.myappli.login.SignUpActivity;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RemindAddActivity extends AppCompatActivity {//implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    String strmessage;
    String strTime;
    int year,month,day,hour,minute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_add);
        EditText message = this.findViewById(R.id.messageEdit);
        DatePicker datePicker = this.findViewById(R.id.dp);
        TimePicker timePicker = this.findViewById(R.id.tp);
        timePicker.setIs24HourView(true);
        Calendar calendar = Calendar.getInstance();//"2023/12/05 15:22"
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
        Button ReturnButton = this.findViewById(R.id.ReturnButton);
        Button CommitButton = this.findViewById(R.id.CommitButton);

        datePicker.init(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int Year, int monthOfYear, int dayOfMonth) {
                //获取选中的年月日
                RemindAddActivity.this.year = Year;
                //月份是从0开始的
                RemindAddActivity.this.month = (monthOfYear + 1);
                RemindAddActivity.this.day = dayOfMonth;
                //弹窗显示
            }
        });

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int Minute) {
                RemindAddActivity.this.hour = hourOfDay;
                //月份是从0开始的
                RemindAddActivity.this.minute = Minute;
                //Toast.makeText(RemindAddActivity.this, RemindAddActivity.this.year+" "+
                //      RemindAddActivity.this.month+" "+RemindAddActivity.this.day+" "+RemindAddActivity.this.hour+" "+RemindAddActivity.this.minute,Toast.LENGTH_SHORT).show();

            }
        });

        //strTime=year+"/"+String.format("%02",month)+"/"+day+" "+hour+":"+minute;
        System.out.println(year+" "+month+" "+day+" "+RemindAddActivity.this.hour+":"+RemindAddActivity.this.minute);


        CommitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(message.getText())) {
                    Toast.makeText(RemindAddActivity.this, "请输入提醒内容", Toast.LENGTH_SHORT).show();
                } /*else if (TextUtils.isEmpty(passWord.getText())) {
                    Toast.makeText(RemindAddActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                }*/ else {
                    strmessage = message.getText().toString();
                    //strTime = passWord.getText().toString();
                    DateFormat format1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                    Date datetime=new Date(RemindAddActivity.this.year - 1900, RemindAddActivity.this.month - 1,
                            RemindAddActivity.this.day, RemindAddActivity.this.hour,RemindAddActivity.this.minute);
                    try {
                        datetime = format1.parse(RemindAddActivity.this.year + "/" + RemindAddActivity.this.month +
                                "/" + RemindAddActivity.this.day + " " + RemindAddActivity.this.hour + ":" + RemindAddActivity.this.minute);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    System.out.println(datetime+"?");
                    //SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                    strTime = format1.format(datetime);//"2023/12/05 8:01";
                    System.out.println(strTime);
                    okhttppost();
                }
            }
        });

        ReturnButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(RemindAddActivity.this, ReminderActivity.class);
                        startActivity(intent);
                    }
                }
        );

    }
    private void okhttppost() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MediaType JSON = MediaType.parse("application/json");
                OkHttpClient client = new OkHttpClient();
                String jsonObject = String.format("{\"message\": \"%s\",\"time\": \"%s\"}", strmessage,strTime);
                RequestBody body = RequestBody.create(JSON, jsonObject);
                Request request = new Request.Builder()
                        .url("http://39.105.180.206:9000/reminder/add")
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
                Toast.makeText(this,"此提醒添加成功", Toast.LENGTH_LONG).show();
                Looper.loop();

            }
            else{
                Looper.prepare();
                Toast.makeText(RemindAddActivity.this, "错误，请稍候", Toast.LENGTH_LONG).show();
                Looper.loop();
            }

        }
    }
}
