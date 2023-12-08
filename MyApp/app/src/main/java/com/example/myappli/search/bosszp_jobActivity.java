package com.example.myappli.search;

import static com.example.myappli.MainActivity.token;
import static com.example.myappli.MainActivity.total_count_of_users;
import static com.example.myappli.MainActivity.user_list;
import static com.example.myappli.MainActivity.usermap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myappli.MainActivity;
import com.example.myappli.R;
import com.example.myappli.chat.UserList;
import com.example.myappli.login.SignUpActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class bosszp_jobActivity extends Activity {
    String strinfo,strarea;
    String intoffset,intlimit;
    private Button jobButton;
    private Button companyButton;
    private Button change58Button;
    private Button changebossButton;
    private Button favouriteButton;
    private Button changeotherButton;
    private int choose;
    private int companychoose;
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    List<JobClass> mJobsList = new ArrayList<>();

    /*@Override
    public void onFragmentInteraction(String s) {
        TextView tvMain =  findViewById(R.id.nav_home);
        tvMain.setText(s);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);//activity_search
        //HomeViewModel homeViewModel = new ViewModelProvider(this)
        //              .get(HomeViewModel.class);

        EditText area=this.findViewById(R.id.areaEdit);
        EditText info=this.findViewById(R.id.infoEdit);
        Button EnterButton = this.findViewById(R.id.EnterButton);
        jobButton=this.findViewById(R.id.jobButton);
        companyButton =this.findViewById(R.id.companyButton);
        change58Button =this.findViewById(R.id.change58button);
        changebossButton =this.findViewById(R.id.changebossButton);
        changeotherButton =this.findViewById(R.id.changeothersButton);
        favouriteButton = this.findViewById(R.id.FavouriteButton);

        okHttpGetUser();

        jobButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setEnable1(jobButton);
                Log.i("TAG","click_job");
                choose=1;
            }
        });

        companyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setEnable1(companyButton);
                choose=2;
            }
        });
        change58Button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setEnable2(change58Button);
                companychoose=2;
                strarea = area.getText().toString();
                intlimit="50";
                intoffset="0";
                OKhttpGetRandom();
            }
        });
        changebossButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setEnable2(changebossButton);
                Log.i("TAG","click_boss");
                companychoose=1;
                strarea = area.getText().toString();
                intlimit="50";
                intoffset="0";
                OKhttpGetRandom();
            }
        });
        changeotherButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setEnable2(changeotherButton);
                companychoose=3;
                strarea = area.getText().toString();
                intlimit="50";
                intoffset="0";
                OKhttpGetRandom();

            }
        });

        EnterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(info.getText())) {
                    Toast.makeText(bosszp_jobActivity.this, "请输入搜索信息", Toast.LENGTH_SHORT).show();
                }else{
                    strinfo = info.getText().toString();
                    strarea = area.getText().toString();
                    intlimit="100";
                    intoffset="0";
                    okHttpGet();

                }
            }
        });
    }
    private void setEnable1(Button btn) {
        List<Button> buttonList1=new ArrayList<>();
        if (buttonList1.size()==0){
            buttonList1.add(jobButton);
            buttonList1.add(companyButton);

        }
        for (int i = 0; i <buttonList1.size() ; i++) {
            buttonList1.get(i).setEnabled(true);
        }

        btn.setEnabled(false);
    }
    private void setEnable2(Button btn) {
        List<Button> buttonList2=new ArrayList<>();
        if(buttonList2.size()==0){
            buttonList2.add(change58Button);
            buttonList2.add(changebossButton);
            buttonList2.add(changeotherButton);
        }

        for (int i = 0; i <buttonList2.size() ; i++) {
            buttonList2.get(i).setEnabled(true);
        }
        btn.setEnabled(false);
    }
    private void okHttpGet(){
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder httpBuilder = null;
        if (choose==1&&companychoose==1){
            httpBuilder = HttpUrl.parse("http://39.105.180.206:9000/boss_data/job").newBuilder();
        }else if(choose==2&&companychoose==1){
            httpBuilder = HttpUrl.parse("http://39.105.180.206:9000/boss_data/company").newBuilder();
        }else if(choose==1&&companychoose==2){
            httpBuilder = HttpUrl.parse("http://39.105.180.206:9000/58_data/job").newBuilder();
        }else if(choose==2&&companychoose==2){
            httpBuilder = HttpUrl.parse("http://39.105.180.206:9000/58_data/company").newBuilder();
        }else if(choose==1&&companychoose==3){
            httpBuilder = HttpUrl.parse("http://39.105.180.206:9000/other_data/job").newBuilder();
        }else if(choose==2&&companychoose==3){
            httpBuilder = HttpUrl.parse("http://39.105.180.206:9000/other_data/company").newBuilder();
        }
        httpBuilder.addQueryParameter("info",strinfo);//数据分析&Limit=10;
        if(strarea!=null){
            httpBuilder.addQueryParameter("area",strarea);
        }
        if(intoffset!=null){
            httpBuilder.addQueryParameter("offset",intoffset);
        }
        if(intlimit!=null){
            httpBuilder.addQueryParameter("limit",intlimit);
        }

        //RequestBody body = RequestBody.create(null, "");

        Request request = new Request.Builder()
                .url(httpBuilder.build())
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
                    if(companychoose == 1){
                        try {
                            jsonGetbossData(responseStr,response);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (companychoose == 2) {
                        try {
                            jsonGet58Data(responseStr,response);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }else if (companychoose==3){
                        try {
                            jsonGetotherData(responseStr,response);
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                    // Do what you want to do with the response.
                } else {
                    // Request not successful
                }
            }
        });
    }

    private void OKhttpGetRandom(){
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder httpBuilder = null;
        if (companychoose==1){
            httpBuilder = HttpUrl.parse("http://39.105.180.206:9000/boss_data/random").newBuilder();
        }else if(companychoose==2){
            httpBuilder = HttpUrl.parse("http://39.105.180.206:9000/58_data/random").newBuilder();
        }else if(companychoose==3){
            httpBuilder = HttpUrl.parse("http://39.105.180.206:9000/other_data/random").newBuilder();
        }
        if(strarea!=null){
            httpBuilder.addQueryParameter("area",strarea);
        }
        if(intoffset!=null){
            httpBuilder.addQueryParameter("offset",intoffset);
        }
        if(intlimit!=null){
            httpBuilder.addQueryParameter("limit",intlimit);
        }

        //RequestBody body = RequestBody.create(null, "");

        Request request = new Request.Builder()
                .url(httpBuilder.build())
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
                    if(companychoose == 1){
                        try {
                            jsonGetbossData(responseStr,response);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (companychoose == 2) {
                        try {
                            jsonGet58Data(responseStr,response);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }else if (companychoose==3){
                        try {
                            jsonGetotherData(responseStr,response);
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                    // Do what you want to do with the response.
                } else {
                    // Request not successful
                }
            }
        });
    }

    private void okHttpGetUser(){
        Log.i("TAG","get_user_begin");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://39.105.180.206:9000/all_user")
                        .header("X-Token",token)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i("TAG","get_user_fail");
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //此方法运行在子线程中，不能在此方法中进行UI操作。
                        String jsondata = response.body().string();
                        //解析
                        try {
                            jsonJXUser(jsondata,response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        response.body().close();
                    }
                });
            }
        });


    }

    public int total_count;
    private void jsonGetbossData(String jsondata,Response response) throws JSONException {//解析JSON
        Log.i("TAG","--jiexi_Get_begin-");
        mJobsList = new ArrayList<>();
        if (jsondata != null) {
            JSONObject jsonObject = new JSONObject(jsondata);
            total_count = jsonObject.getInt("total_count");

            JSONArray jobs = jsonObject.getJSONArray("jobs");//遍历JSONArray对象，解析后放入集合中
            for (int time = 0;time < jobs.length();time++) {
                JSONObject jsonObject1 = jobs.getJSONObject(time);
                int job_id = jsonObject1.getInt("job_id");

                String company_name = jsonObject1.getString("company_name");

                JSONArray get_hr_info = jsonObject1.getJSONArray("hr_info");
                String hr_info = get_hr_info.getString(0);
                for(int i = 1;i < get_hr_info.length();i++){
                    String temp;
                    temp = hr_info;
                    String tag = get_hr_info.getString(i);
                    hr_info = temp + "  "+tag + "  ";
                }

                String job_area = jsonObject1.getString("job_area");

                JSONArray get_job_need = jsonObject1.getJSONArray("job_need");
                JSONArray get_tag_list = jsonObject1.getJSONArray("tag_list");
                String long_tag_list = get_tag_list.getString(0);
                for(int i = 1;i < get_tag_list.length();i++){
                    String temp;
                    temp = long_tag_list;
                    String tag = get_tag_list.getString(i);
                    long_tag_list = temp + "  "+tag + "  ";
                }
                for(int i = 0;i < get_job_need.length();i++){
                    String temp;
                    temp = long_tag_list;
                    String tag = get_job_need.getString(i);
                    long_tag_list = temp + "  "+tag + "  ";
                }

                String job_name = jsonObject1.getString("job_name");

                String salary = jsonObject1.getString("salary");

                JSONArray get_company_tag_list = jsonObject1.getJSONArray("company_tag_list");
                String company_tag_list = get_company_tag_list.getString(0);
                for(int i = 1;i < get_company_tag_list.length();i++){
                    String temp;
                    temp = company_tag_list;
                    String tag = get_company_tag_list.getString(i);
                    company_tag_list = temp + "  "+tag + "  ";
                }

                String job_url = jsonObject1.getString("job_url");

                boolean get_is_full = jsonObject1.getBoolean("is_full");
                boolean get_is_favor=jsonObject1.getBoolean("is_favor");
                JobClass job = new JobClass(job_id,job_name,job_area,salary,long_tag_list,
                        hr_info,company_name,company_tag_list,job_url,get_is_full,"boss直聘",get_is_favor);
                mJobsList.add(job);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView = findViewById(R.id.recyclerview);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(bosszp_jobActivity.this);
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

    private void jsonGet58Data(String jsondata,Response response) throws JSONException {//解析JSON
        Log.i("TAG","--jiexi_Get_begin-");
        mJobsList = new ArrayList<>();
        if (jsondata != null) {
            JSONObject jsonObject = new JSONObject(jsondata);
            total_count = jsonObject.getInt("total_count");

            JSONArray jobs = jsonObject.getJSONArray("jobs");//遍历JSONArray对象，解析后放入集合中
            for (int time = 0;time < jobs.length();time++) {
                JSONObject jsonObject1 = jobs.getJSONObject(time);
                int job_id = jsonObject1.getInt("job_id");

                String company_name = jsonObject1.getString("company_name");

                String create_time = jsonObject1.getString("created_at");

                String job_area = jsonObject1.getString("job_area");

                JSONArray get_job_need = jsonObject1.getJSONArray("job_need");
                String long_tag_list = get_job_need.getString(0);
                for(int i = 0;i < get_job_need.length();i++){
                    String temp;
                    temp = long_tag_list;
                    String tag = get_job_need.getString(i);
                    long_tag_list = temp + "  "+tag + "  ";
                }

                String job_name = jsonObject1.getString("job_name");

                String salary = jsonObject1.getString("salary");

                JSONArray get_company_tag_list = jsonObject1.getJSONArray("job_wel");
                String company_tag_list = get_company_tag_list.getString(0);
                for(int i = 1;i < get_company_tag_list.length();i++){
                    String temp;
                    temp = company_tag_list;
                    String tag = get_company_tag_list.getString(i);
                    company_tag_list = temp + "  "+tag + "  ";
                }

                String job_url = jsonObject1.getString("job_url");

                boolean get_is_full = jsonObject1.getBoolean("is_full");
                boolean get_is_favor=jsonObject1.getBoolean("is_favor");
                JobClass job = new JobClass(job_id,job_name,job_area,salary,long_tag_list,
                        create_time,company_name,company_tag_list,job_url,get_is_full,"58同城",get_is_favor);
                mJobsList.add(job);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView = findViewById(R.id.recyclerview);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(bosszp_jobActivity.this);
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
    private void jsonGetotherData(String jsondata,Response response) throws JSONException {//解析JSON
        Log.i("TAG","--jiexi_Get_begin-");
        mJobsList = new ArrayList<>();
        if (jsondata != null) {
            JSONObject jsonObject = new JSONObject(jsondata);
            total_count = jsonObject.getInt("total_count");

            JSONArray jobs = jsonObject.getJSONArray("jobs");//遍历JSONArray对象，解析后放入集合中
            for (int time = 0;time < jobs.length();time++) {
                JSONObject jsonObject1 = jobs.getJSONObject(time);
                int job_id = jsonObject1.getInt("job_id");
                String job_src=jsonObject1.getString("job_src");
                String company_name = jsonObject1.getString("company_name");

                String create_time = jsonObject1.getString("created_at");

                String job_area = jsonObject1.getString("job_area");

                String get_job_need = jsonObject1.getString("job_need");
                /*String long_tag_list = get_job_need.getString(0);
                for(int i = 0;i < get_job_need.length();i++){
                    String temp;
                    temp = long_tag_list;
                    String tag = get_job_need.getString(i);
                    long_tag_list = temp + "  "+tag + "  ";
                }*/

                String job_name = jsonObject1.getString("job_name");

                String salary = jsonObject1.getString("salary");

                String get_company_tag_list = jsonObject1.getString("job_desc");
                /*String company_tag_list = get_company_tag_list.getString(0);
                for(int i = 1;i < get_company_tag_list.length();i++){
                    String temp;
                    temp = company_tag_list;
                    String tag = get_company_tag_list.getString(i);
                    company_tag_list = temp + "  "+tag + "  ";
                }*/

                String job_url = jsonObject1.getString("job_url");

                boolean get_is_full = jsonObject1.getBoolean("is_full");
                boolean get_is_favor=jsonObject1.getBoolean("is_favor");
                JobClass job = new JobClass(job_id,job_name,job_area,salary,get_job_need,
                        create_time,company_name,get_company_tag_list,job_url,get_is_full,job_src,get_is_favor);
                mJobsList.add(job);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView = findViewById(R.id.recyclerview);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(bosszp_jobActivity.this);
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
    private void jsonJXUser(String jsondata,Response response) throws JSONException {//解析JSON
        Log.i("TAG", "--jiexi_Get_begin-");

        if (jsondata != null) {
            JSONObject jsonObject = new JSONObject(jsondata);
            total_count_of_users = jsonObject.getInt("total_count");

            JSONArray jobs = jsonObject.getJSONArray("users");//遍历JSONArray对象，解析后放入集合中
            for (int time = 0; time < jobs.length(); time++) {
                JSONObject jsonObject1 = jobs.getJSONObject(time);
                int user_id = jsonObject1.getInt("user_id");

                String user_name = jsonObject1.getString("user_name");
                usermap.put(user_id,user_name);
            }
        }
    }


    class MyAdapter extends RecyclerView.Adapter<MyViewHoder> {
        private final int HEADER_TYPE =1;
        private final int FOOTER_TYPE =2;
        private final int NORMAL_TYPE =4;
        View headerView;
        View footerView;
        String head = String.format("  共%d条结果",total_count);

        @Override
        public MyViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == HEADER_TYPE){
                return new MyViewHoder(headerView);
            }else if(viewType == FOOTER_TYPE){
                return new MyViewHoder(footerView);
            }
            View view = getLayoutInflater().inflate(R.layout.card_with_line,null);
            return new MyViewHoder(view, new IMyViewHolderClicks() {
                @Override
                public void onItemClick(View view, int position) {
                    Log.i("TAG","fav_success");
                    okhttpPost(view,position);//user_favourite.add(mJobsList.get(position-1));

                }
            });

        }
        public void okhttpPost(View view,int position){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    OkHttpClient Client = new OkHttpClient();
                    HttpUrl.Builder httpBuilder = null;
                    if(companychoose == 2)
                    {
                        httpBuilder = HttpUrl.parse("http://39.105.180.206:9000/58_data/favorite").newBuilder();
                    }else if (companychoose == 1){
                        httpBuilder = HttpUrl.parse("http://39.105.180.206:9000/boss_data/favorite").newBuilder();
                    }else if(companychoose==3){
                        httpBuilder = HttpUrl.parse("http://39.105.180.206:9000/other_data/favorite").newBuilder();
                    }

                    httpBuilder.addQueryParameter("id", String.valueOf(mJobsList.get(position-1).job_id));

                    RequestBody body = RequestBody.create(null, "");

                    Request request = new Request.Builder()
                            .url(httpBuilder.build())
                            .post(body)//Post请求的参数传递
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
                            favouriteButton = view.findViewById(R.id.FavouriteButton);
                            //解析
                            String words = null;
                            if (response.code() == 200) {
                                words = "已收藏";
                                favouriteButton.setText(words);
                                //favouriteButton.setEnabled(false);
                            }else{
                                Log.i("TAD","收藏失败");
                            }

                            //favouriteButton.setText(words);//jsonEmailData(jsondata,response);
                            response.body().close();
                        }
                    });
                }
            });
        }



        @Override
        public void onBindViewHolder(@NonNull MyViewHoder holder, int position) {
            if (position == 0) {
                holder.card_header.setText(String.format(head));
                return;
            }
            if (position == (getItemCount() - 1)) {
                return;
            }
            JobClass jobs = mJobsList.get(position - 1);
            holder.job_edit.setText(jobs.job_name);
            holder.salary_edit.setText(jobs.salary);
            holder.company_tag_list_edit.setText(jobs.company_tag_list);
            holder.job_tag_list_edit.setText(jobs.long_tag_list);
            holder.hr_edit.setText(jobs.hr_info);
            holder.location_edit.setText(jobs.job_area);
            holder.company_name.setText(jobs.company_name + ":");
            holder.jobsrc.setText("来源:"+jobs.job_src);
            String text = null;
            if (jobs.is_favor) {
                text = "已收藏";
                holder.favourite_Button.setText(text);
                holder.favourite_Button.setEnabled(false);
            }else{
                text = "收藏";
                holder.favourite_Button.setText(text);
            }


            holder.card_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(jobs.job_url));
                    startActivity(intent);
                }
            });
        }


        @Override
        public int getItemCount() {
            return mJobsList.size()+2;
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
        TextView job_edit;
        TextView salary_edit;
        TextView company_tag_list_edit;
        TextView job_tag_list_edit;
        TextView hr_edit;
        TextView location_edit;
        TextView company_name;
        TextView card_header;
        TextView jobsrc;
        Button favourite_Button;
        CardView card_search;
        IMyViewHolderClicks mListener;

        public MyViewHoder(View itemView, IMyViewHolderClicks Listener) {
            super(itemView);
            mListener = Listener;
            favourite_Button = itemView.findViewById(R.id.FavouriteButton);
            favourite_Button.setOnClickListener(this);
            job_edit = itemView.findViewById(R.id.job_Edit);
            salary_edit = itemView.findViewById(R.id.salaryEdit);
            company_tag_list_edit = itemView.findViewById(R.id.Compony_tag_list_edit);
            job_tag_list_edit = itemView.findViewById(R.id.job_tag_list_edit);
            hr_edit = itemView.findViewById(R.id.hr_Edit);
            location_edit = itemView.findViewById(R.id.location_Edit);
            jobsrc = itemView.findViewById(R.id.jobsrc_Edit);
            company_name = itemView.findViewById(R.id.Compony_name_edit);
            card_header = itemView.findViewById(R.id.cardHeader);
            card_search = itemView.findViewById(R.id.card_search);
        }
        public MyViewHoder(View itemView) {
            super(itemView);
            job_edit = itemView.findViewById(R.id.job_Edit);
            salary_edit = itemView.findViewById(R.id.salaryEdit);
            company_tag_list_edit = itemView.findViewById(R.id.Compony_tag_list_edit);
            job_tag_list_edit = itemView.findViewById(R.id.job_tag_list_edit);
            hr_edit = itemView.findViewById(R.id.hr_Edit);
            location_edit = itemView.findViewById(R.id.location_Edit);
            jobsrc = itemView.findViewById(R.id.jobsrc_Edit);
            company_name = itemView.findViewById(R.id.Compony_name_edit);
            card_header = itemView.findViewById(R.id.cardHeader);
        }
        @Override
        public void onClick(View view){
            mListener.onItemClick(view,getLayoutPosition());
        }
    }
    private interface IMyViewHolderClicks{
        public void onItemClick(View view,int jobClass);
    }



}
