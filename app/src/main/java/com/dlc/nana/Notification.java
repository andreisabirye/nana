package com.dlc.nana;

import com.google.firebase.Timestamp;

public class Notification {
    private String for_email, from_email, from_name, from_photo, msg, status, request;
    private Timestamp time_sent;

    public Notification(){}

    public Notification(String for_email, String from_email, String from_name, String from_photo, String msg, Timestamp time_sent, String status, String request) {
        this.for_email = for_email;
        this.from_email = from_email;
        this.from_name = from_name;
        this.from_photo = from_photo;
        this.msg = msg;
        this.time_sent = time_sent;
        this.status = status;
        this.request = request;
    }

    public String getFor_email() { return for_email; }
    public String getFrom_email() { return from_email; }
    public String getFrom_name() { return from_name; }
    public void setFor_email(String for_email) { this.for_email = for_email; }
    public void setFrom_email(String from_email) { this.from_email = from_email;}
    public String getFrom_photo() { return from_photo; }
    public String getMsg() { return msg; }
    public String getStatus() { return status;}
    public void setFrom_name(String from_name) { this.from_name = from_name; }
    public Timestamp getTime_sent() { return time_sent; }
    public void setFrom_photo(String from_photo) { this.from_photo = from_photo; }
    public void setMsg(String msg) { this.msg = msg; }
    public void setStatus(String status) { this.status = status; }
    public void setTime_sent(Timestamp time_sent) { this.time_sent = time_sent; }
    public String getRequest() { return request; }
    public void setRequest(String request) { this.request = request; }
}
