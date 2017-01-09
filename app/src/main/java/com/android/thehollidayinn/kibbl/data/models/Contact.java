
package com.android.thehollidayinn.kibbl.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Contact {

    @SerializedName("phone")
    @Expose
    private Object phone;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("address2")
    @Expose
    private Object address2;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("zip")
    @Expose
    private String zip;
    @SerializedName("fax")
    @Expose
    private Object fax;
    @SerializedName("address1")
    @Expose
    private String address1;

    public Object getPhone() {
        return phone;
    }

    public void setPhone(Object phone) {
        this.phone = phone;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Object getAddress2() {
        return address2;
    }

    public void setAddress2(Object address2) {
        this.address2 = address2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public Object getFax() {
        return fax;
    }

    public void setFax(Object fax) {
        this.fax = fax;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

}
