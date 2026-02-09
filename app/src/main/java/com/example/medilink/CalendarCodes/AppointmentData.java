package com.example.medilink.CalendarCodes;

public class AppointmentData {

    private String clientName;
    private String client_phone;
    private String eventDate;
    private String eventTime_Start;
    private String eventTime_Finish;
    private String key;


    // Empty constructor for Firebase
    public AppointmentData() {
    }

    // Constructor with parameters
    public AppointmentData(String clientName, String clientPhone, String eventDate, String eventTime_Start, String eventTime_Finish) {
        this.clientName = clientName;
        this.client_phone = clientPhone;
        this.eventDate = eventDate;
        this.eventTime_Start = eventTime_Start;
        this.eventTime_Finish = eventTime_Finish;
    }

    // Getters and setters
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClient_phone() {
        return client_phone;
    }

    public void setClientPhone(String client_phone_NO) {
        this.client_phone = client_phone_NO;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime_Start() {
        return eventTime_Start;
    }

    public void setEventTime_Start(String eventTime_Start) {
        this.eventTime_Start = eventTime_Start;
    }

    public String getEventTime_Finish() {
        return eventTime_Finish;
    }

    public void setEventTime_Finish(String eventTime_Finish) {
        this.eventTime_Finish = eventTime_Finish;
    }

}
