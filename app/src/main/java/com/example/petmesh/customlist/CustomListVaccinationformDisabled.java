package com.example.petmesh.customlist;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.petmesh.R;
import com.example.petmesh.datahelpers.Keystore;
import com.example.petmesh.datahelpers.Vaccination;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CustomListVaccinationformDisabled extends ArrayAdapter<Vaccination> {
    List<Vaccination> itemsList;
    Context context;
    Keystore store;
    public CustomListVaccinationformDisabled(Context context,int LayoutId, List<Vaccination> itemsList) {
        super(context, LayoutId, itemsList);
        this.itemsList = itemsList;
        this.context=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View listViewItem = inflater.inflate(R.layout.customvaccinationform, null, true);
        store= Keystore.getInstance(getContext());
        ImageView closeimage=listViewItem.findViewById(R.id.imageclose);
        TextView txtvacserialno=listViewItem.findViewById(R.id.txtvacserialno);
        TextView vactxtdate=listViewItem.findViewById(R.id.vactxtdate);
        TextView vactxtstockno=listViewItem.findViewById(R.id.vactxtstockno);

        final Vaccination iteme = itemsList.get(position);


        txtvacserialno.setText(iteme.getSerialno());
        vactxtdate.setText(iteme.getApplicationDate());
        vactxtstockno.setText(iteme.getStockno());
        closeimage.setVisibility(View.GONE);

        return listViewItem;
    }
}