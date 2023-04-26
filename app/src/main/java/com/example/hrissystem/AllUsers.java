package com.example.hrissystem;

public class AllUsers {
    String name,fname,cnic,contact,designation,district,password;
    boolean isAdmin;



    public AllUsers(String name, String fname, String cnic, String contact, String designation, String ditrict, String password) {
        this.name = name;
        this.fname = fname;
        this.cnic = cnic;
        this.contact = contact;
        this.designation = designation;
        this.district = ditrict;
        this.isAdmin=false;
        this.password=password;
    }

    public AllUsers() {

    }

    public String getName() {
        return name;
    }

    public String getFname() {
        return fname;
    }

    public String getCnic() {
        return cnic;
    }

    public String getContact() {
        return contact;
    }

    public String getDesignation() {
        return designation;
    }

    public String getDistrict() {
        return district;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
