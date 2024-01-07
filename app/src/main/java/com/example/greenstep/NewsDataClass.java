package com.example.greenstep;

public class NewsDataClass {
    private String dataTitle;
    private String dataAuthor;
    private String dataLang;
    private String dataImage;
    private String dataDate;
    private String key;
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getDataTitle() {
        return dataTitle;
    }
    public String getDataAuthor() {
        return dataAuthor;
    }
    public String getDataLang() {
        return dataLang;
    }
    public String getDataImage() {
        return dataImage;
    }
    public String getDataDate() {
        return dataDate;
    }

    public NewsDataClass(String dataTitle, String dataAuthor, String dataLang, String dataImage, String dataDate) {
        this.dataTitle = dataTitle;
        this.dataAuthor = dataAuthor;
        this.dataLang = dataLang;
        this.dataImage = dataImage;
        this.dataDate = dataDate;
    }
    public NewsDataClass() {

    }

}