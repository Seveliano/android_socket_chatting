package com.example.chattingdemo;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import io.socket.emitter.Emitter;


import static com.example.chattingdemo.Activity_Select_Chatting_Room.mSocket;
import static com.example.chattingdemo.Activity_Select_Chatting_Room.st_nickName;
import static com.example.chattingdemo.Activity_Select_Chatting_Room.STATUS_CONNECT;

public class Activity_Main extends Alert_Base {

    private String st_current_contact_person;

    private String JOINED_ROOM = null;
    private String SOCKET_ID = null;

    public static final int MY_MESSAGE = 0;
    public static final int OTHER_MESSAGE = 1;

    private View lin_rootView;
    private ImageView img_backToSelectRoom;
    private TextView tv_chatting_person;
    private ImageButton iBtn_sendMsg;
    private EmojiconEditText emoji_msg;
    private ImageView img_select_chatting_method, img_select_typing, img_select_emoji;
    private LinearLayout lin_select_method, lin_select_image, lin_select_audio, lin_select_video;
    private EmojIconActions emojIconActions;
    private ImageView img_send_tmpFile;

    private RecyclerView rel_message;
    private Adapter_Message adapter_message;
    private ArrayList<Item_Message> item_messages = new ArrayList<>();

    //----- camera ---------------
    private final static int IMAGE_PICK = 101;
    private String st_send_tempFile_path;
    private Uri uri_send_tmpFile = null;

