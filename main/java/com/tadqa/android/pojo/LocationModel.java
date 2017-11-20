package com.tadqa.android.pojo;

public class LocationModel {

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }

    public String getPostal(){return postal;}

    public void setPostal(String postalcode)
    {
        postal=postalcode;
    }
    public String getAddress(){return Address;}
    public void  setAddress(String address){this.Address=address;}

    public String getLandmark(){return Landmark;}
    public void  setLandmark(String landmark){this.Landmark=landmark;}


    String City;
    String State;
    String Area;
    String postal;
    String Address,Landmark;

}
