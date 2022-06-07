package com.example.petmesh;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.petmesh.datahelpers.Api;
import com.example.petmesh.datahelpers.Keystore;
import com.example.petmesh.datahelpers.RequestHandler;
import com.example.petmesh.owners.OwnerActivity;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class loginfragment extends Fragment {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    EditText username,password;
    AppCompatButton loginButton;
    boolean isUpdating = false;

    TextInputLayout textInputLayout1,textInputLayout2;
    float v=0;

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.arg1 == 1)
                Toast.makeText(getActivity(),"Network error. Please check your internet and try again.", Toast.LENGTH_LONG).show();
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root=(ViewGroup) inflater.inflate(R.layout.fragment_loginfragment, container,false);

        username=root.findViewById(R.id.txtUsername);
        password=root.findViewById(R.id.txtPassword);
        loginButton=root.findViewById(R.id.loginButton);

        textInputLayout1=root.findViewById(R.id.textInputLayout1);
        textInputLayout2=root.findViewById(R.id.textInputLayout2);
        textInputLayout1.setTranslationX(800);
        textInputLayout2.setTranslationX(800);
        loginButton.setTranslationX(800);

        textInputLayout1.setAlpha(v);
        textInputLayout2.setAlpha(v);
        loginButton.setAlpha(v);

        textInputLayout1.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(100).start();
        textInputLayout2.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(200).start();
        loginButton.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if it is updating
                if (isUpdating) {
                } else {
                    UserById();
                }
            }
        });

        Keystore store=Keystore.getInstance(getContext());
        if(store.get("Id")!=null)
        {
            if(store.get("Type").equals("1")){
                startActivity(new Intent(getActivity(), MainActivity.class));}
            else if(store.get("Type").equals("0"))
            {
                startActivity(new Intent(getActivity(), OwnerActivity.class));
            }
            else
            {
                startActivity(new Intent(getActivity(), Administrator.class));
            }
        }
        return root;
    }

    private void UserById() {
        String Username = username.getText().toString().trim();
        String Password = password.getText().toString().trim();

        //validating the inputs

        if (TextUtils.isEmpty(Username)) {
            username.setError("Please enter username");
            username.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(Password)) {
            password.setError("Please enter password");
            password.requestFocus();
            return;
        }

        //if validation passes

        HashMap<String, String> params = new HashMap<>();
        params.put("Username", Username);
        params.put("Password", Password);
        //Calling the create hero API
        if(Username.equals("admin") && Password.equals("admin"))
        {
            Keystore store= Keystore.getInstance(getContext());
            store.put("Id","0");
            store.put("Type","2");
            store.put("Username","admin");
            store.put("Password","admin");
            startActivity(new Intent(getActivity(), Administrator.class));
        }
        else {
            PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_ACCOUNT_BY_LOGIN, params, CODE_POST_REQUEST, getContext());
            request.execute();
        }
        username.getText().clear();
        password.getText().clear();
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

                    if(object.getString("message").equals("Login successfully"))
                    {
                        JSONArray jsonArray=object.getJSONArray("user");

                        if(jsonArray.getJSONObject(0).getString("Status").equals("0")){

                            Keystore store= Keystore.getInstance(context);
                            store.put("Id",jsonArray.getJSONObject(0).getString("Id"));
                            store.put("Firstname",jsonArray.getJSONObject(0).getString("Firstname"));
                            store.put("Middlename",jsonArray.getJSONObject(0).getString("Middlename"));
                            store.put("Lastname",jsonArray.getJSONObject(0).getString("Lastname"));
                            store.put("Mobileno",jsonArray.getJSONObject(0).getString("Mobileno"));
                            store.put("Phoneno",jsonArray.getJSONObject(0).getString("Phoneno"));
                            store.put("Emailaddress",jsonArray.getJSONObject(0).getString("Emailaddress"));
                            store.put("PRCandValidity",jsonArray.getJSONObject(0).getString("PRCandValidity"));
                            store.put("ImageData",jsonArray.getJSONObject(0).getString("ImageData"));
                            store.put("Username",jsonArray.getJSONObject(0).getString("Username"));
                            store.put("Password",jsonArray.getJSONObject(0).getString("Password"));
                            store.put("Type",jsonArray.getJSONObject(0).getString("Type"));
                            if(jsonArray.getJSONObject(0).getString("Type").equals("1"))
                            {
                                context.startActivity(new Intent(context, MainActivity.class));
                            }
                            else if(jsonArray.getJSONObject(0).getString("Type").equals("0"))
                            {
                                context.startActivity(new Intent(context, OwnerActivity.class));
                            }
                            Toast.makeText(context.getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(context.getApplicationContext(), "Account is currently suspended", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(context.getApplicationContext(), "Account does not exist", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(context.getApplicationContext(), "Wrong username or password.", Toast.LENGTH_SHORT).show();
                }}else
                {
                    Toast.makeText(context.getApplicationContext(), "Network error. Please check your internet and try again.", Toast.LENGTH_SHORT).show();
                }
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
                    connection = true;
                    Log.d("GooglePing","TRUE");
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