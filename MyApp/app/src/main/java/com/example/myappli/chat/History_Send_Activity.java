package com.example.myappli.chat;

import static com.example.myappli.MainActivity.token;
import static com.example.myappli.MainActivity.websocket;
import static com.example.myappli.chat.ListActivity.historyList;
import static com.example.myappli.chat.ListActivity.nowchatter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myappli.R;

import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class History_Send_Activity extends Activity {

    EditText message;
    Button send_button;
    RecyclerView recyclerView;
    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Intent intent = getIntent();
        setContentView(R.layout.activity_chat_page);
        message=this.findViewById(R.id.MessageEdit);
        send_button = this.findViewById(R.id.SendCommitButton);
        TextView username = this.findViewById(R.id.chatMember);
        username.setText(nowchatter.username);


        showhistory();
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmsg();
                recyclerView = findViewById(R.id.chat_recycler);
                LinearLayoutManager layoutManager = new LinearLayoutManager(History_Send_Activity.this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                layoutManager.setReverseLayout(true);
                recyclerView.setLayoutManager(layoutManager);

                myAdapter = new MyAdapter();
                recyclerView.setAdapter(myAdapter);

                // View header_view = getLayoutInflater().inflate(R.layout.card_header,null);
                //  myAdapter.setHeaderView(header_view);
                View footer_view = getLayoutInflater().inflate(R.layout.card_footer_chat, null);
                myAdapter.setFooterView(footer_view);

            }
        });

    }

    private void showhistory(){
                recyclerView = findViewById(R.id.chat_recycler);
                LinearLayoutManager layoutManager = new LinearLayoutManager(History_Send_Activity.this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                layoutManager.setReverseLayout(true);
                recyclerView.setLayoutManager(layoutManager);

                myAdapter = new MyAdapter();
                recyclerView.setAdapter(myAdapter);

                // View header_view = getLayoutInflater().inflate(R.layout.card_header,null);
                //  myAdapter.setHeaderView(header_view);
                View footer_view = getLayoutInflater().inflate(R.layout.card_footer_chat, null);
                myAdapter.setFooterView(footer_view);

    }
    private void sendmsg(){
            String jsonObject = String.format("{\"from\":%s,\"to\":%s,\"msg\": \"%s\"}",
                String.valueOf(token.charAt(token.length()-1)),String.valueOf(nowchatter.userid), message.getText().toString());
        //"{\"\":1,\"to\":1,\"msg\":\"你好\"}"
        websocket.send(jsonObject);

        Date date =new Date();
        HistoryClass newsend=new HistoryClass(nowchatter.userid,nowchatter.username,token.charAt(token.length()-1)-'0', nowchatter.userid, message.getText().toString(),"2023/12/10 00:00");
        historyList.get(nowchatter.userid).add(0,newsend);//.add(j);


        Toast.makeText(History_Send_Activity.this,"发送了"+jsonObject,Toast.LENGTH_LONG).show();
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHoder> {
        private final int HEADER_TYPE =0;
        private final int FOOTER_TYPE =1;
        private final int RIGHT_TYPE =2;
        private final int LEFT_TYPE = 3;
        View headerView;
        View footerView;
        //String head = String.format("  共%d条结果",total_count);

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
            if(position == getItemCount()-1){
                return;
            }
            HistoryClass historys = historyList.get(nowchatter.userid).get(position);
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
            return historyList.get(nowchatter.userid).size()+1;
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
            HistoryClass historys = historyList.get(nowchatter.userid).get(position);
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
