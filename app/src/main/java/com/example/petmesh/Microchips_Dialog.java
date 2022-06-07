package com.example.petmesh;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.petmesh.datahelpers.Api;
import com.example.petmesh.datahelpers.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class Microchips_Dialog extends DialogFragment {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private EditText txtmicrochip;
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.arg1 == 1)
                Toast.makeText(getActivity().getApplicationContext(),"Network error. Please check your internet and try again.", Toast.LENGTH_LONG).show();
        }
    };
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater= getActivity().getLayoutInflater();
        View view =inflater.inflate(R.layout.dialog_microchips,null);
        txtmicrochip=view.findViewById(R.id.txtmicrochipnumber);
        builder.setView(view)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ongo();
                    }
                });

        return builder.create();

    }

    public void ongo()
    {
        String chipnumber=txtmicrochip.getText().toString();

        if(!chipnumber.isEmpty())
        {
            HashMap<String, String> params = new HashMap<>();
            params.put("Microchip",chipnumber);

            PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_INSERT_MICROCHIP, params, CODE_POST_REQUEST,getActivity().getApplicationContext(),getActivity());
            request.execute();
        }
        else
        {
            Toast.makeText(getActivity().getApplicationContext(), "Please enter microchip number", Toast.LENGTH_SHORT).show();
        }
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
        Activity actibity;
        //constructor to initialize values
        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode,Context context,Activity actibity) {
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
            this.context=context;
            this.actibity=actibity;
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
                        Toast.makeText(actibity, "Performance Error.", Toast.LENGTH_SHORT).show();
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
