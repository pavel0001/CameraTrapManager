package com.example.cameratrapmanager;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
@Entity
@TypeConverters({Converters.class})
public class TrapList {
    @NonNull
    @PrimaryKey
    private String number;
    private String signal, storage, battery;
    private Double latitude, longitude;
    private volatile String uri;
    @Ignore
    private boolean FLAG_LOAD = false;
    @Ignore
    private volatile ArrayList<Uri> listUri;
    @Ignore
    private LatLng pos;
    private ArrayList<String> logCommand;
    public TrapList(String number){
        this.number = number;
        this.signal = "0";
        this.storage = "0";
        this.battery = "0";
        this.pos = new LatLng(0,0);
        this.listUri = new ArrayList<Uri>();
        this.FLAG_LOAD = false;
    }
    public TrapList(String number, LatLng pos){
        this.number = number;
        this.signal = "0";
        this.storage = "0";
        this.battery = "0";
        this.pos = pos;
        this.latitude = pos.latitude;
        this.longitude = pos.longitude;
        logCommand = new ArrayList<String>();
        this.listUri = new ArrayList<Uri>();
        this.FLAG_LOAD = false;
    }
    public TrapList(String number, Double lat, Double lon){
        this.number = number;
        this.signal = "0";
        this.storage = "0";
        this.battery = "0";
        this.latitude = lat;
        this.longitude = lon;
        this.pos = new LatLng(lat, lon);
        logCommand = new ArrayList<String>();
        this.listUri = new ArrayList<Uri>();
        this.FLAG_LOAD = false;
    }

    public TrapList(@NonNull TrapList x) {
        this.number = x.number;
        this.signal = x.signal;
        this.storage = x.storage;
        this.battery = x.battery;
        this.latitude = x.latitude;
        this.longitude = x.longitude;
        this.uri = x.uri;
        this.pos = new LatLng(x.latitude, x.longitude);
        this.logCommand = x.logCommand;
        this.listUri = new ArrayList<Uri>();
        this.FLAG_LOAD = x.FLAG_LOAD;

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

    public synchronized String getUri() {
        return this.uri;
    }

    public synchronized ArrayList<Uri> getListUri() {
        return listUri;
    }

    public synchronized void setListUri(ArrayList<Uri> listUri) {
        this.listUri = listUri;
    }

    public synchronized void setUri(String uri) {
        this.uri = uri;
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

    public Double getLatitude() { return latitude; }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() { return longitude; }

    public boolean isFLAG_LOAD() {
        return FLAG_LOAD;
    }

    public void setFLAG_LOAD(boolean FLAG_LOAD) {
        this.FLAG_LOAD = FLAG_LOAD;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public LatLng getPos(){ return pos; }
    public ArrayList<String> getLogCommand(){
        return logCommand;
    }

    public void addLogCommand(String log) {
        Log.d("MyLog","AddLogCmd: "+ log);
        if (log == null) {
            return;
        }
        this.logCommand.add(0, log);
        if (this.logCommand.size() > 6) {
            logCommand.remove(6);
        }
    }

    public void clearLogCommand(){
        logCommand.removeAll(logCommand);
    }
    public void setStatusToTesting(){
        this.signal = "4 of 5";
        this.storage = "03%";
        this.battery = "3 of 5";
        this.latitude = 54.523;
        this.longitude = 28.351;
    }

    @NonNull
    @Override
    public String toString() {
        return "Camera "+this.number+" battery: "+battery+" signal: "+signal+" gps: lat-"+latitude+" long-"+longitude;
    }
}
