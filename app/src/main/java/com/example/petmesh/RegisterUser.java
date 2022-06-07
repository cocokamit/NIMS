package com.example.petmesh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.petmesh.datahelpers.Api;
import com.example.petmesh.datahelpers.Keystore;
import com.example.petmesh.datahelpers.RequestHandler;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class RegisterUser extends AppCompatActivity {


    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private EditText Firstname,Middlename,Lastname,Mobileno,Phoneno,Emailaddress,Username,Password;
    private Button btnRegister;
    Keystore store;
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.arg1 == 1)
                Toast.makeText(getApplicationContext(),"Network error. Please check your internet and try again.", Toast.LENGTH_LONG).show();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        store=Keystore.getInstance(this);
        Firstname=findViewById(R.id.txtfirstname);
        Middlename=findViewById(R.id.txtmiddlename);
        Lastname=findViewById(R.id.txtlastname);
        Mobileno=findViewById(R.id.txtmobilenumber);
        Phoneno=findViewById(R.id.txtphonenumber);
        Emailaddress=findViewById(R.id.txtemailaddress);
        Username=findViewById(R.id.txtUsername);
        Password=findViewById(R.id.txtPassword);
        btnRegister=findViewById(R.id.btnRegister);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if it is updating
                RegisterUser();
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Keystore store=Keystore.getInstance(this);

        return true;
    }

    private void RegisterUser() {

        //validating the /inputs

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
        //if validation passes

        HashMap<String, String> params = new HashMap<>();
        params.put("Username", Username.getText().toString());
        params.put("Password", Password.getText().toString());
        params.put("Firstname", Firstname.getText().toString());
        params.put("Middlename", Middlename.getText().toString());
        params.put("Lastname", Lastname.getText().toString());
        params.put("Mobileno", Mobileno.getText().toString());
        params.put("Phoneno", Phoneno.getText().toString());
        params.put("Emailaddress", Emailaddress.getText().toString());
        params.put("VetId", store.get("Id"));
        params.put("Typec", "1");
        params.put("ImageData",  "2");
        params.put("PRCandValidity","0");
        //Calling the create hero API

        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_CREATE_ACCOUNT, params, CODE_POST_REQUEST, this);
        request.execute();

        Firstname.getText().clear();
        Middlename.getText().clear();
        Lastname.getText().clear();
        Mobileno.getText().clear();
        Phoneno.getText().clear();
        Emailaddress.getText().clear();
        Username.getText().clear();
        Password.getText().clear();


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
        Activity activity;
        //constructor to initialize values
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode,Context context) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
            this.context=context;
            this.activity=(Activity)context;
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
                        store.put("uUsername",params.get("Username"));
                        store.put("uPassword",params.get("Password"));
                        store.put("uFirstname",params.get("Firstname"));
                        store.put("uMiddlename",params.get("Middlename"));
                        store.put("uLastname",params.get("Lastname"));
                        store.put("uMobileno",params.get("Mobileno"));
                        store.put("uPhoneno",params.get("Phoneno"));
                        store.put("uEmailaddress",params.get("Emailaddress"));
                        store.put("uId",object.getJSONArray("users").getJSONObject(0).getString("Id"));

                        Intent i=new Intent(context, OwnersProf.class);
                        activity.startActivity(i);
                        activity.overridePendingTransition(R.anim.slide_right, R.anim.slide_up);
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