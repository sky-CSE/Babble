package com.example.babble;
//The hashmap at the firebase are model class
public class ModelClass {

    public String from;
    public String message;

    public ModelClass(){}

    public ModelClass(String from,String message) {
        this.from = from;
        this.message = message;
    }

    public String getFrom() {
        return from;
    }
    public String getMessage() {
        return message;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
