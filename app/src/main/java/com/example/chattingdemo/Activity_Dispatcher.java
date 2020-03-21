package com.example.chattingdemo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class Activity_Dispatcher extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Class<?> activityClass;
        try {
            SharedPreferences prefs = getSharedPreferences("shared_activity", MODE_PRIVATE);
            activityClass = Class.forName(prefs.getString("lastActivity", Activity_Login.class.getName()));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            activityClass = Activity_Login.class;
        }

        startActivity(new Intent(this, activityClass));
    }
}
