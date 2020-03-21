package com.example.chattingdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.net.URISyntaxException;

import io.socket.client.IO;
import me.leolin.shortcutbadger.ShortcutBadger;

import static com.example.chattingdemo.Activity_Select_Chatting_Room.STATUS_CONNECT;
import static com.example.chattingdemo.Activity_Select_Chatting_Room.URL;
import static com.example.chattingdemo.Activity_Select_Chatting_Room.mSocket;
import static com.example.chattingdemo.Activity_Select_Chatting_Room.st_nickName;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TOPIC_GLOBAL = "global";
    private static final String SENDER_NAME = "sender_name";
    private static final String CONTACT_NAME = "contact_name";
    private static final String TITLE = "title";
    private static final String EMPTY = "";
    private static final String MESSAGE = "message";
    private static final String IMAGE = "image";
    private static final String ACTION = "action";
    private static final String DATA = "data";
    private static final String ACTION_DESTINATION = "action_destination";


    @Override
    public void onNewToken(@NonNull String firebase_idToken) {
        super.onNewToken(firebase_idToken);

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_GLOBAL);

        SharedPreferences.Editor editor = getSharedPreferences(Activity_SignUp.MY_SHARED_FIREBASE_IDTOKEN, MODE_PRIVATE).edit();
        editor.putString("firebase_idToken", firebase_idToken);
        editor.apply();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData().get(TITLE) != null){
            handleData(remoteMessage);
        }else if (remoteMessage.getNotification() != null) {
            handleNotification(remoteMessage.getNotification());
        }
    }

    private void handleData(RemoteMessage remoteMessage){
        String sender_name = remoteMessage.getData().get(SENDER_NAME);
        String contact_name = remoteMessage.getData().get(CONTACT_NAME);
        String title = remoteMessage.getData().get(TITLE);
        String message = remoteMessage.getData().get(MESSAGE);
        String iconUrl = remoteMessage.getData().get(IMAGE);
        String action = remoteMessage.getData().get(ACTION);
        String actionDestination = remoteMessage.getData().get(ACTION_DESTINATION);

        NotificationVO notificationVO = new NotificationVO();
        notificationVO.setTitle(title);
        notificationVO.setMessage(message);
        notificationVO.setIconUrl(iconUrl);
        notificationVO.setAction(action);
        notificationVO.setActionDestination(actionDestination);

        Intent resultIntent = new Intent(getApplicationContext(), Activity_Main.class);

        //-------------- initialize -------------------------------
        SharedPreferences.Editor editor = getSharedPreferences("shared_current_contact_person", Context.MODE_PRIVATE).edit();
        editor.putString("current_contact_person", sender_name);
        editor.apply();

        SharedPreferences.Editor editor1 = getSharedPreferences("shared_myName", MODE_PRIVATE).edit();
        editor1.putString("myName", contact_name);
        editor1.commit();

        st_nickName = contact_name;

        try {
            mSocket = IO.socket(URL);
            mSocket.connect();
            STATUS_CONNECT = true;
        } catch (URISyntaxException e) {}

        //=================================================================

        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.displayNofigication(notificationVO, resultIntent);
        notificationUtils.playNotificationSound();
    }

    private void handleNotification(RemoteMessage.Notification remoteMsgNotification){
        String message = remoteMsgNotification.getBody();
        String title = remoteMsgNotification.getTitle();
        NotificationVO notificationVO = new NotificationVO();
        notificationVO.setTitle(title);
        notificationVO.setMessage(message);

        Intent resultIntent = new Intent(getApplicationContext(), Activity_Main.class);
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.displayNofigication(notificationVO, resultIntent);
        notificationUtils.playNotificationSound();
    }
}
