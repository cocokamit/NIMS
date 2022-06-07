package com.example.petmesh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.petmesh.customlist.AdpaterRec;
import com.example.petmesh.customlist.CustomListOwnerPetList;
import com.example.petmesh.datahelpers.Api;
import com.example.petmesh.datahelpers.InventoryItems;
import com.example.petmesh.datahelpers.Keystore;
import com.example.petmesh.datahelpers.Pets;
import com.example.petmesh.datahelpers.RecyclerItemClickListener;
import com.example.petmesh.datahelpers.RequestHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Inventory extends AppCompatActivity {
    RecyclerView dataList;
    Keystore store;
    FloatingActionButton floatingbtn;

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    Spinner itemcategory;
    ArrayList<InventoryItems> Items=null;
    List<InventoryItems> tempItems=null;
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
        setContentView(R.layout.activity_inventory);
        dataList=findViewById(R.id.l3view);

        itemcategory=findViewById(R.id.spinner2);
        String[] species = new String[]{"Food", "Item","Toys", "Medicine"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, species);
        itemcategory.setAdapter(adapter);

        itemcategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(Items!=null){
                    if(i==0)
                    {
                        tempItems=Items.stream().filter(inventoryItems -> inventoryItems.getCategory().contains("Food")).collect(Collectors.toList());
                        AdpaterRec customListView=new AdpaterRec(getApplicationContext(),Items.stream().filter(inventoryItems -> inventoryItems.getCategory().contains("Food")).collect(Collectors.toList()));

                        GridLayoutManager gridLayoutManager=new GridLayoutManager(getApplicationContext(),2,GridLayoutManager.VERTICAL,false);
                        dataList.setLayoutManager(gridLayoutManager);
                        dataList.setAdapter(customListView);
                    }
                    else if(i==1)
                    {
                        AdpaterRec customListView=new AdpaterRec(getApplicationContext(),Items.stream().filter(inventoryItems -> inventoryItems.getCategory().contains("Item")).collect(Collectors.toList()));
                        tempItems=Items.stream().filter(inventoryItems -> inventoryItems.getCategory().contains("Item")).collect(Collectors.toList());

                        GridLayoutManager gridLayoutManager=new GridLayoutManager(getApplicationContext(),2,GridLayoutManager.VERTICAL,false);
                        dataList.setLayoutManager(gridLayoutManager);
                        dataList.setAdapter(customListView);

                    }
                    else if(i==2)
                    {
                        AdpaterRec customListView=new AdpaterRec(getApplicationContext(),Items.stream().filter(inventoryItems -> inventoryItems.getCategory().contains("Toys")).collect(Collectors.toList()));
                        tempItems=Items.stream().filter(inventoryItems -> inventoryItems.getCategory().contains("Toys")).collect(Collectors.toList());

                        GridLayoutManager gridLayoutManager=new GridLayoutManager(getApplicationContext(),2,GridLayoutManager.VERTICAL,false);
                        dataList.setLayoutManager(gridLayoutManager);
                        dataList.setAdapter(customListView);

                    }
                    else if(i==3)
                    {
                        AdpaterRec customListView=new AdpaterRec(getApplicationContext(),Items.stream().filter(inventoryItems -> inventoryItems.getCategory().contains("Medicine")).collect(Collectors.toList()));
                        tempItems=Items.stream().filter(inventoryItems -> inventoryItems.getCategory().contains("Medicine")).collect(Collectors.toList());

                        GridLayoutManager gridLayoutManager=new GridLayoutManager(getApplicationContext(),2,GridLayoutManager.VERTICAL,false);
                        dataList.setLayoutManager(gridLayoutManager);
                        dataList.setAdapter(customListView);
                    }
                }
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
        floatingbtn=findViewById(R.id.floaterbtn);
        store=Keystore.getInstance(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        floatingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if it is updating
                Intent i=new Intent(Inventory.this, InventoryAdder.class);
                Inventory.this.startActivity(i);
                Inventory.this.overridePendingTransition(R.anim.slide_right, R.anim.slide_up);
            }
        });

        swipeRefreshLayout=findViewById(R.id.swiperefresh1);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                PerformNetworkRequest request =new PerformNetworkRequest(Api.URL_READ_INVENTORY, null, CODE_GET_REQUEST,getApplicationContext());
                request.execute();
            }
        });

        PerformNetworkRequest request =new PerformNetworkRequest(Api.URL_READ_INVENTORY, null, CODE_GET_REQUEST,getApplicationContext());
        request.execute();

        dataList.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), dataList ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        if(tempItems!=null)
                        {
                            store.put("iId",tempItems.get(position).getId());
                            store.put("iName",tempItems.get(position).getName());
                            store.put("iDescription",tempItems.get(position).getDescription());
                            store.put("iQuantity",tempItems.get(position).getQuantity());
                            store.put("iPrice",tempItems.get(position).getPrice());
                            store.put("iCategory",tempItems.get(position).getCategory());
                            store.put("iSysdate",tempItems.get(position).getSysdate());
                            store.put("iImageData",getStringImage(tempItems.get(position).getImageData()));

                            Intent i=new Intent(Inventory.this, inventory_info.class);
                            startActivity(i);
                            Inventory.this.overridePendingTransition(R.anim.slide_right, R.anim.slide_up);
                        }
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        Toast.makeText(getApplicationContext(),"bbb",Toast.LENGTH_SHORT).show();
                    }
                })
        );

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Keystore store=Keystore.getInstance(this);

        return true;
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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
                        refreshItemList(object.getJSONArray("users"));
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

        Items=new ArrayList<InventoryItems>();

        for (int i = 0; i < heroes.length(); i++) {
            JSONObject obj = heroes.getJSONObject(i);
            Bitmap decodedByte=null;
            if(!obj.getString("ImageData").equals("")) {
                byte[] decodedString = Base64.decode(obj.getString("ImageData"), Base64.DEFAULT);
                decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            }
            Items.add(new InventoryItems(
                    obj.getString("Id"),
                    obj.getString("Name"),
                    obj.getString("Description"),
                    obj.getString("Quantity"),
                    obj.getString("Price"),
                    obj.getString("Category"),
                    obj.getString("Sysdate"),
                    decodedByte
            ));
        }

        AdpaterRec customListView=new AdpaterRec(getApplicationContext(),Items);

        tempItems=Items;
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false);
        dataList.setLayoutManager(gridLayoutManager);
        dataList.setAdapter(customListView);
        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}