package com.example.petmesh.datahelpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class NetworkRequest extends AsyncTask<Void, Void, String> {
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.arg1 == 1)
                Toast.makeText(context,"Network error. Please check your internet and try again.", Toast.LENGTH_LONG).show();
        }
    };
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
    public NetworkRequest(String url, HashMap<String, String> params, int requestCode, Context context) {
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
                }else
                {
                    Toast.makeText(context.getApplicationContext(), "Performance Error.", Toast.LENGTH_SHORT).show();
                }
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
                Log.d("GooglePing","FALSE");
                connection=false;
            }
        }
        else {
            Message msg = handler.obtainMessage();
            msg.arg1 = 1;
            handler.sendMessage(msg);
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
