package com.example.petmesh.datahelpers;

public class Vaccination {
    private String Vaccinationno,ApplicationDate,Stockno,PetId,Id;
    public Vaccination(String Id,String Vaccinationno,String ApplicationDate,String Stockno,String PetId)
    {
        this.Vaccinationno=Vaccinationno;
        this.ApplicationDate=ApplicationDate;
        this.Stockno=Stockno;
        this.PetId=PetId;
        this.Id=Id;
    }

    public String getSerialno(){return Vaccinationno;}
    public String getApplicationDate(){return ApplicationDate;}
    public String getStockno(){return  Stockno;}
    public String getId(){return Id; }
    public String getPetId(){return PetId;}
}
