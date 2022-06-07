package com.example.petmesh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.petmesh.datahelpers.Api;
import com.example.petmesh.datahelpers.Keystore;
import com.example.petmesh.datahelpers.Pets;
import com.example.petmesh.datahelpers.RequestHandler;
import com.github.dhaval2404.imagepicker.ImagePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class InventoryAdder extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    Keystore store;
    Button btnAdd;
    Spinner itemcategory;
    ImageView imagepetid;
    EditText txtname,txtdescription,txtquantity,txtprice;
    Bitmap bitmap=null;
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.arg1 == 1)
                Toast.makeText(getApplicationContext(),"Network error. Please check your internet and try again.", Toast.LENGTH_LONG).show();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_adder);
        store= Keystore.getInstance(this);
        txtname=findViewById(R.id.txtname);
        txtdescription=findViewById(R.id.txtdescription);
        txtquantity=findViewById(R.id.txtquantity);
        txtprice=findViewById(R.id.txtprice);
        imagepetid=findViewById(R.id.itemimage);
        itemcategory=findViewById(R.id.spinner2);
        btnAdd=findViewById(R.id.btnAdd);
        String[] species = new String[]{"Food", "Item","Toys", "Medicine"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, species);
        itemcategory.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if it is updating
                RegisterPet();
            }
        });

        imagepetid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(InventoryAdder.this)
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
                        imagepetid.setImageURI(resultUri);
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

    private void RegisterPet() {

        if (TextUtils.isEmpty(txtname.getText().toString().trim())) {
            txtname.setError("Please enter name");
            txtname.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(txtdescription.getText().toString().trim())) {
            txtdescription.setError("Please enter description");
            txtdescription.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(txtquantity.getText().toString().trim())) {
            txtquantity.setError("Please enter quantity");
            txtquantity.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(txtprice.getText().toString().trim())) {
            txtprice.setError("Please enter price");
            txtprice.requestFocus();
            return;
        }


        String uploadImage="2";
        if(bitmap!=null){
            uploadImage = getStringImage(bitmap);

            HashMap<String, String> params = new HashMap<>();
            params.put("Name", txtname.getText().toString());
            params.put("Description", txtdescription.getText().toString());
            params.put("Quantity", txtquantity.getText().toString());
            params.put("Price", txtprice.getText().toString());
            params.put("Category", itemcategory.getSelectedItem().toString());
            params.put("ImageData",  uploadImage);
            //Calling the create hero API
            PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_INSERT_ITEM, params, CODE_POST_REQUEST, this);
            request.execute();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Item Image is required.", Toast.LENGTH_SHORT).show();
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

                        if(object.getString("message").equals("Item added successfully"))
                        {
                            onBackPressed();
                        }
                        else{
                            Toast.makeText(context.getApplicationContext(), "Item already exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(context.getApplicationContext(), "Error.", Toast.LENGTH_SHORT).show();
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