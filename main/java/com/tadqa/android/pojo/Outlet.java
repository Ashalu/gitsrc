package com.tadqa.android.pojo;

/**
 * POJO (Plain Old Java Object) class for outlet.
 */

public class Outlet {

    String id = "";
    String state = "";
    String city = "";
    String pinCode = "";
    String area = "";
    String contact = "";
    boolean isHomeMade;
    boolean isTadqaTandoor;

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public boolean isHomeMade() {
        return isHomeMade;
    }

    public void setHomeMade(boolean homeMade) {
        isHomeMade = homeMade;
    }

    public boolean isTadqaTandoor() {
        return isTadqaTandoor;
    }

    public void setTadqaTandoor(boolean tadqaTandoor) {
        isTadqaTandoor = tadqaTandoor;
    }
}
