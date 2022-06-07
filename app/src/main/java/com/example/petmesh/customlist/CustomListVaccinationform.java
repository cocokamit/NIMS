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
import com.example.petmesh.datahelpers.Api;
import com.example.petmesh.datahelpers.Keystore;
import com.example.petmesh.datahelpers.Micros;
import com.example.petmesh.datahelpers.NetworkRequest;
import com.example.petmesh.datahelpers.Vaccination;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CustomListVaccinationform  extends ArrayAdapter<Vaccination> {
    List<Vaccination> itemsList;
    Context context;
    Keystore store;
    public CustomListVaccinationform(Context context,int LayoutId, List<Vaccination> itemsList) {
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

        closeimage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle("Confirmation Message");
                builder.setMessage("Are you sure you want to delete this data?");
                builder.setPositiveButton("Confirm",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ArrayList<Vaccination> obj=new ArrayList<Vaccination>();
                                String json = store.get("Vaccinationform");
                                Gson gson = new Gson();
                                if (json != null) {
                                    if(!json.equals("")){
                                        Type type = new TypeToken<List<Vaccination>>(){}.getType();
                                        obj = gson.fromJson(json, type);}
                                }
                                obj.remove(position-1);

                                store.put("Vaccinationform",gson.toJson(obj));
                               /* NetworkRequest request=new NetworkRequest(Api.URL_DELETE_MICROCHIP,params,CODE_POST_REQUEST,getContext());
                                request.execute();*/

                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }

        });
        return listViewItem;
    }
}
