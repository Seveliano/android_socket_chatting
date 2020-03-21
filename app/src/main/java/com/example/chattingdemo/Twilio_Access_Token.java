package com.example.chattingdemo;

import android.content.Context;
import android.provider.Settings;

import com.google.android.gms.tasks.TaskCompletionSource;
import com.twilio.chat.Channel;
import com.twilio.chat.ChatClient;

import java.net.URISyntaxException;

public class Twilio_Access_Token {
    //------------ twilio service --------------------
    final static String SERVER_TOKEN_URL = "https://camel-dogfish-6498.twil.io/video-token?identity=user";
    final static String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImN0eSI6InR3aWxpby1mcGE7dj0xIn0.eyJqdGkiOiJTSzJjYTUyNmIxM2Q0ODQwZjQ0NThhZGZhNzQ5OWFkZmI3LTE1Njg0NDc1MDUiLCJncmFudHMiOnsiaWRlbnRpdHkiOiJ1c2VyIiwidmlkZW8iOnsicm9vbSI6IkRhaWx5U3RhbmR1cCJ9fSwiaWF0IjoxNTY4NDQ3NTA1LCJleHAiOjE1Njg0NTExMDUsImlzcyI6IlNLMmNhNTI2YjEzZDQ4NDBmNDQ1OGFkZmE3NDk5YWRmYjciLCJzdWIiOiJBQzI0ZmYzYWExMWUxMTExZTFiM2ZlNzZmNTQ4NDNmODYwIn0.Wc4K1ihAHceWiOBuuzXql5AIhRJ4fSh2_iuLXcPj9Yc";
    private ChatClient twilio_client;
    private Channel twilio_channel;
    private String identity = "CHAT_USER";
    private String room = "DailyStandup";


    private Context context;

    public Twilio_Access_Token(Context context){
        this.context = context;
    }

    private void retrieveAccessTokenfromServer(){
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

//        String tokenURL = SERVER_TOKEN_URL + "?device=" + deviceId + "&identity=" + identity;
    }

    public static void main(String[] args) throws URISyntaxException{

    }
}