    private void permissionGranted(){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 11);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantedResult){
        super.onRequestPermissionsResult(requestCode, permissions, grantedResult);

        if (grantedResult.length > 0 && grantedResult[0] == PackageManager.PERMISSION_GRANTED){
            return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("shared_current_contact_person", MODE_PRIVATE);
        st_current_contact_person = prefs.getString("current_contact_person", "your partener");

        init_viewItem();
        init_socketIO();
        event_handling();
    }

    private void init_viewItem(){
        img_backToSelectRoom = findViewById(R.id.img_backToSelectRoom);
        tv_chatting_person = findViewById(R.id.tv_chatting_person);
        tv_chatting_person.setText("Chatting With " + st_current_contact_person);

        iBtn_sendMsg = findViewById(R.id.iBtn_sendMsg);
        emoji_msg = findViewById(R.id.emoji_msg);

        rel_message = (RecyclerView) findViewById(R.id.rel_message);
        LinearLayoutManager llm = new LinearLayoutManager(this);
//        llm.setStackFromEnd(true);
        rel_message.setLayoutManager(llm);
        adapter_message = new Adapter_Message();
        rel_message.setAdapter(adapter_message);

        img_select_chatting_method = findViewById(R.id.img_select_chatting_method);
        img_select_typing =  findViewById(R.id.img_select_typing);
        img_select_emoji = findViewById(R.id.img_select_emoji);
        lin_select_method = findViewById(R.id.lin_select_method);
        lin_select_image = findViewById(R.id.lin_select_image);
        lin_select_audio = findViewById(R.id.lin_select_audio);
        lin_select_video = findViewById(R.id.lin_select_video);

        lin_rootView = findViewById(R.id.lin_rootView);
        emojIconActions = new EmojIconActions(this, lin_rootView, emoji_msg, img_select_emoji);
        emojIconActions.ShowEmojIcon();

        img_send_tmpFile = findViewById(R.id.img_sendTmp_file);
    }

    private boolean bFlag_chatting_select_mode = false;

    private void event_handling(){

        img_backToSelectRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        iBtn_sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_message();
            }
        });

        img_select_chatting_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bFlag_chatting_select_mode = !bFlag_chatting_select_mode;
                if (bFlag_chatting_select_mode){
                    lin_select_method.setVisibility(View.VISIBLE);
                }else lin_select_method.setVisibility(View.GONE);
                hideKeyboard();
            }
        });

        img_select_typing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtil.hideKeyboard(Activity_Main.this);
            }
        });

        lin_select_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionGranted();

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(intent, IMAGE_PICK);
            }
        });

        emoji_msg.setOnEditorActionListener(new EditText.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEND){
                    send_message();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == IMAGE_PICK && resultCode == RESULT_OK &&
                data != null && data.getData() != null){

            uri_send_tmpFile = data.getData();
            st_send_tempFile_path = FilePath.getPath(this, uri_send_tmpFile);
            img_send_tmpFile.setImageURI(uri_send_tmpFile);
            img_send_tmpFile.setVisibility(View.VISIBLE);
        }
    }

    private void init_socketIO(){
        mSocket.on("chatting_contact_init", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONArray arr_init_result = (JSONArray) args[0];
                            for (int i = 0; i < arr_init_result.length(); i ++){
                                JSONObject obj_init_result = arr_init_result.getJSONObject(i);
                                if (obj_init_result.getString("sender_name").trim().toLowerCase().equals(st_nickName.trim().toLowerCase())){
                                    item_messages.add(new Item_Message(obj_init_result.getString("msg_content"),
                                            obj_init_result.getString("file_image"), MY_MESSAGE));
                                }else {
                                    item_messages.add(new Item_Message(obj_init_result.getString("msg_content"),
                                            obj_init_result.getString("file_image"), OTHER_MESSAGE));
                                }
                            }
                            adapter_message = new Adapter_Message(item_messages);
                            rel_message.setAdapter(adapter_message);
                            //--------- move to down ----------------
                            rel_message.smoothScrollToPosition(rel_message.getAdapter().getItemCount() - 1);
//                            ((LinearLayoutManager) rel_message.getLayoutManager()).scrollToPositionWithOffset(rel_message.getAdapter().getItemCount() - 1, 200);

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        mSocket.on("message", onMessageReceived);
        mSocket.connect();

        try {
            JSONObject obj_send_current_contact = new JSONObject();
            obj_send_current_contact.put("sender_name", st_current_contact_person);
            obj_send_current_contact.put("contact_name", st_nickName);

            mSocket.emit("current_contact_person", obj_send_current_contact);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String st_sender_name;
                    String st_msg_content;
                    String st_img_url;

                    try {
                        st_sender_name = data.getString("sender_name");
                        st_msg_content = data.getString("msg_content");
                        st_img_url = data.getString("file_image");

                        if (st_sender_name.equals("server")){
                            JOINED_ROOM = data.getString("roomId");
                            SOCKET_ID = data.getString("socketId");
                        }

                        Item_Message item_message = new Item_Message(st_msg_content, st_img_url, OTHER_MESSAGE);
                        adapter_message.add_message(item_message);
                        rel_message.smoothScrollToPosition(rel_message.getAdapter().getItemCount() - 1);
                    } catch (Exception e) {
                        return;
                    }
                }
            });

        }
    };

    private void send_message(){
        String st_msg_content = emoji_msg.getText().toString();

        if ((st_msg_content.isEmpty() && uri_send_tmpFile == null) || !STATUS_CONNECT ) {
            alert_ok("Alert", "No message detected!");
            return;
        }

        emoji_msg.getText().clear();

        Item_Message item_message;
        if (uri_send_tmpFile == null){
            item_message = new Item_Message(st_msg_content, "Empty", MY_MESSAGE);
        }else {
            item_message = new Item_Message(st_msg_content, uri_send_tmpFile.toString(), MY_MESSAGE);
        }

        adapter_message.add_message(item_message);
        rel_message.smoothScrollToPosition(rel_message.getAdapter().getItemCount() - 1);

        byte[] bt_file_image = null;
        if (uri_send_tmpFile != null){
            try {
                bt_file_image = readBytesToFile(st_send_tempFile_path);
            } catch (IOException e) {
                e.printStackTrace();
            }

            img_send_tmpFile.setImageDrawable(null);
            img_send_tmpFile.setVisibility(View.GONE);
            uri_send_tmpFile = null;
        }

        JSONObject obj_sendMessage = new JSONObject();
        try {
            obj_sendMessage.put("sender_name", st_nickName);
            obj_sendMessage.put("contact_name", st_current_contact_person);
            obj_sendMessage.put("msg_content", st_msg_content);
            obj_sendMessage.put("file_image", bt_file_image);

            mSocket.emit("message", obj_sendMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private byte[] readBytesToFile(String st_path) throws IOException{
        File file = new File(st_path);
        byte[] fileData = new byte[(int) file.length()];
        DataInputStream dis = new DataInputStream(new FileInputStream(file));
        dis.readFully(fileData);
        dis.close();
        return fileData;
    }

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void writeBytesToFile(InputStream inputStream, File file) throws IOException{
        FileOutputStream fos = null;
        try{
            byte[] data = new byte[2048];
            int nbread = 0;
            fos = new FileOutputStream(file);
            while ((nbread = inputStream.read(data)) > -1){
                fos.write(data, 0, nbread);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (fos != null) fos.close();
        }
    }

    Emitter.Listener onFileRecived = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];

                    try {
                        byte[] data_file;
                        data_file = (byte[]) data.get("sendfile");
                        Bitmap b = BitmapFactory.decodeByteArray(data_file, 0, data_file.length);
                        img_send_tmpFile.setImageBitmap(b);
                        img_send_tmpFile.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        return;
                    }
                }
            });
        }
    };

    private void send_image(){
        String st_converted_from_image = getBase64String(st_send_tempFile_path);
        if (st_converted_from_image.isEmpty() || !STATUS_CONNECT || JOINED_ROOM == null) {
            alert_ok("Alert", "No message detected!");
            return;
        }
        String[] ss = st_send_tempFile_path.split("/");
        String img_name = ss[ss.length - 1];

        JSONObject obj_sendImage = new JSONObject();
        try {
            obj_sendImage.put("sender_name", st_nickName);
            obj_sendImage.put("st_img", st_converted_from_image);
            obj_sendImage.put("img_name", img_name);
            obj_sendImage.put("roomId", JOINED_ROOM);

            mSocket.emit("imageString", obj_sendImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Emitter.Listener onImage_message = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject obj_result = (JSONObject) args[0];

                    try {
                        String sender_name = obj_result.getString("sender_name");
                        String st_img = obj_result.getString("st_img");
                        String img_name = obj_result.getString("img_name");

                        Bitmap bitmap = decodeBase64AndSetImage(st_img);
//                        img_send.setImageBitmap(bitmap);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private String getBase64String(String path) {

        // give your image file url in mCurrentPhotoPath
        Bitmap bitmap = BitmapFactory.decodeFile(path);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // In case you want to compress your image, here it's at 40%
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private Bitmap decodeBase64AndSetImage(String completeImageData) {

        // Incase you're storing into aws or other places where we have extension stored in the starting.
        String imageDataBytes = completeImageData.substring(completeImageData.indexOf(",")+1);

        InputStream stream = new ByteArrayInputStream(Base64.decode(imageDataBytes.getBytes(), Base64.DEFAULT));

        Bitmap bitmap = BitmapFactory.decodeStream(stream);

        return bitmap;
    }
}
