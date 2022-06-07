package com.example.petmesh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.icu.util.Calendar;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.DragAndDropPermissions;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.petmesh.customlist.CustomListMicrochip;
import com.example.petmesh.customlist.CustomListVaccinationform;
import com.example.petmesh.datahelpers.Api;
import com.example.petmesh.datahelpers.Keystore;
import com.example.petmesh.datahelpers.Pets;
import com.example.petmesh.datahelpers.RequestHandler;
import com.example.petmesh.datahelpers.Vaccination;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class RegisterPet extends AppCompatActivity {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    Keystore store;
    ArrayList<Pets> Items=null;
    ArrayList<Vaccination> VaccinationItems=null;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    Button btnAdd,btnvaccinationform;
    Spinner petgender;
    ImageView imagepetid;
    EditText txtpetname,txtpetage,txtpetbreed,txtpetdateofbirth,txtpetweight,txtpetcoat,txtpetmicropchip,petspecie; //txtpetantiereabiesno,txtpetantiRabiesdate,txtpetvaccination
    Bitmap bitmap=null;
   // LinearLayout contrainvac;

    String GetImageNameEditText;
    String ImageName = "image_name" ;
    String ImagePath = "image_path" ;
    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.arg1 == 1)
                Toast.makeText(getApplicationContext(),"Network error. Please check your internet and try again.", Toast.LENGTH_LONG).show();
        }
    };


    final Calendar myCalendar = Calendar.getInstance();
    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        txtpetdateofbirth.setText(sdf.format(myCalendar.getTime()));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_pet);

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
        btnAdd=findViewById(R.id.btnAdd);
        listView=findViewById(R.id.l3view);
        petspecie=findViewById(R.id.txtspecie);
        petgender=findViewById(R.id.spinner2);
        txtpetmicropchip=findViewById(R.id.txtpetmicrochip);
        imagepetid=findViewById(R.id.imagepetid);
       // contrainvac=findViewById(R.id.contrainvac);
        btnvaccinationform=findViewById(R.id.btnvaccinationform);
        String[] species = new String[]{"Dog", "Cat", "Others"};
        String[] genders = new String[]{"Male", "Female", "Others"};

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }



        };

        txtpetdateofbirth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(RegisterPet.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        /*ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, species);
        petspecie.setAdapter(adapter);*/
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genders);
        petgender.setAdapter(adapter);

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
                ImagePicker.with(RegisterPet.this)
                        .cropSquare()
                        .compress(1024)
                        .maxResultSize(512, 512)
                        .start();
            }
        });

        btnvaccinationform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
        store.put("Vaccinationform","");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus)
        {
            ArrayList<Vaccination> obj=new ArrayList<>();
            String json = store.get("Vaccinationform");
            Gson gson = new Gson();
            if (json!=null) {
            if (!json.equals("")) {
                Type type = new TypeToken<List<Vaccination>>(){}.getType();
                obj = gson.fromJson(json, type);
                VaccinationItems=obj;
                CustomListVaccinationform customListView=new CustomListVaccinationform(this,R.layout.customlvownerlist,VaccinationItems);

                listView.setAdapter(customListView);
            }
        }}
    }

    public void openDialog()
    {
        RegisterPet_Vaccination_Dialog createUserDialog = new RegisterPet_Vaccination_Dialog();
        createUserDialog.show(getSupportFragmentManager(), "Account");
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

        if (TextUtils.isEmpty(txtpetname.getText().toString().trim())) {
            txtpetname.setError("Please enter pet name");
            txtpetname.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(txtpetage.getText().toString().trim())) {
            txtpetage.setError("Please enter pet age");
            txtpetage.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(txtpetweight.getText().toString().trim())) {
            txtpetweight.setError("Please enter pet weight");
            txtpetweight.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(txtpetcoat.getText().toString().trim())) {
            txtpetcoat.setError("Please enter pet coat");
            txtpetcoat.requestFocus();
            return;
        }

     /*   if (TextUtils.isEmpty(txtpetvaccination.getText().toString().trim())) {
            txtpetvaccination.setError("Please enter pet vaccination stock Number");
            txtpetvaccination.requestFocus();
            return;
        }
*/
        if (TextUtils.isEmpty(txtpetdateofbirth.getText().toString().trim())) {
            txtpetdateofbirth.setError("Please enter pet date of birth");
            txtpetdateofbirth.requestFocus();
            return;
        }

        /*if (TextUtils.isEmpty(txtpetantiereabiesno.getText().toString().trim())) {
            txtpetantiereabiesno.setError("Please enter pet latest anti-rabies vaccination serial no.");
            txtpetantiereabiesno.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(txtpetantiRabiesdate.getText().toString().trim())) {
            txtpetantiRabiesdate.setError("Please enter pet latest anti-rabies application date");
            txtpetantiRabiesdate.requestFocus();
            return;
        }*/
        if (TextUtils.isEmpty(txtpetmicropchip.getText().toString().trim())) {
            txtpetmicropchip.setError("Please enter pet microchip");
            txtpetmicropchip.requestFocus();
            return;
        }
        //if validation passes
        //'Name','Microchip','Breed','Gender','DateofBirth','Age','Weight','Coat','Specie','AntiRabiesSerialno','AntiRabiesDate','Vaccinationno','Veterinarian','PRCandValidity','OwnerId'

        String uploadImage="2";
        if(bitmap!=null){
            uploadImage = getStringImage(bitmap);

        HashMap<String, String> params = new HashMap<>();
        params.put("Name", txtpetname.getText().toString());
        params.put("Microchip", txtpetmicropchip.getText().toString());
        params.put("Breed", txtpetbreed.getText().toString());
        params.put("Gender", petgender.getSelectedItem().toString());
        params.put("DateofBirth", txtpetdateofbirth.getText().toString());
        params.put("Age", txtpetage.getText().toString());
        params.put("Weight", txtpetweight.getText().toString());
        params.put("Coat", txtpetcoat.getText().toString());
        params.put("Specie", petspecie.getText().toString());
       /* params.put("AntiRabiesSerialno", txtpetantiereabiesno.getText().toString());
        params.put("AntiRabiesDate", txtpetantiRabiesdate.getText().toString());
        params.put("Vaccinationno", txtpetvaccination.getText().toString());*/
        params.put("AntiRabiesSerialno", "1");
        params.put("AntiRabiesDate", "1");
        params.put("Vaccinationno", "1");
        params.put("Veterinarian",  store.get("Id"));
        params.put("PRCandValidity", "0");
        params.put("OwnerId",  store.get("uId"));
        params.put("ImageData",  uploadImage);
        StringBuilder ff= new StringBuilder();
            ArrayList<Vaccination> obj=new ArrayList<>();
            String json = store.get("Vaccinationform");
            Gson gson = new Gson();
            if (json!=null) {
                if (!json.equals("")) {
                    Type type = new TypeToken<List<Vaccination>>(){}.getType();
                    obj = gson.fromJson(json, type);
                    VaccinationItems=obj;
                    for(Vaccination e :VaccinationItems)
                    {
                        ff.append(e.getSerialno()).append(",").append(e.getApplicationDate()).append(",").append(e.getStockno()).append("~");
                    }
                }
            }

        params.put("Vaccinationform", ff.toString());
        //Calling the create hero API
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_INSERT_PET, params, CODE_POST_REQUEST, this);
        request.execute();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Pet Image is required.", Toast.LENGTH_SHORT).show();
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

                    if(object.getString("message").equals("Pet added successfully"))
                    {
                        onBackPressed();
                    }
                    else{
                        Toast.makeText(context.getApplicationContext(), "back error", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(context.getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
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