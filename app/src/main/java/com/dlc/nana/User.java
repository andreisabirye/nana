package com.dlc.nana;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    private String user_name, user_surname, user_email, user_dob, user_city, user_photo, user_gender, user_number, token;

    public User(){}

    public User(String user_name, String user_surname, String user_email, String user_dob, String user_city, String user_photo, String user_gender, String user_number, String token){
        this.user_name = user_name;
        this.user_surname = user_surname;
        this.user_email = user_email;
        this.user_dob = user_dob;
        this.user_city = user_city;
        this.user_photo = user_photo;
        this.user_gender = user_gender;
        this.user_number = user_number;
        this.token = token;
    }

    public String getUser_city() { return user_city; }
    public String getUser_dob() { return user_dob; }
    public String getUser_email() { return user_email; }
    public String getUser_name() { return user_name; }
    public String getUser_surname() { return user_surname; }
    public void setUser_email(String user_email) { this.user_email = user_email; }
    public void setUser_name(String user_name) { this.user_name = user_name; }
    public void setUser_city(String user_city) { this.user_city = user_city; }
    public void setUser_dob(String user_dob) { this.user_dob = user_dob; }
    public void setUser_surname(String user_surname) { this.user_surname = user_surname; }
    public String getUser_gender() { return user_gender; }
    public String getUser_photo() { return user_photo; }
    public void setUser_photo(String user_photo) { this.user_photo = user_photo; }
    public void setUser_gender(String user_gender) { this.user_gender = user_gender; }
    public String getUser_number() { return user_number; }
    public void setUser_number(String user_number) { this.user_number = user_number; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
