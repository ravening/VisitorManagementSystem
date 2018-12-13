package com.leaseweb.global.visitormanagement.model;

public class Visitor {
    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private String reasonForVisit;
    private String contactPerson;
    private String licensePlate;
    private int badgeNumber;
    private String checkinTime;
    private String checkoutTime;

    public Visitor() {
    }

    public Visitor(String firstName, String lastName, String phone, String reasonForVisit, String contactPerson,
                   String licensePlate, int badgeNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.reasonForVisit = reasonForVisit;
        this.contactPerson = contactPerson;
        this.licensePlate = licensePlate;
        this.badgeNumber = badgeNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getReasonForVisit() {
        return reasonForVisit;
    }

    public void setReasonForVisit(String reasonForVisit) {
        this.reasonForVisit = reasonForVisit;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public int getBadgeNumber() {
        return badgeNumber;
    }

    public void setBadgeNumber(int badgeNumber) {
        this.badgeNumber = badgeNumber;
    }

    public String getCheckinTime() {
        return checkinTime;
    }

    public void setCheckinTime(String checkinTime) {
        this.checkinTime = checkinTime;
    }

    public String getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(String checkoutTime) {
        this.checkoutTime = checkoutTime;
    }
}
