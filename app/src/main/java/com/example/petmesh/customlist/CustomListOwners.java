package com.example.petmesh.customlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.petmesh.R;
import com.example.petmesh.datahelpers.Accounts;
import com.example.petmesh.datahelpers.Pets;

import java.util.List;

public class CustomListOwners extends ArrayAdapter<Accounts> {
    List<Accounts> itemsList;
    Context context;
    public CustomListOwners(Context context,int LayoutId, List<Accounts> itemsList) {
        super(context, LayoutId, itemsList);
        this.itemsList = itemsList;
        this.context=context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View listViewItem = inflater.inflate(R.layout.customlvownerlist, null, true);

        ImageView ownerimage=listViewItem.findViewById(R.id.ownerimage);
        TextView txtname=listViewItem.findViewById(R.id.txtname);
        TextView txtemailaddress = listViewItem.findViewById(R.id.txtemailaddress);
        TextView txtmobileno = listViewItem.findViewById(R.id.txtmobileno);

        final Accounts iteme = itemsList.get(position);

        txtname.setText(iteme.getFirstname()+" "+iteme.getMiddlename()+" "+iteme.getLastname());
        txtemailaddress.setText(iteme.getEmailAddress());
        txtmobileno.setText(iteme.getMobileNo());
if(iteme.getImageData()!=null) {
    ownerimage.setImageBitmap(iteme.getImageData());
}
        return listViewItem;
    }
}
