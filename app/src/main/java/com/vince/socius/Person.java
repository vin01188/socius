package com.vince.socius;

/**
 * Created by Vince on 9/9/16.
 */

public class Person {
    private String address;
    private double lattitude;
    private double longitude;
    private String time;
    private String poster;

    public Person (){
        address = "";
        lattitude = 0.0;
        longitude = 0.0;
        time ="";
        poster = "";
    }

    public Person (String address, double lattitude, double longitude,String time, String poster){
        this.address = address;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.time = time;
        this.poster = poster;
    }


    public String getAddress(){
        return address;
    }

    public double getLattitude(){
        return lattitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public String getTime(){
        return time;
    }

    public String getPoster() { return poster; }

    public void setAddress(String address){
        this.address = address;
    }

    public void setLattitude(double lattitude){
        this.lattitude = lattitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public void setTime(String time){
        this.time = time;
    }

    public void setPoster(String poster) { this.poster = poster; }

}
