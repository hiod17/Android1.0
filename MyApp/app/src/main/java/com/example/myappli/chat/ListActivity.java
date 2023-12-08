package com.example.myappli.chat;

import static com.example.myappli.MainActivity.*;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myappli.R;
import com.example.myappli.login.LoginActivity;
import com.example.myappli.login.SignUpActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class ListActivity extends Activity {
    private Button jobButton;
    //public int total_count;
    public static HistoryClass nowchatter;
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    List<HistoryClass> userList = new ArrayList<>();
    //Map<Integer,String> user2name =new Map<Integer, String>() {

    public static HashMap<Integer,List<HistoryClass>> historyList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);//activity_search
        //HomeViewModel homeViewModel = new ViewModelProvider(this)
        //              .get(HomeViewModel.class);

        EditText area=this.findViewById(R.id.areaEdit);
        jobButton=this.findViewById(R.id.jobButton);

        for(int i=0;i<=total_count_of_users;i++) {
            okHttpGetHistory(i);
        }
        setonws();

    }

    private void setonws(){
        //okHttpGetUser();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("ws://39.105.180.206:9000/chat/ws")
                .get()
                .header("X-Token",token)
                .build();
        websocket = client.newWebSocket(request, new WebSocketListener() {
            public void onOpen(WebSocket webSocket, Response response) {
                        super.onOpen(webSocket, response);
                Log.i("TAG","连接成功");
            }
            public void onMessage(WebSocket webSocket, String text) {
                Log.i("TAG","on_message_begin" + text);
                super.onMessage(webSocket, text);
                System.out.println("收到text消息:" + text);
                JSONObject j;
                try {
                    j = new JSONObject(text);
                    if(j.get("from").toString().equals("0")) {
                        notification(j.get("msg").toString());
                    }else{
                        HistoryClass newmsg=new HistoryClass(j.getInt("from"),usermap.get(j.get("from")),
                                j.getInt("from"),j.getInt("to"),j.getString("msg"),j.getString("time"));
                        historyList.get(j.get("from")).add(0,newmsg);//.add(j);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // webSocket.close( 1000,"再见");
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
                Log.i("TAG","on_message_byte_begin" + bytes);
            }
        })
        ;

    }
    //显示通知栏
    private void notification(String message) {
        NotificationManager mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification;
        Intent intent = new Intent(this, ListActivity.class);//MessageActivity.class
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("default", "name", NotificationManager.IMPORTANCE_HIGH);
            mChannel.setShowBadge(true);
            mChannel.enableLights(true);

            mManager.createNotificationChannel(mChannel);

            notification = new NotificationCompat.Builder(this, "default")
                    .setContentTitle("BUAAJobHunting")//设置通知栏标题
                    .setContentText(message) //设置通知栏显示内容
                    .setWhen(System.currentTimeMillis())//通知产生的时间。
                    .setSmallIcon(R.drawable.logo1)//设置通知小ICON
                    .setDefaults(NotificationManager.IMPORTANCE_HIGH)
                    .setPriority(NotificationManager.IMPORTANCE_HIGH)
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    //.setFullScreenIntent(pendingIntent, true)
                    .setContentIntent(pendingIntent)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo1))//设置通知大ICONmipmap.app_icon
                    .build();
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "default")
                    .setContentTitle("标题")
                    .setContentText(message)
                    .setWhen(System.currentTimeMillis())//通知产生的时间。
                    //.setSmallIcon(R.mipmap.app_icon)
                    .setDefaults(NotificationCompat.PRIORITY_HIGH)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setDefaults(NotificationCompat.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    //.setFullScreenIntent(pendingIntent, true)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo1));
            notification = notificationBuilder.build();
        }
        mManager.notify(1, notification);
    }

    public void okHttpGetHistory(int useri){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                HttpUrl.Builder httpBuilder = HttpUrl.parse("http://39.105.180.206:9000/chat/history").newBuilder();
                httpBuilder.addQueryParameter("user_id", String.valueOf(useri));

                Request request = new Request.Builder()
                        .url(httpBuilder.build())
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
                        try {
                            jsonJXHistory(jsondata,response,useri);//token对应的userid与useri之间的history
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        response.body().close();
                    }
                });
            }
        });

    }

    private void jsonJXHistory(String jsondata,Response response,int user_id) throws JSONException {//解析JSON
        Log.i("TAD","history_jiexi_begin");
        if (jsondata != null) {
            if(user_id == 0){
                Looper.prepare();
                //Toast.makeText(ListActivity.this,"来自系统",Toast.LENGTH_LONG).show();


                JSONObject jsonObject = new JSONObject(jsondata);
                JSONArray jobs = jsonObject.getJSONArray("messages");
                JSONObject jsonObject1 = jobs.getJSONObject(0);
                String message = jsonObject1.getString("msg");
                notification(message);
                Looper.loop();
                return;
            }
            JSONObject jsonObject = new JSONObject(jsondata);
            int history_count = jsonObject.getInt("total_count");
            List<HistoryClass> chatList = new ArrayList<>();
            if(history_count == 0){
                HistoryClass userhistory = new HistoryClass(user_id, usermap.get(user_id), 0,0,"","");
                chatList.add(userhistory);

                historyList.put(user_id,chatList);
            }
            else {
                JSONArray jobs = jsonObject.getJSONArray("messages");//遍历JSONArray对象，解析后放入集合中

                for (int i = 0; i < jobs.length(); i++) {
                    JSONObject jsonObject1 = jobs.getJSONObject(i);
                    int from = jsonObject1.getInt("from");
                    int to =jsonObject1.getInt("to");

                    String message = jsonObject1.getString("msg");
                    String time = jsonObject1.getString("time");

                    HistoryClass userhistory = new HistoryClass(user_id, usermap.get(user_id), from, to,message,time);
                    chatList.add(userhistory);
                    //userList.add(userhistory);
                }
                historyList.put(user_id,chatList);
            }userList.add(historyList.get(user_id).get(0));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView = findViewById(R.id.recyclerview);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(ListActivity.this);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);

                    myAdapter = new MyAdapter();
                    recyclerView.setAdapter(myAdapter);

//                     View header_view = getLayoutInflater().inflate(R.layout.card_header,null);
//                      myAdapter.setHeaderView(header_view);
                    View footer_view = getLayoutInflater().inflate(R.layout.card_footer, null);
                    myAdapter.setFooterView(footer_view);
                }
            });
        }
    }

