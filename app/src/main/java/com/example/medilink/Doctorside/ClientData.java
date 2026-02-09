package com.example.medilink.Doctorside;

public class ClientData {

    // Instance variables (attributes) for client data
    private String imClient;  // Image data as a base64 encoded string (can be used for image storage)
    private String nmClient;  // Client's name
    private String dsClient;  // Disease/Condition description
    private String ageClient; // Client's age
    private String sxClient;  // Client's gender
    private String pnClient;  // Client's phone number
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    // Getter methods to retrieve client data
    public String getImClient() {
        return imClient;
    }

    public String getPnClient() {
        return pnClient;
    }

    public String getSxClient() {
        return sxClient;
    }

    public String getAgeClient() {
        return ageClient;
    }

    public String getDsClient() {
        return dsClient;
    }

    public String getNmClient() {
        return nmClient;
    }

    // Setter methods (required by some frameworks for field mapping)
    public void setImClient(String imClient) {
        this.imClient = imClient;
    }

    public void setPnClient(String pnClient) {
        this.pnClient = pnClient;
    }

    public void setSxClient(String sxClient) {
        this.sxClient = sxClient;
    }

    public void setAgeClient(String ageClient) {
        this.ageClient = ageClient;
    }

    public void setDsClient(String dsClient) {
        this.dsClient = dsClient;
    }

    public void setNmClient(String nmClient) {
        this.nmClient = nmClient;
    }

    // Constructor to initialize the ClientData object with all necessary fields
    public ClientData(String imClient, String nmClient, String ageClient, String sxClient, String dsClient, String pnClient) {


        this.imClient = imClient;
        this.pnClient = pnClient;
        this.sxClient = sxClient;
        this.ageClient = ageClient;
        this.nmClient = nmClient;
        this.dsClient = dsClient;

        //Log.d("DEBUG", "ClientData Created: Name = " + nmClient + ", Phone = " + pnClient);
    }

    // Default constructor (useful for some scenarios, e.g., initializing an empty object before setting data)
    public ClientData() {
        // Empty constructor
    }




}
