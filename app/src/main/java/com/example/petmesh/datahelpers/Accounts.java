package com.example.petmesh.datahelpers;

import android.graphics.Bitmap;

public class Accounts {
    private String id;
    private String Firstname,Middlename,Lastname, Username,Password,Status,Type,EmailAddress,MobileNo,PhoneNo,VetId;
    Bitmap ImageData;

    public Accounts(String id, String Firstname,String Middlename,String Lastname, String EmailAddress,String MobileNo,String PhoneNo,String Username, String Password,String Status,String Type,String VetId,Bitmap ImageData) {
        this.id = id;
        this.Firstname = Firstname;
        this.Middlename=Middlename;
        this.Lastname=Lastname;
        this.Username = Username;
        this.Password = Password;
        this.Status=Status;
        this.Type=Type;
        this.EmailAddress=EmailAddress;
        this.MobileNo=MobileNo;
        this.PhoneNo=PhoneNo;
        this.VetId=VetId;
        this.ImageData=ImageData;
    }

    public String getId() {
        return id;
    }

    public String getFirstname() {
        return Firstname;
    }

    public String getMiddlename() {
        return Middlename;
    }

    public String getLastname() {
        return Lastname;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }

    public String getStatus() {
        return Status;
    }

    public String getEmailAddress() {
        return EmailAddress;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public String getPhoneNo() {
        return PhoneNo;
    }

    public String getType() {
        return Type;
    }

    public String getVetId() { return VetId; }

    public Bitmap getImageData(){return ImageData;}



}