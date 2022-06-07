package com.example.petmesh.customlist;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.petmesh.R;
import com.example.petmesh.datahelpers.Accounts;
import com.example.petmesh.datahelpers.Api;
import com.example.petmesh.datahelpers.Micros;
import com.example.petmesh.datahelpers.NetworkRequest;

import java.util.HashMap;
import java.util.List;

public class CustomListMicrochip extends ArrayAdapter<Micros> {
    List<Micros> itemsList;
    Context context;
    public CustomListMicrochip(Context context,int LayoutId, List<Micros> itemsList) {
        super(context, LayoutId, itemsList);
        this.itemsList = itemsList;
        this.context=context;
    }


    private static final int CODE_GET_REQUEST = 1024;
    private static final int CODE_POST_REQUEST = 1025;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View listViewItem = inflater.inflate(R.layout.customlvmicrochip, null, true);

        ImageView closeimage=listViewItem.findViewById(R.id.imageclose);
        TextView txtmicrochip=listViewItem.findViewById(R.id.txtmicrochip);
        TextView txtowned=listViewItem.findViewById(R.id.txtowned);

        final Micros iteme = itemsList.get(position);

        if(iteme.getOwned().equals("0"))
        {
            txtowned.setText("");
        }

        txtmicrochip.setText(iteme.getMicrochip());
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

                                HashMap<String, String> params = new HashMap<>();
                                params.put("Id",iteme.getId());

                                NetworkRequest request=new NetworkRequest(Api.URL_DELETE_MICROCHIP,params,CODE_POST_REQUEST,getContext());
                                request.execute();

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
