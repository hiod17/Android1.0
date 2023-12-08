package com.example.myappli.chat;

import static com.example.myappli.MainActivity.token;
import static com.example.myappli.MainActivity.websocket;
import static com.example.myappli.chat.ListActivity.historyList;
import static com.example.myappli.chat.ListActivity.nowchatter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myappli.R;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class History_Send_Activity extends Activity {

    EditText message;
    Button send_button;
    RecyclerView mrecyclerView;
    MyAdapter mAdapter= new MyAdapter();

    private final BroadcastReceiver dataSentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("CHAT_DATA_SENT".equals(intent.getAction())) {
                Log.i("ChatMessageSent","receive broadcast");
                HistoryClass updatedData = (HistoryClass) intent.getSerializableExtra("updatedData");
                // 在这里更新MyAdapter的数据
                // ...
                mAdapter.updateSentData(updatedData);
            }
        }
    };

    private final BroadcastReceiver dataReceiveReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("CHAT_DATA_RECEIVE".equals(intent.getAction())) {
                Log.i("ChatMessageSent","receive broadcast");
                HistoryClass updatedData = (HistoryClass) intent.getSerializableExtra("updatedData");
                // 在这里更新MyAdapter的数据
                // ...
                mAdapter.updateReceiveData(updatedData);
            }
        }
    };

    @Override
    protected void onDestroy() {
        // 在 onDestroy 中注销广播接收器
        unregisterReceiver(dataSentReceiver);
        unregisterReceiver(dataReceiveReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Intent intent = getIntent();
        setContentView(R.layout.activity_chat_page);
        message=this.findViewById(R.id.MessageEdit);
        send_button = this.findViewById(R.id.SendCommitButton);
        TextView username = this.findViewById(R.id.chatMember);
        username.setText(nowchatter.username);
        IntentFilter filter1 = new IntentFilter("CHAT_DATA_SENT");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(dataSentReceiver, filter1, Context.RECEIVER_NOT_EXPORTED);
        }

        IntentFilter filter2 = new IntentFilter("CHAT_DATA_RECEIVE");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(dataReceiveReceiver, filter2, Context.RECEIVER_NOT_EXPORTED);
        }

        int total_history = historyList.get(nowchatter.userid).size();
       //mAdapter.registerAdapterDataObserver(mObservable);
       showhistory();

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmsg();
                message.setText("");
