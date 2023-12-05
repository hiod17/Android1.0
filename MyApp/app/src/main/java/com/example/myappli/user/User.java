package com.example.myappli.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myappli.R;
import com.example.myappli.login.LoginActivity;
import com.example.myappli.login.SignUpActivity;

public class User extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        //EditText area = this.findViewById(R.id.areaEdit);
        Button changeuser =this.findViewById(R.id.userManage);
        Button favorite =this.findViewById(R.id.favorite_button);
        Button reminder=this.findViewById(R.id.reminder_button);

        changeuser.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(User.this, ChangePwd_LogoutActivity.class);
                        startActivity(intent);
                    }
                }
        );
        favorite.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(User.this, FavoritejobActivity.class);
                        startActivity(intent);
                    }
                }
        );
        reminder.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(User.this, ReminderActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }

}
