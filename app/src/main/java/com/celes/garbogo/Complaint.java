package com.celes.garbogo;

public class Complaint {
    public String userUid, subject, query, address, status, compID, latitude, longitude, markMap;

    public Complaint(){

    }
    public Complaint(String userUid, String subject, String query, String address, String status, String compID,
                     String latitude, String longitude, String markMap) {
        this.userUid = userUid;
        this.subject = subject;
        this.query = query;
        this.address = address;
        this.status = status;
        this.compID = compID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.markMap = markMap;
    }
}
