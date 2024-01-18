package com.example.greenstep;

public class DataClass {
   // Fields to store data properties
    private String dataTitle;   // Title of the data
    private String dataDesc;    // Description of the data
    private String dataLang;    // Language of the data
    private String dataImage;   // Image URL or reference of the data
    private String key;         // Key for identifying the data in the database

    // Getter and setter methods for the 'key' field
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    // Getter methods for the other data fields
    public String getDataTitle() {
        return dataTitle;
    }
    public String getDataDesc() {
        return dataDesc;
    }
    public String getDataLang() {
        return dataLang;
    }
    public String getDataImage() {
        return dataImage;
    }

    // Constructor to initialize the data fields
    public DataClass(String dataTitle, String dataDesc, String dataLang, String dataImage) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.dataLang = dataLang;
        this.dataImage = dataImage;
    }

    // Default constructor (required for Firebase)
    public DataClass(){
    }


}
