package com.example.chattingdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Activity_Select_Chatting_Room extends Alert_Base {

    public static String st_nickName;
    public static final String URL = "http://192.168.2.205:3000/";
    public static boolean STATUS_CONNECT = false;

    private Button btn_search_new, btn_cancel_search;
    private LinearLayout lin_search;
    private EditText et_searchText;
    private RecyclerView rel_search_result, rel_contact_persons;

    private ArrayList<Item_Contact_Person> item_contact_people = new ArrayList<>();
    private ArrayList<Item_Contact_Person> item_searchs = new ArrayList<>();
    private Adapter_Contact_Persons adapter_contact_persons, adapter_search;

    public static Socket mSocket;
    {
        try {
            mSocket = IO.socket(URL);
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("connectStatus", STATUS_CONNECT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_chatting_room);

        if(savedInstanceState != null){
            STATUS_CONNECT = savedInstanceState.getBoolean("connectStatus");
        }

        SharedPreferences prefs = getSharedPreferences("shared_myName", MODE_PRIVATE);
        st_nickName = prefs.getString("myName", "Temp_name");

        view_item_init();

        init_socket();

        event_handling();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void view_item_init(){
        btn_search_new = findViewById(R.id.btn_search_new);
        lin_search = findViewById(R.id.lin_search);
        btn_cancel_search = findViewById(R.id.btn_cancel_search);
        et_searchText = findViewById(R.id.et_searchText);

        rel_contact_persons = findViewById(R.id.rel_contact_persons);
        LinearLayoutManager llm_contact_person = new LinearLayoutManager(this);
        rel_contact_persons.setLayoutManager(llm_contact_person);

        rel_search_result = findViewById(R.id.rel_search_result);
        LinearLayoutManager llm_search_result = new LinearLayoutManager(this);
        rel_search_result.setLayoutManager(llm_search_result);
    }

    private void event_handling(){
        btn_search_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lin_search.setVisibility(View.VISIBLE);
                btn_search_new.setVisibility(View.GONE);
                rel_contact_persons.setVisibility(View.GONE);
                rel_search_result.setVisibility(View.VISIBLE);
            }
        });

        btn_cancel_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lin_search.setVisibility(View.GONE);
                btn_search_new.setVisibility(View.VISIBLE);
                rel_contact_persons.setVisibility(View.VISIBLE);
                rel_search_result.setVisibility(View.GONE);

                et_searchText.setText("");
                item_searchs.clear();
                adapter_search = new Adapter_Contact_Persons(item_searchs);
                rel_search_result.setAdapter(adapter_search);
            }
        });

        et_searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().isEmpty()) {
                    item_searchs.clear();
                    adapter_search = new Adapter_Contact_Persons(item_searchs);
                    rel_search_result.setAdapter(adapter_search);
                }else {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("keyword", charSequence.toString());
                        mSocket.emit("search_rooms", object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void init_socket(){
        if (!STATUS_CONNECT) {
            mSocket.on(Socket.EVENT_CONNECT, onConnect)
                    .on("contact_person", onContact_person)
                    .on("search_result", onSearch_result);
            mSocket.on("message", onMessageReceived);
//            mSocket.on(Socket.EVENT_DISCONNECT, onDisConnect)
//                    .on(Socket.EVENT_CONNECT_ERROR, onConnectError)
//                    .on(Socket.EVENT_CONNECT_TIMEOUT, onConnectTimeOut);

            mSocket.connect();
        }
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject obj_sendNickName = new JSONObject();
                        obj_sendNickName.put("nickName", st_nickName);
                        mSocket.emit("enter_chatting_center", obj_sendNickName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    STATUS_CONNECT = true;
                }
            });
        }
    };

    private final Emitter.Listener onContact_person = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    item_contact_people.clear();
                    JSONArray arr_contact_persons = (JSONArray) args[0];

                    for (int i = 0; i < arr_contact_persons.length(); i++) {
                        try {
                            JSONObject obj_contact_person = arr_contact_persons.getJSONObject(i);
                            item_contact_people.add(new Item_Contact_Person(obj_contact_person.getString("nickName"),
                                    obj_contact_person.getString("img_url")));
                            update_recyclerView_contact_persons();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    };

    private void update_recyclerView_contact_persons(){
        adapter_contact_persons = new Adapter_Contact_Persons(item_contact_people);
        rel_contact_persons.setAdapter(adapter_contact_persons);
    }

    private Emitter.Listener onSearch_result = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        item_searchs.clear();
                        JSONArray arr_search_result = (JSONArray) args[0];

                        for (int i = 0; i < arr_search_result.length(); i ++) {
                            JSONObject obj_search_result = arr_search_result.getJSONObject(i);
                            item_searchs.add(new Item_Contact_Person(obj_search_result.getString("nickName"),
                                    obj_search_result.getString("img_url")));
                        }

                        adapter_search = new Adapter_Contact_Persons(item_searchs);
                        rel_search_result.setAdapter(adapter_search);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String sender_name;
                    String message;
                    int ntemp = 0;
                    try {
                        sender_name = data.getString("sender_name");
//                        for (int i = 0; i < item_contact_people.size(); i ++){
//                            if (item_contact_people.get(i).st_name.trim().toLowerCase().equals(sender_name.trim().toLowerCase())){
//                                ntemp = i;
//                                int n = item_contact_people.get(i).nUnreceived_count;
//                                item_contact_people.get(i).nUnreceived_count = n + 1;
//                                break;
//                            }
//                        }
//                        update_recyclerView_contact_persons();
                        adapter_contact_persons.check_unReceived_message(sender_name);
                    } catch (Exception e) {
                        return;
                    }
                }
            });

        }
    };

    private Emitter.Listener onDisConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    STATUS_CONNECT = false;
                    alert_ok("Error", "disconnect!");
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    STATUS_CONNECT = false;
                    alert_ok("Error", "Connect Error!");
                }
            });
        }
    };

    private Emitter.Listener onConnectTimeOut = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    STATUS_CONNECT = false;
                    alert_ok("Error", "Time out!");
                }
            });
        }
    };

//    @Override
//    protected void onPause(){
//        super.onPause();
//
////        SharedPreferences prefs = getSharedPreferences("shared_activity", MODE_PRIVATE);
////        SharedPreferences.Editor editor = prefs.edit();
////        editor.putString("lastActivity", getClass().getName());
////        editor.commit();
//
////        mSocket.disconnect();
//    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        mSocket.disconnect();
//    }
}
