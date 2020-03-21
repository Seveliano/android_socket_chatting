package com.example.chattingdemo;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Activity_SignUp extends Alert_Base implements Alert_Base.Interface_AsyncTask {

    public static final String MY_SHARED_FIREBASE_IDTOKEN = "firebase_idToken";

    private EditText et_userName, et_email, et_phoneNumber;
    private Button btn_signUp, btn_cancel;
    private ImageView img_person;

    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private Api_Register api_signUP;
    private String url_signUp = "http://192.168.2.205:3000/signUp";
    private String url_signUp_image = "http://192.168.2.205:3000/signUp_image";

    //------- modal dialog for capturing image -------
    private Dialog modal_image_capture;
    private FrameLayout frame_camera, frame_library;

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_TAKE_PHOTO = 2;

    private File storageDir;
    private Uri photoURI;
    private String currentPhotoPath;
    private String img_name;

    //-------- camera permission ----------------
    public void createStorageDirectory(){
        if (Build.VERSION.SDK_INT >= 23){
            if (!isPermissionGranted()) return;
        }
        storageDir = new File(Environment.getExternalStorageDirectory() + "/Iamges");
        if(!storageDir.exists()){
            boolean bResult = storageDir.mkdirs();
            if( bResult ){
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Fail", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isPermissionGranted(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            {
                return true;
            }else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        1);
                return false;
            }
        }else return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantedResult){
        super.onRequestPermissionsResult(requestCode, permissions, grantedResult);

        if (grantedResult.length > 0 && grantedResult[0] == PackageManager.PERMISSION_GRANTED){
            createStorageDirectory();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        view_item_init();
        createStorageDirectory();
        init_modal();
        event_handler();

    }

    private void view_item_init(){
        et_userName = findViewById(R.id.et_userName);
        et_email= findViewById(R.id.et_email);
        et_phoneNumber = findViewById(R.id.et_phoneNumber);
        btn_signUp = findViewById(R.id.btn_signUp);
        btn_cancel = findViewById(R.id.btn_cancel_search);
        img_person = findViewById(R.id.img_person);
    }

    private void init_modal(){
        modal_image_capture = new Dialog(this);
        modal_image_capture.setContentView(R.layout.modal_camera);
        modal_image_capture.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        frame_camera = modal_image_capture.findViewById(R.id.frame_camera);
        frame_library = modal_image_capture.findViewById(R.id.frame_library);
    }

    private void event_handler(){
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String st_userName = et_userName.getText().toString();
                String st_email = et_email.getText().toString();
                String st_phoneNumber = et_phoneNumber.getText().toString();

                if (st_userName.isEmpty()) alert_ok("Alert", "User name is required!");
                else if (st_email.isEmpty()) alert_ok("Alert", "Email is required!");
                else if (!st_email.trim().matches(emailPattern)) alert_ok("Alert", "Email is not correct!");
                else if (st_phoneNumber.isEmpty()) alert_ok("Alert", "PhoneNumber is required!");
                else {
                    call_Api(st_userName, st_email, st_phoneNumber);
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        img_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                modal_image_capture.show();
            }
        });

        frame_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasCamera())
                {
                    alert_ok("Alert", "There is no user camera!");
                }
                else launchCamera();
            }
        });

        frame_library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openFileChooser();
            }
        });
    }

    private boolean hasCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        img_name = "JPEG_" + timeStamp + ".jpg";

        String state = Environment.getExternalStorageState();
        File imageTmp = null;
        if (Environment.MEDIA_MOUNTED.equals(state)){
            imageTmp = new File(storageDir.getPath() + "/" + img_name);
        }

        currentPhotoPath = imageTmp.getAbsolutePath();

        return imageTmp;
    }

    private void launchCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intent.resolveActivity(getPackageManager()) != null){
            File imageFile = null;

            try{
                imageFile = createImageFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            if (imageFile != null){
                if (Build.VERSION.SDK_INT >= 23){
                    photoURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", imageFile);
                }
                else {
                    String state = Environment.getExternalStorageState();
                    if (Environment.MEDIA_MOUNTED.equals(state)){
                        photoURI = Uri.fromFile(imageFile);
                    }else photoURI = Uri.parse("content://com.example.android.fileprovider/");
                }

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
//        intent.putExtra("crop", "true");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, REQUEST_TAKE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            img_person.setImageURI(photoURI);
            modal_image_capture.hide();
        }

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK &&
                data != null && data.getData() != null){

            photoURI = data.getData();
            currentPhotoPath = FilePath.getPath(this, photoURI);
//            img_person.setImageURI(photoURI);
            Picasso.with(this).load(photoURI.toString()).into(img_person);
            modal_image_capture.hide();

            Cursor returnCursor = getContentResolver().query(photoURI, null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
//            String[] ss = currentPhotoPath.split("/");
//            String name = ss[ss.length - 1];
            img_name = returnCursor.getString(nameIndex);
            String size = Long.toString(returnCursor.getLong(sizeIndex));
        }
    }

    private void call_Api(String userName, String email, String phoneNumber){

        MultipartBody body;
        if (currentPhotoPath == null){
            body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("nickName", userName)
                    .addFormDataPart("email", email)
                    .addFormDataPart("phoneNumber", phoneNumber)
                    .build();
            api_signUP = new Api_Register(Activity_SignUp.this, body, url_signUp);
        }else {
            File imgFile = new File(currentPhotoPath);
            final MediaType MEDIA_TYPE_IMG = MediaType.parse("image/*");

            body = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("nickName", userName)
                    .addFormDataPart("email", email)
                    .addFormDataPart("phoneNumber", phoneNumber)
                    .addFormDataPart("fileToUpload", img_name, RequestBody.create(MEDIA_TYPE_IMG, imgFile))
                    .build();
            api_signUP = new Api_Register(Activity_SignUp.this, body, url_signUp_image);
        }

        api_signUP.detect_register = this;
        api_signUP.execute();
    }

    @Override
    public void get_result(String output) {
        try {
            JSONObject obj = new JSONObject(output);
            if (obj.getString("status").equals("1")) alert_ok("Alert", obj.getString("msg"));
            else alert_ok("Alert", obj.getString("msg"));
        } catch (JSONException e) {
            e.printStackTrace();
            alert_ok("Error", "Error!");
        }
    }
}
