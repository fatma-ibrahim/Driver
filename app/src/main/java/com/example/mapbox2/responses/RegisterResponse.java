package com.example.mapbox2.responses;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RegisterResponse {

    @SerializedName("data")
    private Data data;

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public class Data {

        private String driver_name;
        private String SchoolName;
        ArrayList < Object > email = new ArrayList < Object > ();
        ArrayList < Object > password = new ArrayList < Object > ();
        ArrayList < Object > licenseNumber = new ArrayList < Object > ();
        ArrayList < Object > code = new ArrayList < Object > ();
        ArrayList < Object > mobileNumber = new ArrayList < Object > ();
        ArrayList < Object > name = new ArrayList < Object > ();

        public String getDriver_name() {
            return driver_name;
        }

        public ArrayList<Object> getMobileNumber() {
            return mobileNumber;
        }

        public ArrayList<Object> getCode() {
            return code;
        }

        public ArrayList<Object> getName() {
            return name;
        }

        public String getSchoolName() {
            return SchoolName;
        }

        public ArrayList<Object> getEmail() {
            return email;
        }

        public ArrayList<Object> getPassword() {
            return password;
        }

        public ArrayList<Object> getLicenseNumber() {
            return licenseNumber;
        }
    }
}