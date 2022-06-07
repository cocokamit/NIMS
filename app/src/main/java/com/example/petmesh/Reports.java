package com.example.petmesh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petmesh.customlist.CustomListOwnerPetList;
import com.example.petmesh.datahelpers.Api;
import com.example.petmesh.datahelpers.Keystore;
import com.example.petmesh.datahelpers.PetStats;
import com.example.petmesh.datahelpers.Pets;
import com.example.petmesh.datahelpers.RequestHandler;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Reports extends AppCompatActivity {
    private TextView vetcount,ownercount;
    Keystore store;

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    BarChart barChart;
    ArrayList<PetStats> Items=null;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.arg1 == 1)
                Toast.makeText(getApplicationContext(),"Network error. Please check your internet and try again.", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        barChart=findViewById(R.id.barChart);
        vetcount=findViewById(R.id.vetcount);
        ownercount=findViewById(R.id.ownercount);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        HashMap<String, String> params = new HashMap<>();
        params.put("Yearer", "2021");

        PerformNetworkRequest request =new PerformNetworkRequest(Api.URL_READ_PET_STATS, params, CODE_POST_REQUEST,getApplicationContext());
        request.execute();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Keystore store=Keystore.getInstance(this);

        return true;
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
                        refreshItemList(object.getJSONArray("items"));
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

    //ListRefresher
    private void refreshItemList(JSONArray heroes) throws JSONException {

        Items=new ArrayList<PetStats>();

        ArrayList<BarEntry> entries=new ArrayList<>();
        for (int i = 0; i < heroes.length(); i++) {
            JSONObject obj = heroes.getJSONObject(i);

            Items.add(new PetStats(
                    obj.getString("Entries"),
                    obj.getString("Sysdate")
            ));
            vetcount.setText("Owner Count: "+obj.getString("OwnerCount"));
            ownercount.setText("Vet Count: "+obj.getString("VetCount"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            try {
                String d=(String) DateFormat.format("M",sdf.parse(obj.getString("Sysdate")));
                int a=Integer.parseInt(d)-1;
                int c=0;
                if(!obj.getString("Entries").equals("null")){
                c=(int)Double.parseDouble(obj.getString("Entries"));
                }

                entries.add(new BarEntry(a,c));

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(!entries.isEmpty()) {
            String[] monthsinyear=new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};

            BarDataSet dataSets2=new BarDataSet(entries,"Count");
            dataSets2.setColors(ColorTemplate.MATERIAL_COLORS);
            dataSets2.setValueTextColor(Color.BLACK);

            BarData barData1 = new BarData(dataSets2);

            barChart.setData(barData1);
            barChart.invalidate();
            barChart.setDoubleTapToZoomEnabled(false);
            barChart.setScaleYEnabled(false);
            XAxis xAxis1=barChart.getXAxis();
            xAxis1.setValueFormatter(new MyXAxisValueFormatter(monthsinyear));
            xAxis1.setGranularity(1);
        }

    }

        private class MyXAxisValueFormatter implements IAxisValueFormatter {

            private String[] mValues;
            public MyXAxisValueFormatter(String[] values)
            {
                this.mValues=values;
            }

            @Override
            public String getFormattedValue(float value, AxisBase axis) {

                return mValues[(int)value];
            }
        }
}