package com.example.myappli.user;

import static com.example.myappli.MainActivity.token;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myappli.R;
import com.example.myappli.login.LoginActivity;
import com.example.myappli.search.bosszp_jobActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
//import com.jzxiang.pickerview.TimePickerDialog;
//import com.jzxiang.pickerview.data.Type;
//import com.jzxiang.pickerview.listener.OnDateSetListener;

public class ReminderActivity extends Activity {
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    List<RemindClass> remindList;//implements DatePickerDialog.OnDateSetListener, View.OnClickListener {
    private Button delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        //EditText area = this.findViewById(R.id.areaEdit);
        Button newRemind =this.findViewById(R.id.newRemindButton);
        Button delete = this.findViewById(R.id.deletebutton);

        newRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReminderActivity.this, RemindAddActivity.class);
                startActivity(intent);
                myAdapter = new MyAdapter();
                recyclerView.setAdapter(myAdapter);

                View header_view = getLayoutInflater().inflate(R.layout.card_header,null);
                myAdapter.setHeaderView(header_view);
                View footer_view = getLayoutInflater().inflate(R.layout.card_footer,null);
                myAdapter.setFooterView(footer_view);
            }
        });

        okhttpGet();

    }

    public void okhttpGet(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://39.105.180.206:9000/reminder/get")
                .get()
                .header("X-Token",token)//"336fe8bd-d08d-458f-84dc-94c6801a007a@1"
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Something went wrong
                System.out.println("fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();

                    try {
                        jsonGetData(responseStr, response);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public int total_count;
    private void jsonGetData(String jsondata,Response response) throws JSONException {//解析JSON
        Log.i("TAG",jsondata);
        remindList = new ArrayList<>();
        if (jsondata != null) {
            JSONObject jsonObject = new JSONObject(jsondata);
            total_count = jsonObject.getInt("total_count");
            JSONArray jobs;
            try {
                jobs = jsonObject.getJSONArray("reminders");//遍历JSONArray对象，解析后放入集合中
            } catch (JSONException e) {
                jobs = new JSONArray();
            }
            for (int time = 0;time < jobs.length();time++) {
                JSONObject jsonObject1 = jobs.getJSONObject(time);
                int reminder_id = jsonObject1.getInt("reminder_id");

                String message = jsonObject1.getString("message");
                String remindtime = jsonObject1.getString("time");

                boolean has_sent = jsonObject1.getBoolean("has_sent");

                RemindClass remind = new RemindClass(reminder_id,message,remindtime,has_sent);
                remindList.add(remind);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView = findViewById(R.id.recyclerview);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(ReminderActivity.this);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);

                    myAdapter = new MyAdapter();
                    recyclerView.setAdapter(myAdapter);
                    View header_view = getLayoutInflater().inflate(R.layout.card_header,null);
                    myAdapter.setHeaderView(header_view);
                    View footer_view = getLayoutInflater().inflate(R.layout.card_footer,null);
                    myAdapter.setFooterView(footer_view);
                }
            });
        }
    }
    //@Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        //Toast.makeText(this, "你选择的时间:"+getDateToString(millseconds), Toast.LENGTH_SHORT).show();
    }

    class MyAdapter extends RecyclerView.Adapter<ReminderActivity.MyViewHoder> {
        private final int HEADER_TYPE =1;
        private final int FOOTER_TYPE =2;
        private final int NORMAL_TYPE =4;
        View headerView;
        View footerView;
        String head = String.format("  您的提醒：共%d条结果",total_count);

        @Override
        public MyViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == HEADER_TYPE){
                return new MyViewHoder(headerView);
            }else if(viewType == FOOTER_TYPE){
                return new MyViewHoder(footerView);
            }
            View view = getLayoutInflater().inflate(R.layout.card_reminder,null);
            return new MyViewHoder(view, new IMyViewHolderClicks() {
                @Override
                public void onItemClick(View view, int position) {
                    //Log.i("TAG","fav_success");
                    okhttpPost(view,position);//user_favourite.add(mJobsList.get(position-1));
                    remindList.remove(remindList.get(position-1));
                    total_count = remindList.size();
                    myAdapter = new MyAdapter();
                    recyclerView.setAdapter(myAdapter);

                    View header_view = getLayoutInflater().inflate(R.layout.card_header,null);
                    myAdapter.setHeaderView(header_view);
                    View footer_view = getLayoutInflater().inflate(R.layout.card_footer,null);
                    myAdapter.setFooterView(footer_view);
                }
            });

        }
        public void okhttpPost(View view,int position){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    OkHttpClient Client = new OkHttpClient();
                    HttpUrl.Builder httpBuilder = HttpUrl.parse("http://39.105.180.206:9000/reminder/delete").newBuilder();
                    httpBuilder.addQueryParameter("id", String.valueOf(remindList.get(position-1).reminderId));

                    RequestBody body = RequestBody.create(null, "");

                    Request request = new Request.Builder()
                            .url(httpBuilder.build())
                            .delete()//Post请求的参数传递
                            .header("X-Token",token)
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
                            String words = null;
                            if (response.code() == 200) {
                                words = "已删除";
                                Looper.prepare();
                                Toast.makeText(ReminderActivity.this,"删除成功", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }else{
                                words="失败";
                            }
                            delete = view.findViewById(R.id.deletebutton);
                            delete.setText(words);//jsonEmailData(jsondata,response);
                            response.body().close();
                        }
                    });
                }
            });
        }
        @Override
        public void onBindViewHolder(@NonNull ReminderActivity.MyViewHoder holder, int position) {
            if(position == 0){
                holder.card_header.setText(String.format(head));
                return;
            }
            if(position == (getItemCount()-1)){
                return;
            }
            RemindClass reminds = remindList.get(position-1);
            holder.message.setText(reminds.message);
            holder.time.setText("提醒时间:"+reminds.time);
            String strhasSent;
            if (reminds.hasSent) strhasSent="已提醒";
            else strhasSent="未提醒";
            holder.has_sent.setText(strhasSent);
        }

        @Override
        public int getItemCount() {
            return remindList.size()+2;
        }

        public View getHeaderView() {
            return headerView;
        }

        public void setHeaderView(View headerView) {
            this.headerView = headerView;
        }

        public View getFooterView() {
            return footerView;
        }

        public void setFooterView(View footerView) {
            this.footerView = footerView;
        }
        @Override
        public int getItemViewType(int position){
            if(position ==0){
                return HEADER_TYPE;
            }
            if(position == getItemCount()-1){
                return FOOTER_TYPE;
            }
            return NORMAL_TYPE;
        }
    }

    class MyViewHoder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView message,time,has_sent;
        TextView card_header;
        IMyViewHolderClicks mListener;

        public MyViewHoder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.messageEdit);
            time = itemView.findViewById(R.id.remindTimeEdit);
            has_sent = itemView.findViewById(R.id.hassentEdit);
            card_header = itemView.findViewById(R.id.cardHeader);
        }
        public MyViewHoder(View itemView,IMyViewHolderClicks Listener) {
            super(itemView);
            mListener = Listener;
            delete = itemView.findViewById(R.id.deletebutton);
            delete.setOnClickListener(this);
            message = itemView.findViewById(R.id.messageEdit);
            time = itemView.findViewById(R.id.remindTimeEdit);
            has_sent = itemView.findViewById(R.id.hassentEdit);
            card_header = itemView.findViewById(R.id.cardHeader);
        }
        @Override
        public void onClick(View view){
            mListener.onItemClick(view,getLayoutPosition());
        }

    }

    private interface IMyViewHolderClicks{
        public void onItemClick(View view,int RemindClass);
    }
}
