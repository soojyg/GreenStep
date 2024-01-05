package com.example.greenstep;

public class AlternativesDataClass {
    private String documentID, title, tip1, tip2, imageURL;

    public AlternativesDataClass(){}

    public AlternativesDataClass(String documentID, String title, String tip1, String tip2, String imageURL){
        this.documentID = documentID;
        this.title = title;
        this.tip1 = tip1;
        this.tip2 = tip2;
        this.imageURL = imageURL;
    }

    public String getDocumentID() {
        return documentID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTip1() {
        return tip1;
    }

    public void setTip1(String tip1) {
        this.tip1 = tip1;
    }

    public String getTip2() {
        return tip2;
    }

    public void setTip2(String tip2) {
        this.tip2 = tip2;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDocumentId() {
        return documentID;
    }
}
