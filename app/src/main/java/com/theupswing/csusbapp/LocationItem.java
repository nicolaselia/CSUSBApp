package com.theupswing.csusbapp;

/**
 * You probably won't need to edit this class in the future except if you want to change the structure of the list of locations in ListOfPlaces
 */

// Functions to interact with the LocationItem objects used in the ListOfPlaces class
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