//                mrecyclerView = findViewById(R.id.chat_recycler);
//                LinearLayoutManager layoutManager = new LinearLayoutManager(History_Send_Activity.this);
//                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//                layoutManager.setReverseLayout(true);
//                mrecyclerView.setLayoutManager(layoutManager);
//
//                mAdapter = new MyAdapter();
//                mrecyclerView.setAdapter(mAdapter);
//
//                // View header_view = getLayoutInflater().inflate(R.layout.card_header,null);
//                //  myAdapter.setHeaderView(header_view);
//                View footer_view = getLayoutInflater().inflate(R.layout.card_footer_chat, null);
//                mAdapter.setFooterView(footer_view);

            }
        });

    }

    private void showhistory(){
                mrecyclerView = findViewById(R.id.chat_recycler);
                LinearLayoutManager layoutManager = new LinearLayoutManager(History_Send_Activity.this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                layoutManager.setReverseLayout(true);
                mrecyclerView.setLayoutManager(layoutManager);

                //mAdapter = new MyAdapter();
                mrecyclerView.setAdapter(mAdapter);
                //mrecyclerView.setFocusable(true);
                //mrecyclerView.setFocusableInTouchMode(true);

                // View header_view = getLayoutInflater().inflate(R.layout.card_header,null);
                //  myAdapter.setHeaderView(header_view);
                View footer_view = getLayoutInflater().inflate(R.layout.card_footer_chat, null);
                mAdapter.setFooterView(footer_view);

    }
    private void sendmsg(){
            String jsonObject = String.format("{\"from\":%s,\"to\":%s,\"msg\": \"%s\"}",
                String.valueOf(token.charAt(token.length()-1)),String.valueOf(nowchatter.userid), message.getText().toString());
        //"{\"\":1,\"to\":1,\"msg\":\"你好\"}"
        websocket.send(jsonObject);

        Date date =new Date();
        HistoryClass newsend= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            newsend = new HistoryClass(nowchatter.userid,nowchatter.username,token.charAt(token.length()-1)-'0', nowchatter.userid, message.getText().toString(),date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")).toString());
        }else{
            newsend = new HistoryClass(nowchatter.userid,nowchatter.username,token.charAt(token.length()-1)-'0', nowchatter.userid, message.getText().toString(),date.toString());
        }
        Intent intent = new Intent("CHAT_DATA_SENT");
        intent.putExtra("updatedData", newsend);
        sendBroadcast(intent);
        Log.i("ChatMessageSent","send:" + newsend.message);
        //historyList.get(nowchatter.userid).add(0,newsend);//.add(j);


        //Toast.makeText(History_Send_Activity.this,"发送了"+jsonObject,Toast.LENGTH_LONG).show();
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHoder> {
        private final int HEADER_TYPE = 0;
        private final int FOOTER_TYPE = 1;
        private final int RIGHT_TYPE = 2;
        private final int LEFT_TYPE = 3;
        View headerView;
        View footerView;
        public HashMap<Integer,List<HistoryClass>> get_historyList = historyList;
        //String head = String.format("  共%d条结果",total_count);

        public void updateSentData(HistoryClass x) {
            get_historyList.get(x.chatto).add(0, x);
            notifyDataSetChanged(); // 刷新视图
        }

        public void updateReceiveData(HistoryClass x) {
            get_historyList.get(x.from).add(0, x);
            notifyDataSetChanged(); // 刷新视图
        }


        @Override
        public MyViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == FOOTER_TYPE){
                return new MyViewHoder(footerView);
            }

            View view = getLayoutInflater().inflate(R.layout.card_msg_right,null);
            if (viewType == LEFT_TYPE){//send
                view = getLayoutInflater().inflate(R.layout.card_msg_left,null);
            }else if(viewType == RIGHT_TYPE){//receive
                view = getLayoutInflater().inflate(R.layout.card_msg_right,null);
            }
            return new MyViewHoder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHoder holder, int position) {
            Log.d("Adapter", "Binding data..." + position);
            if(position == getItemCount()-1){
                return;
            }
            HistoryClass historys = get_historyList.get(nowchatter.userid).get(position);
            if (historys.from==historys.userid){//send
                holder.meaasge_left_edit.setText(historys.message);
                holder.EditTimeLeft.setText(historys.time);
            }else{
                holder.message_right_edit.setText(historys.message);
                holder.EditTimeRight.setText(historys.time);
            }
        }


        @Override
        public int getItemCount() {
            return get_historyList.get(nowchatter.userid).size();
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

            if(position == getItemCount()-1){
                return FOOTER_TYPE;
            }
            HistoryClass historys = get_historyList.get(nowchatter.userid).get(position);
            if(historys.from==0) {

            }else if (historys.from==historys.userid){//send
                return LEFT_TYPE;
            }
            return RIGHT_TYPE;
        }

    }

    class MyViewHoder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView meaasge_left_edit;
        TextView message_right_edit;
        TextView EditTimeLeft;
        TextView EditTimeRight;

        IMyViewHolderClicks mListener;

        public MyViewHoder(View itemView, IMyViewHolderClicks Listener) {
            super(itemView);
            mListener = Listener;
            meaasge_left_edit = itemView.findViewById(R.id.message_left);
            message_right_edit = itemView.findViewById(R.id.message_right);
            EditTimeLeft = itemView.findViewById(R.id.chatTimeLeftEdit);
            EditTimeRight = itemView.findViewById(R.id.chatTimeRightEdit);
        }
        public MyViewHoder(View itemView) {
            super(itemView);
            meaasge_left_edit = itemView.findViewById(R.id.message_left);
            message_right_edit = itemView.findViewById(R.id.message_right);
            EditTimeLeft = itemView.findViewById(R.id.chatTimeLeftEdit);
            EditTimeRight = itemView.findViewById(R.id.chatTimeRightEdit);
        }
        @Override
        public void onClick(View view){
            mListener.onItemClick(view,getLayoutPosition());
        }
    }
    private interface IMyViewHolderClicks{
        public void onItemClick(View view,int ListClass);
    }
}
