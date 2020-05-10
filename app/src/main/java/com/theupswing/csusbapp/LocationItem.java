package com.theupswing.csusbapp;

public class LocationItem {

    private String location;
    private int checkboxResource;

    public LocationItem(String location, int checkboxResource) {
        this.checkboxResource = checkboxResource;
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public int getCheckboxResource() {
        return checkboxResource;
    }

    public void setCheckboxResource(int checkboxResource) {
        this.checkboxResource = checkboxResource;
    }
}
