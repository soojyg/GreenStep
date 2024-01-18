package com.example.greenstep;

public class ReadWriteUserDetails {
    public String name, email, password;
    String username, dob, gender, country,contactNo;
    public int pointCollected;

    public String getName() {
        return name;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPointsCollected(int i) {this.pointCollected=pointCollected;}
    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public void setDob(String dob) {
        this.dob = dob;
    }


    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getDob() {
        return dob;
    }
    public String getGender() {
        return gender;
    }
    public String getContactNo() {
        return contactNo;
    }
    public String getCountry() {
        return country;
    }
}
