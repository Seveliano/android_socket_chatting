package com.example.chattingdemo;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class Adapter_Message extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Item_Message> item_messages = new ArrayList<>();
    private View itemView;
    private int nMsg_type;
    private Context mContext;

    public void add_message(Item_Message message){
        this.item_messages.add(message);
        notifyDataSetChanged();
    }

    public Adapter_Message(){}

    public Adapter_Message(ArrayList<Item_Message> messages){
        this.item_messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        return item_messages.get(position).getMsg_type();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType){
            case Activity_Main.MY_MESSAGE:
                itemView = LayoutInflater.from(mContext)
                        .inflate(R.layout.my_message, parent, false);
                viewHolder = new Adapter_Message.My_Message_ViewHolder(itemView);
                break;
            case Activity_Main.OTHER_MESSAGE:
                itemView = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_message, parent, false);
                viewHolder = new Adapter_Message.Receved_Message_ViewHolder(itemView);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        String img_url_temp;
        switch (holder.getItemViewType()){
            case Activity_Main.MY_MESSAGE:
                final My_Message_ViewHolder my_message = (My_Message_ViewHolder) holder;
                my_message.tv_message.setText(item_messages.get(position).getMsg_content());

                Picasso.with(mContext).load(item_messages.get(position).getImg_url())
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                ImageView imageView = my_message.img_file;
                                int nTargetWidth = 300;

                                float ratio = (float) bitmap.getHeight() / bitmap.getWidth();
                                float height_temp = ((float) nTargetWidth) * ratio;

                                final android.view.ViewGroup.MarginLayoutParams layoutParams =
                                        (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();

                                layoutParams.height = (int) height_temp;
                                layoutParams.width = nTargetWidth;

                                imageView.setLayoutParams(layoutParams);
                                imageView.setImageBitmap(bitmap);
                                imageView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                my_message.img_file.setVisibility(View.GONE);
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
                break;
            case Activity_Main.OTHER_MESSAGE:
                final Receved_Message_ViewHolder receved_message = (Receved_Message_ViewHolder) holder;
                receved_message.tv_message.setText(item_messages.get(position).getMsg_content());

                Picasso.with(mContext).load(item_messages.get(position).getImg_url())
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                ImageView imageView = receved_message.img_file;
                                int nTargetWidth = 300;

                                float ratio = (float) bitmap.getHeight() / bitmap.getWidth();
                                float height_temp = ((float) nTargetWidth) * ratio;

                                final android.view.ViewGroup.MarginLayoutParams layoutParams =
                                        (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();

                                layoutParams.height = (int) height_temp;
                                layoutParams.width = nTargetWidth;

                                imageView.setLayoutParams(layoutParams);
                                imageView.setImageBitmap(bitmap);
                                imageView.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                receved_message.img_file.setVisibility(View.GONE);
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });

//                Picasso.with(mContext).load(item_messages.get(position).getImg_url())
//                        .into(receved_message.img_file, new com.squareup.picasso.Callback() {
//                            @Override
//                            public void onSuccess() {
//                                receved_message.img_file.setVisibility(View.VISIBLE);
//                            }
//
//                            @Override
//                            public void onError() {
//                                receved_message.img_file.setVisibility(View.GONE);
//                            }
//                        });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return item_messages.size();
    }

    public class My_Message_ViewHolder extends RecyclerView.ViewHolder {
        public EmojiconTextView tv_message;
        public ImageView img_file;

        public My_Message_ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_message = itemView.findViewById(R.id.tv_myMessage);
            img_file = itemView.findViewById(R.id.img_file);
        }
    }

    public class Receved_Message_ViewHolder extends RecyclerView.ViewHolder {
        public EmojiconTextView tv_message;
        public ImageView img_file;

        public Receved_Message_ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_message = itemView.findViewById(R.id.tv_message);
            img_file = itemView.findViewById(R.id.img_file);
        }
    }

}
