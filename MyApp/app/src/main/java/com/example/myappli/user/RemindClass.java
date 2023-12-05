package com.example.myappli.user;

public class RemindClass {
    boolean hasSent;
    int reminderId;
    String message;
    String time;

    public RemindClass(int reminderId, String message, String time, boolean hasSent) {
        this.reminderId=reminderId;
        this.message=message;
        this.time=time;
        this.hasSent=hasSent;
    }
}
