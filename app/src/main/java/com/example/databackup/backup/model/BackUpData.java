package com.example.databackup.backup.model;

import java.util.ArrayList;
import java.util.List;

public class BackUpData {
    List<Contact> contacts = new ArrayList<Contact>();
    List<SmsModel> smsList = new ArrayList<SmsModel>();
    List<CallLogModel> callLogs = new ArrayList<CallLogModel>();

    public BackUpData(List<Contact> contacts, List<SmsModel> smsList, List<CallLogModel> callLogs) {
        this.contacts = contacts;
        this.smsList = smsList;
        this.callLogs = callLogs;
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
}
