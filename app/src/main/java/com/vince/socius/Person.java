package com.vince.socius;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Vince on 9/9/16.
 */

public class Person implements Parcelable {
    private String address;
    private double lattitude;
    private double longitude;
    private String time;
    private String poster;
    public String description;
    private boolean isNotDelete;
    private String status;
    private int number;
    private String claimer;

    public Person() {
        address = "";
        lattitude = 0.0;
        longitude = 0.0;
        time = "";
        poster = "";
        isNotDelete = true;
        number = 0;
        status = "";
        claimer = "";
    }

    public Person(String address, double lattitude, double longitude, String time, String poster, String description, int number) {
        this.address = address;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.time = time;
        this.poster = poster;
        this.isNotDelete = true;
        this.description = description;
        this.number = number;
        this.status = "Open";
        this.claimer = "";
    }


    public String getAddress() {
        return address;
    }

    public double getLattitude() {
        return lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDescription() {
        return description;
    }

    public int getNumber() {
        return number;
    }

    public String getTime() {
        return time;
    }

    public String getPoster() {
        return poster;
    }

    public boolean getIsNotDelete() {
        return isNotDelete;
    }

    public String getStatus() { return status; }

    public String getClaimer() {return claimer; }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIsNotDelete(boolean bool) {
        this.isNotDelete = bool;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public void setClaimer(String claimer) {this.claimer = claimer; }

    public void delete() {
        isNotDelete = false;
    }


    protected Person(Parcel in) {
        address = in.readString();
        lattitude = in.readDouble();
        longitude = in.readDouble();
        time = in.readString();
        poster = in.readString();
        description = in.readString();
        isNotDelete = in.readByte() != 0x00;
        status = in.readString();
        number = in.readInt();
        claimer = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeDouble(lattitude);
        dest.writeDouble(longitude);
        dest.writeString(time);
        dest.writeString(poster);
        dest.writeString(description);
        dest.writeByte((byte) (isNotDelete ? 0x01 : 0x00));
        dest.writeString(status);
        dest.writeInt(number);
        dest.writeString(claimer);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };
}