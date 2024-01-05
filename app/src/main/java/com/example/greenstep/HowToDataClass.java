package com.example.greenstep;

public class HowToDataClass {
    private String documentId, title, sourceRef,imageURL;
    public HowToDataClass(){}

    public HowToDataClass(String documentId, String title, String sourceRef, String imageURL) {
        this.documentId = documentId;
        this.title = title;
        this.sourceRef = sourceRef;
        this.imageURL = imageURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSourceRef() { return sourceRef; }

    public void setSourceRef(String sourceRef) {this.sourceRef = sourceRef; }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDocumentId(){ return documentId; }


}
