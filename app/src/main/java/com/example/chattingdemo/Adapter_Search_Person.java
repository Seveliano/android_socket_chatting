package com.example.chattingdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter_Search_Person extends RecyclerView.Adapter<Adapter_Search_Person.MyViewHolder> {
    private ArrayList<Item_Contact_Person> item_contacts;
    private Context mContext;

    public Adapter_Search_Person(ArrayList<Item_Contact_Person> item_contact_persons){
        this.item_contacts = item_contact_persons;
        Picasso.with(mContext)
                .setLoggingEnabled(true);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View itemView = LayoutInflater.from(mContext)
                .inflate(R.layout.item_contact_persons, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_name.setText(item_contacts.get(position).st_name);
        String img_url = item_contacts.get(position).st_img_url;

        Picasso.with(mContext)
                .load(img_url)
                .error(mContext.getDrawable(R.mipmap.img_person))
                .into(holder.img_person);
    }

    @Override
    public int getItemCount() {
        return item_contacts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView img_person;
        public TextView tv_name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            img_person = itemView.findViewById(R.id.img_person);
            tv_name = itemView.findViewById(R.id.tv_name);
        }
    }
}
