package com.example.databackup.backup.model;

public class SmsModel {
    String body;
    long date;
    int type;

    public SmsModel(String body, long date, int type) {
        this.body = body;
        this.date = date;
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SmsModel{" +
                "body='" + body + '\'' +
                ", receivedDate=" + date +
                ", type=" + type +
                '}';
    }
}
