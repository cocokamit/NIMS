package com.example.petmesh.datahelpers;

import android.graphics.Bitmap;

public class Pets {
    private int id;
    private String Name, Microchip,Breed,Gender,DateofBirth,Age,Weight,Coat,Specie,AntiRabiesSerialno,AntiRabiesDate,Vaccinationno,Veterinarian,PRCandValidity;
    private Bitmap ImageData;
    public Pets(int id, String Name,String Mircrochip, String Breed,String Gender,String DateofBirth,String Age,String Weight, String Coat,String Specie,String AntiRabiesSerialno,String AntiRabiesDate,String Vaccinationno,String Veterinarian,String PRCandValidity,Bitmap ImageData) {
        this.id = id;
        this.Name=Name;
        this.Microchip=Mircrochip;
        this.Breed=Breed;
        this.Gender=Gender;
        this.DateofBirth=DateofBirth;
        this.Age=Age;
        this.Weight=Weight;
        this.Coat=Coat;
        this.Specie=Specie;
        this.AntiRabiesDate=AntiRabiesDate;
        this.AntiRabiesSerialno=AntiRabiesSerialno;
        this.Vaccinationno=Vaccinationno;
        this.Veterinarian=Veterinarian;
        this.PRCandValidity=PRCandValidity;
        this.ImageData=ImageData;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return Name;
    }

    public String getMicrochip() {
        return Microchip;
    }

    public String getBreed() {
        return Breed;
    }

    public String getGender() {
        return Gender;
    }

    public String getDateofBirth() {
        return DateofBirth;
    }

    public String getAge() {
        return Age;
    }

    public String getWeight() {
        return Weight;
    }

    public String getCoat() {
        return Coat;
    }

    public String getSpecie() {
        return Specie;
    }

    public String getAntiRabiesSerialno() {
        return AntiRabiesSerialno;
    }

    public String getAntiRabiesDate() {
        return AntiRabiesDate;
    }

    public String getVaccinationno() {
        return Vaccinationno;
    }

    public String getVeterinarian() {
        return Veterinarian;
    }

    public String getPRCandValidity() {
        return PRCandValidity;
    }

    public Bitmap getImageData(){return ImageData;}


}
