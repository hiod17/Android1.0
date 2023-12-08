package com.example.myappli;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.example.myappli.chat.UserList;
import com.example.myappli.search.JobClass;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myappli.databinding.ActivityMainBinding;

import org.java_websocket.client.WebSocketClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.WebSocket;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    public static String token;
    public static int total_count_of_users;
    public static List<UserList> user_list = new ArrayList<>();
    public static WebSocket websocket;
    public static HashMap<Integer,String> usermap =new HashMap<>();
    //public static List<JobClass> user_favourite = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
//    public static Map<Integer,String> usermap =new Map<Integer, String>() {
//        @Override
//        public int size() {
//            return 0;
//        }
//
//        @Override
//        public boolean isEmpty() {
//            return false;
//        }
//
//        @Override
//        public boolean containsKey(@Nullable Object key) {
//            return false;
//        }
//
//        @Override
//        public boolean containsValue(@Nullable Object value) {
//            return false;
//        }
//
//        @Nullable
//        @Override
//        public String get(@Nullable Object key) {
//            return null;
//        }
//
//        @Nullable
//        @Override
//        public String put(Integer key, String value) {
//            return null;
//        }
//
//        @Nullable
//        @Override
//        public String remove(@Nullable Object key) {
//            return null;
//        }
//
//        @Override
//        public void putAll(@NonNull Map<? extends Integer, ? extends String> m) {
//
//        }
//
//        @Override
//        public void clear() {
//
//        }
//
//        @NonNull
//        @Override
//        public Set<Integer> keySet() {
//            return null;
//        }
//
//        @NonNull
//        @Override
//        public Collection<String> values() {
//            return null;
//        }
//
//        @NonNull
//        @Override
//        public Set<Entry<Integer, String>> entrySet() {
//            return null;
//        }
//    };
}