package com.example.tholok.lab04;

/**
 * Created by tholok on 03.04.18.
 */

public class Message {

    public String d;
    public String u;
    public String m;

    public Message() {
        // Required by firebase for some reason
    }

    public Message(String d, String u, String m) {
        this.d = d;
        this.u = u;
        this.m = m;
    }
}
