package com.example.cameratrapmanager;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class TrapList {
    String number;
    String signal, storage, battery;
    private LatLng pos;
    private ArrayList<String> logCommand;
    public TrapList(String number){
        this.number = number;
        this.signal = "0";
        this.storage = "0";
        this.battery = "0";
        this.pos = new LatLng(0,0);
    }
    public TrapList(String number, LatLng pos){
        this.number = number;
        this.signal = "0";
        this.storage = "0";
        this.battery = "0";
        this.pos = pos;
        logCommand = new ArrayList<String>();
    }

    public void setNumber(String number) {
        this.number = number;
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
    public void setPos(LatLng pos){
        this.pos = pos;
    }
    public void setLogCommand(ArrayList<String> logCommand){
        this.logCommand = logCommand;
    }

    public String getNumber() {
        return number;
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
    public LatLng getPos(){ return pos; }
    public ArrayList<String> getLogCommand(){
        return logCommand;
    }
    public void addLogCommand(String log){
        this.logCommand.add(0,log);
        if(this.logCommand.size() > 6){
            logCommand.remove(6);
        }
    }
    public void clearLogCommand(){
        logCommand.removeAll(logCommand);
    }
}
