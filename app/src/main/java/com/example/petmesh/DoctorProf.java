package com.example.petmesh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.petmesh.customlist.CustomListVaccinationform;
import com.example.petmesh.datahelpers.Api;
import com.example.petmesh.datahelpers.Keystore;
import com.example.petmesh.datahelpers.RequestHandler;
import com.example.petmesh.datahelpers.Vaccination;
import com.example.petmesh.ui.dashboard.DashboardFragment;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class DoctorProf extends AppCompatActivity {
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private EditText Firstname,Middlename,Lastname,Mobileno,Phoneno,Emailaddress,Username,Password,PRCID;
    private Button btnUpdate;
    Keystore store;
    Bitmap bitmap;

    ImageView imagedocid;
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.arg1 == 1)
                Toast.makeText(getApplicationContext(),"Network error. Please check your internet and try again.", Toast.LENGTH_LONG).show();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_prof);
        store=Keystore.getInstance(this);
        Firstname=findViewById(R.id.txtfirstname);
        Middlename=findViewById(R.id.txtmiddlename);
        Lastname=findViewById(R.id.txtlastname);
        Mobileno=findViewById(R.id.txtmobilenumber);
        Phoneno=findViewById(R.id.txtphonenumber);
        Emailaddress=findViewById(R.id.txtemailaddress);
        Username=findViewById(R.id.txtUsername);
        Password=findViewById(R.id.txtPassword);
        btnUpdate=findViewById(R.id.btnUpdate);
       // PRCID=findViewById(R.id.txtprcid);
        imagedocid=findViewById(R.id.imagedocid);

        Firstname.setText(store.get("uFirstname"));
        Middlename.setText(store.get("uMiddlename"));
        Lastname.setText(store.get("uLastname"));
        Mobileno.setText(store.get("uMobileno"));
        Phoneno.setText(store.get("uPhoneno"));
        Emailaddress.setText(store.get("uEmailaddress"));
        Username.setText(store.get("uUsername"));
        Password.setText(store.get("uPassword"));
        //PRCID.setText(store.get("uVetId"));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(!store.get("uImageData").equals("2") && !store.get("uImageData").equals(""))
        {
            byte[] decodedString = Base64.decode(store.get("uImageData"), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imagedocid.setImageBitmap(decodedByte);
            bitmap=decodedByte;
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if it is updating
                Updatedoctor();
            }
        });

        imagedocid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(DoctorProf.this)
                        .cropSquare()
                        .compress(1024)
                        .maxResultSize(512, 512)
                        .start();
            }
        });


    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Keystore store=Keystore.getInstance(this);

        return true;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            if(data!=null) {
                Uri resultUri = data.getData();
                if (resultUri != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                        imagedocid.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void Updatedoctor() {

        if (TextUtils.isEmpty(Username.getText().toString().trim())) {
            Username.setError("Please enter username");
            Username.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(Password.getText().toString().trim())) {
            Password.setError("Please enter password");
            Password.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(Firstname.getText().toString().trim())) {
            Firstname.setError("Please enter password");
            Firstname.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(Middlename.getText().toString().trim())) {
            Middlename.setError("Please enter password");
            Middlename.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(Lastname.getText().toString().trim())) {
            Lastname.setError("Please enter password");
            Lastname.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(Mobileno.getText().toString().trim())) {
            Mobileno.setError("Please enter password");
            Mobileno.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(Emailaddress.getText().toString().trim())) {
            Emailaddress.setError("Please enter password");
            Emailaddress.requestFocus();
            return;
        }
/*
        if (TextUtils.isEmpty(PRCID.getText().toString().trim())) {
            PRCID.setError("Please enter password");
            PRCID.requestFocus();
            return;
        }*/
        String uploadImage="2";
        if(bitmap!=null){
            uploadImage = getStringImage(bitmap);}
        //if validation passes
        HashMap<String, String> params = new HashMap<>();
        params.put("Id",store.get("uId"));
        params.put("Username", Username.getText().toString());
        params.put("Password", Password.getText().toString());
        params.put("Firstname", Firstname.getText().toString());
        params.put("Middlename", Middlename.getText().toString());
        params.put("Lastname", Lastname.getText().toString());
        params.put("Mobileno", Mobileno.getText().toString());
        params.put("Phoneno", Phoneno.getText().toString());
        params.put("Emailaddress", Emailaddress.getText().toString());
        params.put("PRCandValidity","0");// PRCID.getText().toString()
        params.put("ImageData",  uploadImage);
        params.put("Typec",  "2");
        //Calling the create hero API

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_UPDATE_ACCOUNT, params, CODE_POST_REQUEST, DoctorProf.this);
        request.execute();

    }


    public class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

        public boolean connection;
        private static final int CODE_GET_REQUEST = 1024;
        private static final int CODE_POST_REQUEST = 1025;

        //the url where we need to send the request
        String url;

        //the parameters
        HashMap<String, String> params;

        //the request code to define whether it is a GET or POST
        int requestCode;
        Context context;
        //constructor to initialize values
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode,Context context) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
            this.context=context;
        }

        //when the task started displaying a progressbar
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        //this method will give the response from the request
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if(this.connection){
                    JSONObject object = new JSONObject(s);
                    if (!object.getBoolean("error")) {

                        if(object.getString("message").equals("Account updated successfully"))
                        {
                            Keystore store=Keystore.getInstance(context);
                            //store.put("uId",params.get("Id"));
                            store.put("Id",params.get("Id"));
                            store.put("Firstname",params.get("Firstname"));
                            store.put("Middlename",params.get("Middlename"));
                            store.put("Lastname",params.get("Lastname"));
                            store.put("Mobileno",params.get("Mobileno"));
                            store.put("Phoneno",params.get("Phoneno"));
                            store.put("Emailaddress",params.get("Emailaddress"));
                            store.put("PRCandValidity",params.get("PRCandValidity"));
                            store.put("ImageData",params.get("ImageData"));
                            store.put("Username",params.get("Username"));
                            store.put("Password",params.get("Password"));

                            Toast.makeText(context.getApplicationContext(), "Account updated successfully", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(context.getApplicationContext(), "Account already exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(context.getApplicationContext(), "Wrong username or password.", Toast.LENGTH_SHORT).show();
                    }}
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //the network operation will be performed in background
        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();

            if(isNetworkAvailable(this.context))
            {
                Log.d("NetworkAvailable","TRUE");
                if(connectGoogle())
                {
                    Log.d("GooglePing","TRUE");
                    connection=true;

                    if (requestCode == CODE_POST_REQUEST)
                        return requestHandler.sendPostRequest(url, params);

                    if (requestCode == CODE_GET_REQUEST)
                        return requestHandler.sendGetRequest(url);

                }
                else
                {
                    Message msg = handler.obtainMessage();
                    msg.arg1 = 1;
                    handler.sendMessage(msg);
                    //Toast.makeText(context.getApplicationContext(), "Network error.", Toast.LENGTH_SHORT).show();
                    Log.d("GooglePing","FALSE");
                    connection=false;
                }
            }
            else {
                Message msg = handler.obtainMessage();
                msg.arg1 = 1;
                handler.sendMessage(msg);
                //Toast.makeText(context.getApplicationContext(), "Network error.", Toast.LENGTH_SHORT).show();
                connection=false;
            }

            return null;
        }


        public boolean isNetworkAvailable(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                return true;
            }
            return false;
        }

        public boolean connectGoogle() {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("https://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(10000);
                urlc.connect();
                return (urlc.getResponseCode() == 200);

            } catch (IOException e) {

                Log.d("GooglePing","IOEXCEPTION");
                e.printStackTrace();
                return false;
            }
        }

    }
}