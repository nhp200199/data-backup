package com.example.databackup.backup.model;

public class CallLogModel {
    long date;
    long duration;
    String callType;
    String number;

    public CallLogModel(long date, long duration, String callType, String number) {
        this.date = date;
        this.duration = duration;
        this.callType = callType;
        this.number = number;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "CallLog{" +
                "date=" + date +
                ", duration=" + duration +
                ", callType='" + callType + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}
