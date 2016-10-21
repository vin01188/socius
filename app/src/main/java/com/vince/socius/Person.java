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
    public String description;
    private boolean isNotDelete;
    private int number;

    public Person (){
        address = "";
        lattitude = 0.0;
        longitude = 0.0;
        time ="";
        poster = "";
        isNotDelete = true;
        number = 0;
    }

    public Person (String address, double lattitude, double longitude,String time, String poster, String description,int number){
        this.address = address;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.time = time;
        this.poster = poster;
        this.isNotDelete = true;
        this.description = description;
        this.number = number;
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

    public String getDescription() { return description; }

    public int getNumber() {return number;}

    public String getTime(){
        return time;
    }

    public String getPoster() { return poster; }

    public boolean getIsNotDelete() {return isNotDelete;}

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

    public void setNumber(int number){this.number = number;}

    public void setDescription (String description) { this.description = description; }

    public void setIsNotDelete(boolean bool){
        this.isNotDelete = bool;
    }
    public void delete(){
        isNotDelete = false;
    }

}
