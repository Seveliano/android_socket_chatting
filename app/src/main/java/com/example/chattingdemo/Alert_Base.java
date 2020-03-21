package com.example.chattingdemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Alert_Base extends AppCompatActivity {

    public void alert_ok(String title, String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

// 참고: custom alert dialoge 만들기
//      LayoutInflater inflater = getActivity().getLayoutInflater();
//      View view = inflater.inflate(R.layout.dialog_layout, null)
//      alertDialogBuilder.setView(view);

//      context도 parameter 로 넘겨주면 아무데서나 출력할수 있다.

        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setTitle(title);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


/*===========================================================
---------------------- Api Connecting -----------------------
==============================================================*/

    //--------- Setting Interface ----------------
    public interface Interface_AsyncTask{
        void get_result(String output);
    }

    //--------- Api Connec class ----------------
    public static class Api_Register extends AsyncTask<Integer, Integer, String>
    {
        //----- interface variable -------
        public Interface_AsyncTask detect_register = null;

        private ProgressDialog progressDialog;
        private OkHttpClient httpClient = new OkHttpClient();

//        OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(60, TimeUnit.SECONDS)
//                .readTimeout(60, TimeUnit.SECONDS)
//                .writeTimeout(60, TimeUnit.SECONDS)
//                .build();

        private Context context;
        private RequestBody requestBody;
        private String url;

        public Api_Register(Context context, RequestBody requestBody, String url)
        {
            this.context = context;
            this.requestBody = requestBody;
            this.url = url;
        }

        @Override
        protected String doInBackground(Integer... integers) {

            String result = "";
            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            try {
                Response response = httpClient.newCall(request).execute();
                ResponseBody responseBody = response.body();
                result = responseBody.string();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if(progressDialog.isShowing()){

                progressDialog.dismiss();
            }

            detect_register.get_result(result);
        }

        @Override
        protected void onPreExecute()
        {
            progressDialog = progressDialog.show(context, "Alert", "Api connecting");
        }
    }

    //===========================================================================================//

    //--------- Setting Interface ----------------
    public interface Interface_getDB{
        void getDB_result(String output);
    }

    //----getdb------------------------------
    public static class Api_getDB extends AsyncTask<Integer, Integer,String>
    {
        //----- interface variable -------
        public Interface_getDB detect_getDB = null;

        private ProgressDialog progressDialog;
        private OkHttpClient httpClient = new OkHttpClient();

        private Context context;
        private String url;

        public Api_getDB(Context context, String url)
        {
            this.context = context;
            this.url = url;
        }

        @Override
        protected String doInBackground(Integer... integers) {

            String result = "";
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = httpClient.newCall(request).execute();
                ResponseBody responseBody = response.body();
                result = responseBody.string();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            if (progressDialog.isShowing())
            {
                progressDialog.dismiss();
            }

            detect_getDB.getDB_result(result);
        }

        @Override
        protected void onPreExecute()
        {
            progressDialog = progressDialog.show(context, "Alert", "Loading...");
        }
    }
}
