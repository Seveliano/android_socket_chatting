package com.example.chattingdemo;

public class Item_Message {

    private String st_msg_content;
    private String st_img_url = "";
    private int nMsg_type;

    public Item_Message(){

    }

    public Item_Message(String st_msg_content, String st_img_url, int nMsg_type) {
        this.st_msg_content = st_msg_content;
        this.st_img_url = st_img_url;
        this.nMsg_type = nMsg_type;
    }

    public String getMsg_content() {
        return st_msg_content;
    }

    public int getMsg_type() {
        return nMsg_type;
    }

    public void setMsg_type(int msg_type) {
        this.nMsg_type = msg_type;
    }

    public void setMsg_content(String message) {
        this.st_msg_content = message;
    }

    public String getImg_url() {
        return st_img_url;
    }

    public void setImg_url(String st_img_url) {
        this.st_img_url = st_img_url;
    }
}