package com.example.petmesh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petmesh.customlist.CustomListOwnerPetList;
import com.example.petmesh.datahelpers.Api;
import com.example.petmesh.datahelpers.Keystore;
import com.example.petmesh.datahelpers.Pets;
import com.example.petmesh.datahelpers.RequestHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;

public class OwnersProf extends AppCompatActivity {

    private TextView txtFullname,txtMobileno,txtPhoneno,txtEmailaddress;
    Keystore store;
    ImageView ownerimage;
    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    ArrayList<Pets> Items=null;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton floatingbtn;
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.arg1 == 1)
                Toast.makeText(getApplicationContext(),"Network error. Please check your internet and try again.", Toast.LENGTH_LONG).show();
        }
    };
    HashMap<String, String> params = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owners_prof);

        txtFullname=findViewById(R.id.txtFullname);
        txtEmailaddress=findViewById(R.id.txtemailaddress);
        txtMobileno=findViewById(R.id.txtmobileno);
        txtPhoneno=findViewById(R.id.txtuserphoneno);
        floatingbtn=findViewById(R.id.floaterbtn);

        store=Keystore.getInstance(this);

        ownerimage=findViewById(R.id.ownerimage);
        txtFullname.setText(store.get("uFirstname")+" "+store.get("uMiddlename")+" "+store.get("uLastname"));
        txtEmailaddress.setText(store.get("uEmailaddress"));
        txtMobileno.setText(store.get("uMobileno"));
        txtPhoneno.setText(store.get("uPhoneno"));
        if(!store.get("uImageData").equals("")){
        byte[] decodedString = Base64.decode(store.get("uImageData"), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        ownerimage.setImageBitmap(decodedByte);}

        listView=findViewById(R.id.l3view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        params.put("Id", store.get("uId"));

        floatingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if it is updating
                Intent i=new Intent(OwnersProf.this, RegisterPet.class);
                OwnersProf.this.startActivity(i);
                OwnersProf.this.overridePendingTransition(R.anim.slide_right, R.anim.slide_up);
            }
        });

        swipeRefreshLayout=findViewById(R.id.swiperefresh1);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                PerformNetworkRequest request =new PerformNetworkRequest(Api.URL_READ_PET_BY_OWNER, params, CODE_POST_REQUEST,OwnersProf.this);
                request.execute();
            }
        });

        PerformNetworkRequest request =new PerformNetworkRequest(Api.URL_READ_PET_BY_OWNER, params, CODE_POST_REQUEST,this);
        request.execute();

        listView.setOnItemClickListener(this::onItemClick);
    }



    public void onItemClick(AdapterView<?> l, View v, int position, long id) {

        final Pets iteme = (Pets) l.getAdapter().getItem(position);

        store.put("pId",Integer.toString(iteme.getId()));
        store.put("pName",iteme.getName());
        store.put("pMicrochip",iteme.getMicrochip());
        store.put("pBreed",iteme.getBreed());
        store.put("pGender",iteme.getGender());
        store.put("pDateofBirth",iteme.getDateofBirth());
        store.put("pAge",iteme.getAge());
        store.put("pWeight",iteme.getWeight());
        store.put("pCoat",iteme.getCoat());
        store.put("pSpecie",iteme.getSpecie());
        store.put("pAntiRabiesSerialno",iteme.getAntiRabiesSerialno());
        store.put("pAntiRabiesDate",iteme.getAntiRabiesDate());
        store.put("pVaccinationno",iteme.getVaccinationno());
        store.put("pVeterinarian",iteme.getVeterinarian());
        store.put("pPRCandValidity",iteme.getPRCandValidity());
        store.put("pImageData",getStringImage(iteme.getImageData()));

        Intent i=new Intent(this, PetInfo.class);
        startActivity(i);
        this.overridePendingTransition(R.anim.slide_right, R.anim.slide_up);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus)
        {
            PerformNetworkRequest request =new PerformNetworkRequest(Api.URL_READ_PET_BY_OWNER, params, CODE_POST_REQUEST,OwnersProf.this);
            request.execute();
        }
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

        Items=new ArrayList<Pets>();

        for (int i = 0; i < heroes.length(); i++) {
            JSONObject obj = heroes.getJSONObject(i);
            Bitmap decodedByte=null;
            if(!obj.getString("ImageData").equals("")) {
                byte[] decodedString = Base64.decode(obj.getString("ImageData"), Base64.DEFAULT);
                decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            }
            Items.add(new Pets(
                    obj.getInt("Id"),
                    obj.getString("Name"),
                    obj.getString("Microchip"),
                    obj.getString("Breed"),
                    obj.getString("Gender"),
                    obj.getString("DateofBirth"),
                    obj.getString("Age"),
                    obj.getString("Weight"),
                    obj.getString("Coat"),
                    obj.getString("Specie"),
                    obj.getString("AntiRabiesSerialno"),
                    obj.getString("AntiRabiesDate"),
                    obj.getString("Vaccinationno"),
                    obj.getString("Veterinarian"),
                    obj.getString("PRCandValidity"),
                    decodedByte
            ));
        }

        CustomListOwnerPetList customListView=new CustomListOwnerPetList(this,R.layout.customlvperownerpet,Items);

        listView.setAdapter(customListView);

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}