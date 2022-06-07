package com.example.petmesh;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.icu.util.Calendar;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.example.petmesh.datahelpers.Api;
import com.example.petmesh.datahelpers.Keystore;
import com.example.petmesh.datahelpers.RequestHandler;
import com.example.petmesh.datahelpers.Vaccination;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RegisterPet_Vaccination_Dialog  extends DialogFragment {

    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;
    private EditText txtpetantiereabiesno,txtpetantiRabiesdate,txtpetvaccination;
    Keystore keystore;
    final Calendar myCalendar = Calendar.getInstance();
    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        txtpetantiRabiesdate.setText(sdf.format(myCalendar.getTime()));
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater= getActivity().getLayoutInflater();
        View view =inflater.inflate(R.layout.dialog_vaccinationform,null);
        keystore= Keystore.getInstance(getActivity());
        txtpetantiereabiesno=view.findViewById(R.id.txtpetantiereabiesno);
        txtpetantiRabiesdate=view.findViewById(R.id.txtpetantiRabiesdate);
        txtpetvaccination=view.findViewById(R.id.txtpetvaccination);


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

        txtpetantiRabiesdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

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
        if (TextUtils.isEmpty(txtpetantiereabiesno.getText().toString().trim())) {
            txtpetantiereabiesno.setError("Please enter this field");
            txtpetantiereabiesno.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(txtpetantiRabiesdate.getText().toString().trim())) {
            txtpetantiRabiesdate.setError("Please enter this field");
            txtpetantiRabiesdate.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(txtpetvaccination.getText().toString().trim())) {
            txtpetvaccination.setError("Please enter this field");
            txtpetvaccination.requestFocus();
            return;
        }
        ArrayList<Vaccination> obj=new ArrayList<Vaccination>();
        String json = keystore.get("Vaccinationform");
        Gson gson = new Gson();
        if (json != null) {
            if(!json.equals("")){
            Type type = new TypeToken<List<Vaccination>>(){}.getType();
            obj = gson.fromJson(json, type);}
        }

        obj.add(new Vaccination("",txtpetantiereabiesno.getText().toString(),txtpetantiRabiesdate.getText().toString(),txtpetvaccination.getText().toString(),""));

        keystore.put("Vaccinationform",gson.toJson(obj));
    }

}
