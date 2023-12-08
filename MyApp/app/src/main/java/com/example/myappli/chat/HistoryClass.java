package com.example.myappli.chat;

import java.io.Serializable;

public class HistoryClass implements Serializable {
    public int userid;
    public String username;
    public int from;
    public int chatto;
    public String message;
    public String time;


    public HistoryClass(int userid,String username,int from,int chatto,String message,String time){
        this.userid=userid;
        this.username=username;
        this.from = from;
        this.chatto = chatto;
        this.message = message;
        this.time = time;
    }
}
