package com.example.petmesh;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.petmesh.customlist.CustomListVaccinationform;
import com.example.petmesh.customlist.CustomListVaccinationformDisabled;
import com.example.petmesh.datahelpers.Api;
import com.example.petmesh.datahelpers.Keystore;
import com.example.petmesh.datahelpers.Pets;
import com.example.petmesh.datahelpers.RequestHandler;
import com.example.petmesh.datahelpers.Vaccination;
import com.example.petmesh.ui.OwnerList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PetInfo extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    Keystore store;
    ArrayList<Pets> Items=null;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    Button btnAdd;
    Spinner petgender;//petspecie,
    EditText txtpetname,txtpetage,txtpetbreed,txtpetdateofbirth,txtpetweight,txtpetcoat,txtpetantiereabiesno,txtpetantiRabiesdate,txtpetvaccination,txtpetmicropchip,txtspecie;
    ImageView petimage,imagedelete;

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.arg1 == 1)
                Toast.makeText(getApplicationContext(),"Network error. Please check your internet and try again.", Toast.LENGTH_LONG).show();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_info);

        store= Keystore.getInstance(this);
        txtpetname=findViewById(R.id.txtpetname);
        txtpetage=findViewById(R.id.txtpetage);
        txtpetbreed=findViewById(R.id.txtpetbreed);
        txtpetcoat=findViewById(R.id.txtpetcoat);
        txtpetdateofbirth=findViewById(R.id.txtpetdateofbirth);
        txtpetweight=findViewById(R.id.txtpetweight);
       /* txtpetantiereabiesno=findViewById(R.id.txtpetantiereabiesno);
        txtpetantiRabiesdate=findViewById(R.id.txtpetantiRabiesdate);
        txtpetvaccination=findViewById(R.id.txtpetvaccination);*/
        listView=findViewById(R.id.l3view);
        //petspecie=findViewById(R.id.spinner1);
        txtspecie=findViewById(R.id.txtspecie);
        petgender=findViewById(R.id.spinner2);
        txtpetmicropchip=findViewById(R.id.txtpetmicrochip);
        petimage=findViewById(R.id.petimage);
        imagedelete=findViewById(R.id.imagedelete);

        String[] species = new String[]{"Dog", "Cat", "Others"};
        String[] genders = new String[]{"Male", "Female", "Others"};

        /* adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, species);
        petspecie.setAdapter(adapter);*/
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genders);
        petgender.setAdapter(adapter);

        imagedelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PetInfo.this);
                builder.setCancelable(true);
                builder.setTitle("Confirmation Message");
                builder.setMessage("Are you sure you want to delete this data?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                HashMap<String, String> params = new HashMap<>();
                                params.put("Id", store.get("pId"));

                                PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_DELETE_PET, params, CODE_POST_REQUEST, PetInfo.this);
                                request.execute();

                           }
                        });

                builder.setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        byte[] decodedString = Base64.decode(store.get("pImageData"), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        petimage.setImageBitmap(decodedByte);
        txtpetname.setText(store.get("pName"));
        txtpetage.setText(store.get("pAge"));
        txtpetbreed.setText(store.get("pBreed"));
        txtpetcoat.setText(store.get("pCoat"));
        txtpetdateofbirth.setText(store.get("pDateofBirth"));
        txtpetweight.setText(store.get("pWeight"));
       /* txtpetantiereabiesno.setText(store.get("pAntiRabiesSerialno"));
        txtpetantiRabiesdate.setText(store.get("pAntiRabiesDate"));
        txtpetvaccination.setText(store.get("pVaccinationno"));*/
        txtspecie.setText(store.get("pSpecie"));
     /*   if(store.get("pSpecie").equals("Dog"))
        {
            petspecie.setSelection(0);
        }
        else if(store.get("pSpecie").equals("Cat"))
        {
            petspecie.setSelection(1);
        }else
        {
            petspecie.setSelection(2);
        }*/

        if(store.get("pGender").equals("Male"))
        {
            petgender.setSelection(0);
        }
        else if(store.get("pGender").equals("Female"))
        {
            petgender.setSelection(1);
        }else
        {
            petgender.setSelection(2);
        }
        txtpetmicropchip.setText(store.get("pMicrochip"));

        txtpetname.setEnabled(false);
        txtpetage.setEnabled(false);
        txtpetbreed.setEnabled(false);
        txtpetcoat.setEnabled(false);
        txtpetdateofbirth.setEnabled(false);
        txtpetweight.setEnabled(false);
       /* txtpetantiereabiesno.setEnabled(false);
        txtpetantiRabiesdate.setEnabled(false);
        txtpetvaccination.setEnabled(false);*/
       // petspecie.setEnabled(false);
        txtspecie.setEnabled(false);
        petgender.setEnabled(false);
        txtpetmicropchip.setEnabled(false);

        HashMap<String, String> params = new HashMap<>();
        params.put("Id", store.get("pId"));
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_READ_PET_VACCINATION, params, CODE_POST_REQUEST, PetInfo.this);
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

                        if(object.getString("message").equals("Pet deleted successfully"))
                        {
                            onBackPressed();
                        }
                        else if(object.getString("message").equals("getpetvaccination successfully completed"))
                        {
                            ArrayList<Vaccination> obj=new ArrayList<>();
                            String json = object.getString("users");
                            Gson gson = new Gson();
                            if (json!=null) {
                                if (!json.equals("")) {
                                    Type type = new TypeToken<List<Vaccination>>(){}.getType();
                                    obj = gson.fromJson(json, type);
                                    CustomListVaccinationformDisabled customListView=new CustomListVaccinationformDisabled(context,R.layout.customlvownerlist,obj);

                                    listView.setAdapter(customListView);
                                }}
                        }
                        else{
                            //Toast.makeText(context.getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(context.getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
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