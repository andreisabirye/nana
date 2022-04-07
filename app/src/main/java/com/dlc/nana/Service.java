package com.dlc.nana;

import com.google.firebase.firestore.IgnoreExtraProperties;
import java.util.Map;

@IgnoreExtraProperties
public class Service {
    private String name, email, description, id, photo, location, service_name, service_price, service_number, service_type, gender, dob, token;
    private Map<String, Object> service_price_cars;
    private Map<String, Map<String, Object>> services;
    private double avgRating;

    public Service() {}

    public Service(String service_type, String name, String email, String description, String id, String photo, String location, Map<String, Map<String, Object>> services, double avgRating, String service_number, String gender, String dob, String token) {
        this.name = name;
        this.email = email;
        this.description = description;
        this.photo = photo;
        this.id = id;
        this.location = location;
        this.avgRating = avgRating;
        this.services = services;
        this.service_number = service_number;
        this.service_type = service_type;
        this.gender = gender;
        this.dob = dob;
        this.token = token;
    }

    public Service(String service_type, String name, String email, String description, String location, double rating, String id, String photo, Map<String, Object> service_price_cars, String service_name, String service_number, String gender, String dob, String token) {
        this.name = name;
        this.email = email;
        this.description = description;
        this.photo = photo;
        this.id = id;
        this.location = location;
        this.avgRating = rating;
        this.service_name = service_name;
        this.service_price_cars = service_price_cars;
        this.service_number = service_number;
        this.service_type = service_type;
        this.gender = gender;
        this.dob = dob;
        this.token = token;
    }

    public Service(String service_type, String name, String email, String description, String location, double rating, String id, String photo, String service_price, String service_name, String service_number, String gender, String dob, String token) {
        this.name = name;
        this.email = email;
        this.description = description;
        this.photo = photo;
        this.id = id;
        this.location = location;
        this.avgRating = rating;
        this.service_name = service_name;
        this.service_price = service_price;
        this.service_number = service_number;
        this.service_type = service_type;
        this.gender = gender;
        this.dob = dob;
        this.token = token;
    }

    public String getDob() { return dob; }

    public String getGender() { return gender; }

    public void setDob(String dob) { this.dob = dob; }

    public void setGender(String gender) { this.gender = gender; }

    public String getService_type() { return service_type; }

    public void setService_type(String service_type) { this.service_type = service_type; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public Map<String, Map<String, Object>> getServices() { return services; }

    public void setServices(Map<String, Map<String, Object>> services) { this.services = services; }

    public String getService_name() { return service_name; }

    public void setService_name(String service_name) { this.service_name = service_name; }

    public Object getService_price() { return service_price; }

    public void setService_price(String service_price) { this.service_price = service_price; }

    public Map<String, Object> getService_price_cars() { return service_price_cars; }

    public void setService_price_cars(Map<String, Object> service_price_cars) { this.service_price_cars = service_price_cars; }

    public String getService_number() { return service_number; }

    public void setService_number(String service_number) { this.service_number = service_number; }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }
}
