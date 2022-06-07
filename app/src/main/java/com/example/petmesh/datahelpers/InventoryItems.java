package com.example.petmesh.datahelpers;

import android.graphics.Bitmap;

public class InventoryItems {
    private String Id,Name,Description,Quantity,Category,Sysdate,Price;
    Bitmap ImageData;
    public InventoryItems(String Id, String Name,String Description,String Quantity,String Price,String Category,String Sysdate,Bitmap ImageData)
    {
        this.Id=Id;
        this.Name=Name;
        this.Description=Description;
        this.Quantity=Quantity;
        this.Category=Category;
        this.Sysdate=Sysdate;
        this.Price=Price;
        this.ImageData=ImageData;

    }
    public String getId() {
        return Id;
    }

    public String getName(){return Name;}

    public String getDescription(){return Description;}

    public String getQuantity(){return Quantity;}

    public String getCategory(){return Category;}

    public Bitmap getImageData(){return ImageData;}

    public String getSysdate() {
        return Sysdate;
    }
    public String getPrice() {
        return Price;
    }

}
