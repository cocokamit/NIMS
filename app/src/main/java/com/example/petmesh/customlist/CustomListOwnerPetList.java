package com.example.petmesh.customlist;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.petmesh.R;
import com.example.petmesh.datahelpers.Pets;

import java.text.DecimalFormat;
import java.util.List;

public class CustomListOwnerPetList extends ArrayAdapter<Pets> {
    List<Pets> itemsList;
    Context context;
    public CustomListOwnerPetList(Context context,int LayoutId, List<Pets> itemsList) {
        super(context, LayoutId, itemsList);
        this.itemsList = itemsList;
        this.context=context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View listViewItem = inflater.inflate(R.layout.customlvperownerpet, null, true);

        ImageView txtpetimage=listViewItem.findViewById(R.id.petimage);
        TextView txtpetname=listViewItem.findViewById(R.id.petname);
        TextView txtpetmicrochip = listViewItem.findViewById(R.id.petmicroship);
        TextView txtspecie = listViewItem.findViewById(R.id.petspecie);

        final Pets iteme = itemsList.get(position);

        txtpetname.setText(iteme.getName());
        txtpetmicrochip.setText(iteme.getMicrochip());
        txtspecie.setText(iteme.getSpecie());
        txtpetimage.setImageBitmap(iteme.getImageData());
        return listViewItem;
    }
}