package com.example.petmesh.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.petmesh.DoctorList;
import com.example.petmesh.Inventory;
import com.example.petmesh.Login;
import com.example.petmesh.Login2;
import com.example.petmesh.MainActivity;
import com.example.petmesh.Microchips;
import com.example.petmesh.PetList;
import com.example.petmesh.R;
import com.example.petmesh.RegisterUser;
import com.example.petmesh.Reports;
import com.example.petmesh.datahelpers.Keystore;
import com.example.petmesh.ui.OwnerList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private CardView btnRegister,btnUsers,btnPets,btnInventory,btnDoctor,btnReports,btnInventory2;
    private TextView txtdrname;
    private Button btnlogout;
    private ImageView imagedocid;
    Keystore store;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        store=Keystore.getInstance(root.getContext());
        btnRegister=root.findViewById(R.id.btnRegister);
        btnUsers=root.findViewById(R.id.btnUsers);
        btnPets=root.findViewById(R.id.btnPets);
        txtdrname=root.findViewById(R.id.txtdrname);
        btnlogout=root.findViewById(R.id.btnlogout);
        btnInventory=root.findViewById(R.id.btnInventory);
        btnDoctor=root.findViewById(R.id.btnDoctor);
        btnReports=root.findViewById(R.id.btnReports);
        imagedocid=root.findViewById(R.id.imagedocid);
        btnInventory2=root.findViewById(R.id.btnInventory2);
        txtdrname.setText("Dr. "+store.get("Firstname")+" "+store.get("Middlename")+" "+store.get("Lastname"));
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(), RegisterUser.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_right, R.anim.slide_up);
            }
        });

        if(!store.get("ImageData").equals("2") && !store.get("ImageData").equals(""))
        {
            byte[] decodedString = Base64.decode(store.get("ImageData"), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imagedocid.setImageBitmap(decodedByte);
        }

        btnUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(), OwnerList.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_right, R.anim.slide_up);
            }
        });

        btnPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(), PetList.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_right, R.anim.slide_up);
            }
        });

        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(root.getContext(), Login2.class);
                startActivity(i);
                Keystore store=Keystore.getInstance(root.getContext());
                store.clear();
                getActivity().finishAffinity();
            }
        });

        btnInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(), Microchips.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_right, R.anim.slide_up);
            }
        });

        btnInventory2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(), Inventory.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_right, R.anim.slide_up);
            }
        });
        btnDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(), DoctorList.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_right, R.anim.slide_up);
            }
        });
        btnReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(), Reports.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_right, R.anim.slide_up);
            }
        });

        return root;
    }
}