package com.example.petmesh.datahelpers;

public class PetStats {
        private String Entries,Sysdate;
    public PetStats(String Entries, String Sysdate)
    {
        this.Entries = Entries;
        this.Sysdate = Sysdate;
    }
    public String getEntries() {
        return Entries;
    }

    public String getSysdate() {
        return Sysdate;
    }

}
