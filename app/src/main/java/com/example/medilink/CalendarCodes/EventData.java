package com.example.medilink.CalendarCodes;

public class EventData {

    private String eventName;
    private String eventDescription;
    private String eventDate;
    private String eventTime_Start;
    private String eventTime_Finish;
    private String key;

    // Empty constructor for Firebase
    public EventData() {
    }

    // Constructor with parameters
    public EventData(String eventName, String eventDescription, String eventDate, String eventTime_Start, String eventTime_Finish) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
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

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
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
