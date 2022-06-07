package com.example.petmesh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petmesh.datahelpers.Keystore;

public class Administrator extends AppCompatActivity {
    private CardView btnPets,btnInventory,btnDoctor;
    private Button btnlogout;
    Keystore store;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrator);

        store=Keystore.getInstance(getApplicationContext());
        btnPets=findViewById(R.id.btnPets);
        btnlogout=findViewById(R.id.btnlogout);
        btnInventory=findViewById(R.id.btnInventory);
        btnDoctor=findViewById(R.id.btnDoctor);

        btnPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Administrator.this, PetList.class);
                startActivity(i);
                Administrator.this.overridePendingTransition(R.anim.slide_right, R.anim.slide_up);
            }
        });

        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Administrator.this, Login2.class);
                startActivity(i);
                Keystore store=Keystore.getInstance(getApplicationContext());
                store.clear();
                Administrator.this.finishAffinity();
            }
        });

        btnInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Administrator.this, Microchips.class);
                startActivity(i);
                Administrator.this.overridePendingTransition(R.anim.slide_right, R.anim.slide_up);
            }
        });
        btnDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Administrator.this, DoctorList.class);
                startActivity(i);
                Administrator.this.overridePendingTransition(R.anim.slide_right, R.anim.slide_up);
            }
        });
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {

            finishAffinity();
            System.exit(0);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}