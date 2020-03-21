package com.example.chattingdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter_Contact_Persons extends RecyclerView.Adapter<Adapter_Contact_Persons.MyViewHolder> {
    private ArrayList<Item_Contact_Person> item_contact_people;
    private Context mContext;

    public Adapter_Contact_Persons(ArrayList item_contact_persons){
        this.item_contact_people = item_contact_persons;
    }

    public void check_unReceived_message(String sender_name){
        int position = 0;
        for (int i = 0; i < item_contact_people.size(); i ++){
            if (item_contact_people.get(i).st_name.trim().toLowerCase().equals(sender_name.trim().toLowerCase())){
                item_contact_people.get(i).nUnreceived_count ++;
                position = i;
                break;
            }
        }
//        notifyDataSetChanged();
        notifyItemChanged(position);
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
        holder.tv_name.setText(item_contact_people.get(position).st_name);
        String img_url = item_contact_people.get(position).st_img_url;

        Picasso.with(mContext)
                .load(img_url)
                .error(mContext.getDrawable(R.mipmap.img_person))
                .into(holder.img_person);

        if (item_contact_people.get(position).nUnreceived_count == 0){
            holder.tv_unreceived_count.setVisibility(View.GONE);
        }else {
            holder.tv_unreceived_count.setText(item_contact_people.get(position).nUnreceived_count + "");
            holder.tv_unreceived_count.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return item_contact_people.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView img_person;
        public TextView tv_name;
        public TextView tv_unreceived_count;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);

            img_person = itemView.findViewById(R.id.img_person);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_unreceived_count = itemView.findViewById(R.id.tv_unreceived_count);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int posintion = getAdapterPosition();
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("shared_current_contact_person", Context.MODE_PRIVATE).edit();
                    editor.putString("current_contact_person", item_contact_people.get(posintion).st_name);
                    editor.apply();

                    item_contact_people.get(posintion).nUnreceived_count = 0;
                    notifyItemChanged(posintion);

                    Intent intent = new Intent(mContext, Activity_Main.class);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
