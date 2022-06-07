package com.example.petmesh.owners;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.petmesh.R;
import com.example.petmesh.datahelpers.Keystore;

public class OwnerItemInfo extends AppCompatActivity {
    Keystore store;
    ImageView imagepetid,imagedelete;
    EditText txtname,txtdescription,txtquantity,txtprice;
    Bitmap bitmap=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_item_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        store= Keystore.getInstance(this);
        txtname=findViewById(R.id.txtname);
        txtdescription=findViewById(R.id.txtdescription);
        txtquantity=findViewById(R.id.txtquantity);
        txtprice=findViewById(R.id.txtprice);
        imagepetid=findViewById(R.id.itemimage);

        byte[] decodedString = Base64.decode(store.get("iImageData"), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        bitmap=decodedByte;
        imagepetid.setImageBitmap(decodedByte);
        txtname.setText(store.get("iName"));
        txtdescription.setText(store.get("iDescription"));
        txtquantity.setText(store.get("iQuantity"));
        txtprice.setText(store.get("iPrice"));
    }
}