package com.example.myappli.user;

import static com.example.myappli.MainActivity.token;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
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
import com.example.myappli.search.JobClass;
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

public class FavoritejobActivity extends Activity {
    private Button company58Button;
    private Button companybossButton;
    public int companyButton = 0;
    public ArrayList<JobClass> user_favourite = new ArrayList<>();
    public int total_count;
    RecyclerView recyclerView;
    FavoritejobActivity.MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        company58Button = this.findViewById(R.id.favourite58button);
        companybossButton = this.findViewById(R.id.favouritebossButton);

        company58Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEnable(company58Button);
                companyButton = 2;
                okhttpGet58();
            }
        });

        companybossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEnable(companybossButton);
                companyButton = 1;
                okhttpGetBoss();
            }
        });
    }

    private void setEnable(Button btn) {
        List<Button> buttonList2 = new ArrayList<>();
        if (buttonList2.size() == 0) {
            buttonList2.add(company58Button);
            buttonList2.add(companybossButton);
        }

        for (int i = 0; i < buttonList2.size(); i++) {
            buttonList2.get(i).setEnabled(true);
        }
        btn.setEnabled(false);
    }

    private void okhttpGet58() {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder httpBuilder = HttpUrl.parse("http://39.105.180.206:9000/58_data/favorite").newBuilder();

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .get()
                .header("X-Token", token)//"336fe8bd-d08d-458f-84dc-94c6801a007a@1"
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
                        jsonGet58Data(responseStr, response);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    // Request not successful
                }
            }
        });
    }
    private void okhttpGetBoss() {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder httpBuilder = HttpUrl.parse("http://39.105.180.206:9000/boss_data/favorite").newBuilder();

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .get()
                .header("X-Token", token)//"336fe8bd-d08d-458f-84dc-94c6801a007a@1"
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
                        jsonGetbossData(responseStr, response);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    // Request not successful
                }
            }
        });
    }

    private void jsonGet58Data(String jsondata, Response response) throws JSONException {//解析JSON
        Log.i("TAG", "--jiexi_Get_begin-");
        user_favourite = new ArrayList<>();
        if (jsondata != null) {
            JSONObject jsonObject = new JSONObject(jsondata);
            total_count = jsonObject.getInt("total_count");

            JSONArray jobs = jsonObject.getJSONArray("jobs");//遍历JSONArray对象，解析后放入集合中
            for (int time = 0; time < jobs.length(); time++) {
                JSONObject jsonObject1 = jobs.getJSONObject(time);
                int job_id = jsonObject1.getInt("job_id");

                String company_name = jsonObject1.getString("company_name");

                String create_time = jsonObject1.getString("created_at");

                String job_area = jsonObject1.getString("job_area");

                JSONArray get_job_need = jsonObject1.getJSONArray("job_need");
                String long_tag_list = get_job_need.getString(0);
                for (int i = 0; i < get_job_need.length(); i++) {
                    String temp;
                    temp = long_tag_list;
                    String tag = get_job_need.getString(i);
                    long_tag_list = temp + "  " + tag + "  ";
                }

                String job_name = jsonObject1.getString("job_name");

                String salary = jsonObject1.getString("salary");

                JSONArray get_company_tag_list = jsonObject1.getJSONArray("job_wel");
                String company_tag_list = get_company_tag_list.getString(0);
                for (int i = 1; i < get_company_tag_list.length(); i++) {
                    String temp;
                    temp = company_tag_list;
                    String tag = get_company_tag_list.getString(i);
                    company_tag_list = temp + "  " + tag + "  ";
                }

                String job_url = jsonObject1.getString("job_url");
                boolean get_is_full = jsonObject1.getBoolean("is_full");

                JobClass job = new JobClass(job_id, job_name, job_area, salary, long_tag_list,
                        create_time, company_name, company_tag_list, job_url,get_is_full);
                user_favourite.add(job);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView = findViewById(R.id.recyclerview);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(FavoritejobActivity.this);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);

                    myAdapter = new FavoritejobActivity.MyAdapter();
                    recyclerView.setAdapter(myAdapter);
                    View header_view = getLayoutInflater().inflate(R.layout.card_header, null);
                    myAdapter.setHeaderView(header_view);
                    View footer_view = getLayoutInflater().inflate(R.layout.card_footer, null);
                    myAdapter.setFooterView(footer_view);
                }
            });
        }
    }

    private void jsonGetbossData(String jsondata,Response response) throws JSONException {//解析JSON
        Log.i("TAG","--jiexi_Get_begin-");
        user_favourite = new ArrayList<>();
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

                JobClass job = new JobClass(job_id,job_name,job_area,salary,long_tag_list,
                        hr_info,company_name,company_tag_list,job_url,get_is_full);
                user_favourite.add(job);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    recyclerView = findViewById(R.id.recyclerview);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(FavoritejobActivity.this);
                    layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    recyclerView.setLayoutManager(layoutManager);

                    myAdapter = new FavoritejobActivity.MyAdapter();
                    recyclerView.setAdapter(myAdapter);

                    View header_view = getLayoutInflater().inflate(R.layout.card_header,null);
                    myAdapter.setHeaderView(header_view);
                    View footer_view = getLayoutInflater().inflate(R.layout.card_footer,null);
                    myAdapter.setFooterView(footer_view);
                }
            });
        }
    }

    class MyAdapter extends RecyclerView.Adapter<FavoritejobActivity.MyViewHoder> {
        private final int HEADER_TYPE = 1;
        private final int FOOTER_TYPE = 2;
        private final int NORMAL_TYPE = 4;
        View headerView;
        View footerView;
        String head = String.format("  共%d条结果", total_count);

        @Override
        public FavoritejobActivity.MyViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == HEADER_TYPE) {
                return new FavoritejobActivity.MyViewHoder(headerView);
            } else if (viewType == FOOTER_TYPE) {
                return new FavoritejobActivity.MyViewHoder(footerView);
            }
            View view = getLayoutInflater().inflate(R.layout.card_favourite, null);
            return new FavoritejobActivity.MyViewHoder(view, new FavoritejobActivity.IMyViewHolderClicks() {
                @Override
                public void onItemClick(View view, int position) {
                    Log.i("TAG", "delete_success");
                    okhttpDelete(view, position);//user_favourite.add(mJobsList.get(position-1));
                    user_favourite.remove(user_favourite.get(position-1));
                    total_count = user_favourite.size();
                    myAdapter = new FavoritejobActivity.MyAdapter();
                    recyclerView.setAdapter(myAdapter);

                    View header_view = getLayoutInflater().inflate(R.layout.card_header,null);
                    myAdapter.setHeaderView(header_view);
                    View footer_view = getLayoutInflater().inflate(R.layout.card_footer,null);
                    myAdapter.setFooterView(footer_view);
                }
            });

        }

        public void okhttpDelete(View view, int position) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    OkHttpClient Client = new OkHttpClient();
                    HttpUrl.Builder httpBuilder = null;
                    if(companyButton == 1){
                        httpBuilder = HttpUrl.parse("http://39.105.180.206:9000/boss_data/favorite").newBuilder();
                    } else if (companyButton == 2) {
                        httpBuilder = HttpUrl.parse("http://39.105.180.206:9000/58_data/favorite").newBuilder();
                    }
                    httpBuilder.addQueryParameter("id", String.valueOf(user_favourite.get(position-1).job_id));

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
                                Toast.makeText(FavoritejobActivity.this,"删除成功，请刷新", Toast.LENGTH_LONG).show();
                                Looper.loop();
                            }else{
                                words="失败";
                            }
                            Button delete = view.findViewById(R.id.DeleteButton);
                            delete.setText(words);//jsonEmailData(jsondata,response);
                            response.body().close();
                        }
                    });
                }
            });
        }

        @Override
        public void onBindViewHolder(@NonNull FavoritejobActivity.MyViewHoder holder, int position) {
            if (position == 0) {
                holder.card_header.setText(String.format(head));
                return;
            }
            if (position == (getItemCount() - 1)) {
                return;
            }
            JobClass jobs = user_favourite.get(position - 1);
            holder.job_edit.setText(jobs.job_name);
            holder.salary_edit.setText(jobs.salary);
            holder.company_tag_list_edit.setText(jobs.company_tag_list);
            holder.job_tag_list_edit.setText(jobs.long_tag_list);
            holder.hr_edit.setText(jobs.hr_info);
            holder.location_edit.setText(jobs.job_area);
            holder.company_name.setText(jobs.company_name + ":");
            String strisfull;
            if (jobs.is_full) strisfull="已满";
            else strisfull="未满";
            holder.full_edit.setText(strisfull);
        }


        @Override
        public int getItemCount() {
            return user_favourite.size() + 2;
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
        public int getItemViewType(int position) {
            if (position == 0) {
                return HEADER_TYPE;
            }
            if (position == getItemCount() - 1) {
                return FOOTER_TYPE;
            }
            return NORMAL_TYPE;
        }
    }

    class MyViewHoder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView job_edit;
        TextView salary_edit;
        TextView company_tag_list_edit;
        TextView job_tag_list_edit;
        TextView hr_edit;
        TextView location_edit;
        TextView company_name;
        TextView card_header;
        TextView full_edit;
        Button delete_Button;
        FavoritejobActivity.IMyViewHolderClicks mListener;

        public MyViewHoder(View itemView, FavoritejobActivity.IMyViewHolderClicks Listener) {
            super(itemView);
            mListener = Listener;
            delete_Button = itemView.findViewById(R.id.DeleteButton);
            delete_Button.setOnClickListener(this);
            full_edit = itemView.findViewById(R.id.fullEdit);
            job_edit = itemView.findViewById(R.id.job_Edit);
            salary_edit = itemView.findViewById(R.id.salaryEdit);
            company_tag_list_edit = itemView.findViewById(R.id.Compony_tag_list_edit);
            job_tag_list_edit = itemView.findViewById(R.id.job_tag_list_edit);
            hr_edit = itemView.findViewById(R.id.hr_Edit);
            location_edit = itemView.findViewById(R.id.location_Edit);
            company_name = itemView.findViewById(R.id.Compony_name_edit);
            card_header = itemView.findViewById(R.id.cardHeader);
        }

        public MyViewHoder(View itemView) {
            super(itemView);
            job_edit = itemView.findViewById(R.id.job_Edit);
            full_edit = itemView.findViewById(R.id.fullEdit);
            salary_edit = itemView.findViewById(R.id.salaryEdit);
            company_tag_list_edit = itemView.findViewById(R.id.Compony_tag_list_edit);
            job_tag_list_edit = itemView.findViewById(R.id.job_tag_list_edit);
            hr_edit = itemView.findViewById(R.id.hr_Edit);
            location_edit = itemView.findViewById(R.id.location_Edit);
            company_name = itemView.findViewById(R.id.Compony_name_edit);
            card_header = itemView.findViewById(R.id.cardHeader);
        }

        @Override
        public void onClick(View view) {
            mListener.onItemClick(view, getLayoutPosition());
        }
    }

    private interface IMyViewHolderClicks {
        public void onItemClick(View view, int jobClass);
    }
}
