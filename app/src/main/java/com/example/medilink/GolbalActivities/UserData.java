package com.example.medilink.GolbalActivities;


public class UserData {


    private String User_registered_Name;
    private String User_registered_Email;
    private String User_registered_PhoneNo;
    private String User_registered_Password;
    private String User_registered_profile_photo;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public String getUser_registered_profile_photo() {
        return User_registered_profile_photo;
    }

    public String getUser_registered_Name() {
        return User_registered_Name;
    }

    public String getUser_registered_Email() {
        return User_registered_Email;
    }

    public String getUser_registered_PhoneNo() {
        return User_registered_PhoneNo;
    }

    public String getUser_registered_Password() {
        return User_registered_Password;
    }

    public void setUser_registered_profile_photo(String user_registered_profile_photo) {
        this.User_registered_profile_photo = user_registered_profile_photo;
    }

    public void setUser_registered_Name(String User_registered_Name) {
        this.User_registered_Name = User_registered_Name;
    }

    public void setUser_registered_Email(String User_registered_Email) {
        this.User_registered_Email = User_registered_Email;
    }

    public void setUser_registered_PhoneNo(String User_registered_PhoneNo) {
        this.User_registered_PhoneNo = User_registered_PhoneNo;
    }

    public void setUser_registered_Password(String User_registered_Password) {
        this.User_registered_Password = User_registered_Password;
    }


    public UserData(String User_registered_profile_photo,String User_registered_Name, String User_registered_Email, String User_registered_PhoneNo) {


        this.User_registered_Name = User_registered_Name;
        this.User_registered_Email = User_registered_Email;
        this.User_registered_PhoneNo = User_registered_PhoneNo;
        this.User_registered_profile_photo = User_registered_profile_photo;

    }


    public UserData() {

    }




}
