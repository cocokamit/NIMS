package com.example.petmesh.datahelpers;

import android.graphics.Bitmap;

public class Micros {
    private String Id,Microchip,Sysdate,Owned;

    public Micros(String Id, String Microchip, String Sysdate,String Owned)
    {
        this.Id=Id;
        this.Microchip=Microchip;
        this.Sysdate=Sysdate;
        this.Owned=Owned;

    }
    public String getId(){return Id;}
    public String getMicrochip(){return Microchip;}
    public String getSysdate(){return Sysdate;}
    public String getOwned(){return Owned;}

}
