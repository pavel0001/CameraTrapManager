package com.example.cameratrapmanager;

public class TrapList {
    String number, location;
    String signal, storage, battery;
    public TrapList(String number, String location){
        this.number = number;
        this.location = location;
        this.signal = "0";
        this.storage = "0";
        this.battery = "0";

    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSignal(String signal) {
        this.signal = signal;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getNumber() {
        return number;
    }

    public String getLocation() {
        return location;
    }

    public String getSignal() {
        return signal;
    }

    public String getStorage() {
        return storage;
    }

    public String getBattery() {
        return battery;
    }
}
