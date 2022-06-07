package com.example.petmesh.customlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petmesh.Inventory;
import com.example.petmesh.R;
import com.example.petmesh.datahelpers.InventoryItems;
import com.example.petmesh.datahelpers.Pets;

import java.util.List;

public class AdpaterRec extends RecyclerView.Adapter<AdpaterRec.ViewHolder> {

    List<InventoryItems> itemsList;
    Context context;
    LayoutInflater inflater;
    public AdpaterRec(Context context, List<InventoryItems> itemsList) {
        this.itemsList = itemsList;
        this.context=context;
        this.inflater=LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=inflater.inflate(R.layout.customgridlayout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final InventoryItems iteme = itemsList.get(position);
        holder.txtitemname.setText(iteme.getName());
        holder.itemimage.setImageBitmap(iteme.getImageData());
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtitemname;
        ImageView itemimage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtitemname=itemView.findViewById(R.id.txtitemname);
            itemimage=itemView.findViewById(R.id.itemimage);

        }
    }
}
