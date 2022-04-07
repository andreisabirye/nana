package com.dlc.nana;

public class Request {
    private String request_provider_name, request_provider_email, request_seeker_name, request_seeker_email, request_provider_location, request_seeker_location, request_rate, request_status, request_bill, request_service, request_date, request_time, request_quantity, request_rating, request_id, request_seeker_photo, request_provider_photo, request_provider_token, request_seeker_token;

    public Request(){
    }

    public Request(String request_seeker_name, String request_provider_name, String request_seeker_email, String request_provider_email, String request_seeker_location, String request_provider_location, String request_rate, String request_status, String request_date, String request_time, String request_bill, String request_quantity, String request_service, String request_rating, String request_id, String request_seeker_photo, String request_provider_photo, String request_provider_token, String request_seeker_token){
        this.request_bill = request_bill;
        this.request_date = request_date;
        this.request_provider_email = request_provider_email;
        this.request_provider_location = request_provider_location;
        this.request_provider_name = request_provider_name;
        this.request_quantity = request_quantity;
        this.request_rate = request_rate;
        this.request_seeker_email = request_seeker_email;
        this.request_seeker_location = request_seeker_location;
        this.request_seeker_name = request_seeker_name;
        this.request_service = request_service;
        this.request_status = request_status;
        this.request_time = request_time;
        this.request_rating = request_rating;
        this.request_id = request_id;
        this.request_provider_photo = request_provider_photo;
        this.request_seeker_photo = request_seeker_photo;
        this.request_provider_token = request_provider_token;
        this.request_seeker_token = request_seeker_token;
    }

    public String getRequest_provider_email() { return request_provider_email; }
    public String getRequest_bill() { return request_bill; }
    public String getRequest_date() { return request_date; }
    public String getRequest_provider_location() { return request_provider_location; }
    public String getRequest_provider_name() { return request_provider_name; }
    public String getRequest_seeker_email() { return request_seeker_email; }
    public String getRequest_quantity() { return request_quantity; }
    public void setRequest_provider_email(String request_provider_email) { this.request_provider_email = request_provider_email; }
    public String getRequest_rate() { return request_rate; }
    public String getRequest_seeker_location() { return request_seeker_location; }
    public String getRequest_seeker_name() { return request_seeker_name; }
    public void setRequest_provider_location(String request_provider_location) {this.request_provider_location = request_provider_location; }
    public String getRequest_service() { return request_service; }
    public String getRequest_status() {return request_status; }
    public void setRequest_bill(String request_bill) { this.request_bill = request_bill; }
    public void setRequest_provider_name(String request_provider_name) { this.request_provider_name = request_provider_name; }
    public String getRequest_time() { return request_time; }
    public void setRequest_date(String request_date) { this.request_date = request_date; }
    public void setRequest_rate(String request_rate) { this.request_rate = request_rate; }
    public void setRequest_quantity(String request_quantity) { this.request_quantity = request_quantity; }
    public void setRequest_seeker_email(String request_seeker_email) { this.request_seeker_email = request_seeker_email; }
    public void setRequest_seeker_location(String request_seeker_location) { this.request_seeker_location = request_seeker_location; }
    public void setRequest_seeker_name(String request_seeker_name) { this.request_seeker_name = request_seeker_name; }
    public void setRequest_service(String request_service) { this.request_service = request_service; }
    public void setRequest_status(String request_status) { this.request_status = request_status; }
    public void setRequest_time(String request_time) { this.request_time = request_time; }
    public String getRequest_rating() { return request_rating; }
    public void setRequest_rating(String request_rating) { this.request_rating = request_rating; }
    public String getRequest_id() { return request_id; }
    public void setRequest_id(String request_id) { this.request_id = request_id; }
    public String getRequest_provider_photo() { return request_provider_photo; }
    public String getRequest_seeker_photo() { return request_seeker_photo; }
    public void setRequest_provider_photo(String request_provider_photo) { this.request_provider_photo = request_provider_photo; }
    public void setRequest_seeker_photo(String request_seeker_photo) { this.request_seeker_photo = request_seeker_photo; }
    public String getRequest_provider_token() { return request_provider_token; }
    public void setRequest_provider_token(String request_provider_token) { this.request_provider_token = request_provider_token; }
    public String getRequest_seeker_token() { return request_seeker_token; }
    public void setRequest_seeker_token(String request_seeker_token) { this.request_seeker_token = request_seeker_token; }
}