//    public void myItemClick(View view){
//        int position = recyclerView.getChildAdapterPosition(view);
//        nowchatter = userList.get(position);
//        Intent intent = new Intent(ListActivity.this, History_Send_Activity.class);
//        startActivity(intent);
//        Toast.makeText(ListActivity.this,"点击了"+String.valueOf(position),Toast.LENGTH_LONG).show();
//    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHoder> {
        private final int HEADER_TYPE =1;
        private final int FOOTER_TYPE =2;
        private final int NORMAL_TYPE =4;
        //View headerView;
        View footerView;
        //String head = String.format("  共%d条结果",total_count);

        @Override
        public MyViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {
            // if(viewType == HEADER_TYPE){
            // return new MyViewHoder(headerView);
            if(viewType == FOOTER_TYPE){
                return new MyViewHoder(footerView);
            }
            View view = getLayoutInflater().inflate(R.layout.card_chat_list,null);
            return new MyViewHoder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHoder holder, int position) {
            /*if(position == 0){
                holder.card_header.setText(String.format(head));
                return;
            }
            if(position == (getItemCount()-1)){
                return;
            }*/
            if(position < getItemCount()-1){
                HistoryClass chats = userList.get(position);
                holder.username_edit.setText(chats.username);
                holder.message_edit.setText(chats.message);//////
                holder.time_edit.setText(chats.time);
                holder.chat_list_card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nowchatter = userList.get(position);
                        Intent intent = new Intent(ListActivity.this, History_Send_Activity.class);
                        startActivity(intent);
                    }
                });
            }


        }


        @Override
        public int getItemCount() {
            return userList.size()+1;
        }

        public View getFooterView() {
            return footerView;
        }

        public void setFooterView(View footerView) {
            this.footerView = footerView;
        }
        @Override
        public int getItemViewType(int position){
            if(position == getItemCount()-1){
                return FOOTER_TYPE;
            }
            return NORMAL_TYPE;
        }
    }

    class MyViewHoder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView username_edit;
        TextView message_edit;
        TextView time_edit;

        IMyViewHolderClicks mListener;
        CardView chat_list_card;

        public MyViewHoder(View itemView, IMyViewHolderClicks Listener) {
            super(itemView);
            mListener = Listener;
            username_edit = itemView.findViewById(R.id.ChatterNameEdit);
            message_edit = itemView.findViewById(R.id.chatMessageEdit);
            time_edit = itemView.findViewById(R.id.TimeEdit);

        }
        public MyViewHoder(View itemView) {
            super(itemView);
            username_edit = itemView.findViewById(R.id.ChatterNameEdit);
            message_edit = itemView.findViewById(R.id.chatMessageEdit);
            time_edit = itemView.findViewById(R.id.TimeEdit);
            chat_list_card = itemView.findViewById(R.id.card_list);
        }
        @Override
        public void onClick(View view){
            mListener.onItemClick(view,getLayoutPosition());
        }
    }
    private interface IMyViewHolderClicks{
        public void onItemClick(View view,int ListClass);
    }
    //是否有通知权限
    public static boolean isOpenNotify(Context context) {
        boolean isOpened = false;
        try {
            NotificationManagerCompat from = NotificationManagerCompat.from(context);
            isOpened = from.areNotificationsEnabled();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isOpened;
    }

    //前往设置
    public static void goToSetNotify(Context context) {
        if(Build.VERSION.SDK_INT >=26) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE,context.getPackageName());
            context.startActivity(intent);
        } else {
            Intent intent = new Intent();
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package",context.getApplicationContext().getPackageName());
            intent.putExtra("app_uid",context.getApplicationInfo().uid);
            context.startActivity(intent);
        }
    }
}
