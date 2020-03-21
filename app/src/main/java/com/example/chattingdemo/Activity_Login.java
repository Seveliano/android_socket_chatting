package com.example.chattingdemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;

import static com.example.chattingdemo.Activity_SignUp.MY_SHARED_FIREBASE_IDTOKEN;

public class Activity_Login extends Alert_Base implements Alert_Base.Interface_AsyncTask {

    private TextInputLayout til_nickName, til_email;
    private Button btn_login;
    private TextView tv_login_signUp;

    private String st_login_name, st_login_email;

    private Api_Register api_login;
    private String url_login = "http://192.168.2.205:3000/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        view_item_init();

        event_handler();

    }

    public void view_item_init(){
        til_nickName = findViewById(R.id.til_nickName);
        til_email = findViewById(R.id.til_email);
        btn_login =  findViewById(R.id.btn_login);
        tv_login_signUp = findViewById(R.id.tv_login_signUp);
    }

    public void event_handler(){

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                st_login_name = til_nickName.getEditText().getText().toString();
                st_login_email = til_email.getEditText().getText().toString();

                if (st_login_name.isEmpty()) alert_ok("Alert", "Require name!");
                else if (st_login_email.isEmpty()) alert_ok("Alert", "Require password!");
                else {
                    call_api(st_login_name, st_login_email);
                }

            }
        });

        tv_login_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Activity_SignUp.class);
                startActivity(intent);
            }
        });
    }

    private void call_api(String name, String email){
        SharedPreferences preferences = getSharedPreferences(MY_SHARED_FIREBASE_IDTOKEN, MODE_PRIVATE);
        String firebase_idToken = preferences.getString("firebase_idToken", null);

        FormBody body = new FormBody.Builder()
                .add("nickName", name)
                .add("email", email)
                .add("firebase_idToken", firebase_idToken)
                .build();
        api_login = new Api_Register(this, body, url_login);
        api_login.detect_register = this;
        api_login.execute();
    }

    @Override
    public void get_result(String output) {
        try {
            JSONObject obj_result = new JSONObject(output);

            if (obj_result.getString("status").equals("1")){
                //----- save name -----------------
                SharedPreferences.Editor editor = getSharedPreferences("shared_myName", MODE_PRIVATE).edit();
                editor.putString("myName", st_login_name);
                editor.commit();

                Intent intent = new Intent(getApplicationContext(), Activity_Select_Chatting_Room.class);
                startActivity(intent);
                finish();
            }else alert_ok("Alert", obj_result.getString("msg"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
