package com.example.medilink.Patientside;

public class DoctorData {


    private String photo;
    private String name;
    private String phoneNumber;
    private String key;





    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
    // Getters and setters
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    // Getters and Setters for each field
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // Constructor
    public DoctorData(String photo,String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.photo = photo;

    }
    public DoctorData() {

    }


}
