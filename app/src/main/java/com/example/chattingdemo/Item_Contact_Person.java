package com.example.chattingdemo;

public class Item_Contact_Person {

    public String st_name;
    public String st_img_url = "";
    public int nUnreceived_count;

    public Item_Contact_Person(String name, String img_url){
        this.st_name = name;
        this.st_img_url = img_url;
        this.nUnreceived_count = 0;
    }
}
