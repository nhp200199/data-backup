package com.example.databackup.backup.model;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

public class BackUpData {
    long backUpDate;
    List<Contact> contacts = new ArrayList<Contact>();
    List<SmsModel> smsList = new ArrayList<SmsModel>();
    List<CallLogModel> callLogs = new ArrayList<CallLogModel>();

    public BackUpData() {}

    public BackUpData(long backUpDate, List<Contact> contacts, List<SmsModel> smsList, List<CallLogModel> callLogs) {
        this.contacts = contacts;
        this.smsList = smsList;
        this.callLogs = callLogs;
        this.backUpDate = backUpDate;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<SmsModel> getSmsList() {
        return smsList;
    }

    public void setSmsList(List<SmsModel> smsList) {
        this.smsList = smsList;
    }

    public List<CallLogModel> getCallLogs() {
        return callLogs;
    }

    public void setCallLogs(List<CallLogModel> callLogs) {
        this.callLogs = callLogs;
    }

    public long getBackUpDate() {
        return backUpDate;
    }

    public void setBackUpDate(long backUpDate) {
        this.backUpDate = backUpDate;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
    
    public static BackUpData fromJson(String jsonString) {
        try {
            return new Gson().fromJson(jsonString, BackUpData.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return new BackUpData();
        }
    }
}
