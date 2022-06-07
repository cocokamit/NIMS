package com.example.petmesh.datahelpers;

import android.os.Parcel;
import android.os.Parcelable;

public class Searches implements Parcelable {
    private String name,type;

    public Searches(String name,String type)
    {
        this.name=name;
        this.type=type;
    }

    protected Searches(Parcel in) {
        name = in.readString();
        type = in.readString();
    }

    public static final Creator<Searches> CREATOR = new Creator<Searches>() {
        @Override
        public Searches createFromParcel(Parcel in) {
            return new Searches(in);
        }

        @Override
        public Searches[] newArray(int size) {
            return new Searches[size];
        }
    };

    public String getName() { return name; }
    public String getType() { return type; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);
    }
}
