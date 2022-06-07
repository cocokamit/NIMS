package com.example.petmesh.customlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.petmesh.R;
import com.example.petmesh.datahelpers.PetSearches;
import com.example.petmesh.datahelpers.Pets;

import java.util.List;

public class CustomListOwnerPetListSearches extends ArrayAdapter<PetSearches> {
    List<PetSearches> itemsList;
    Context context;
    public CustomListOwnerPetListSearches(Context context,int LayoutId, List<PetSearches> itemsList) {
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
        TextView ownername = listViewItem.findViewById(R.id.ownername);

        final PetSearches iteme = itemsList.get(position);

        txtpetname.setText("Name: "+iteme.getName());
        txtpetmicrochip.setText("Chip No."+iteme.getMicrochip());
        ownername.setText(iteme.getFullname());
        txtpetimage.setImageBitmap(iteme.getImageData());
        return listViewItem;
    }
}
