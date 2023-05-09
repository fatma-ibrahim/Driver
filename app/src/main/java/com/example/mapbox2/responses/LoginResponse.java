package com.example.mapbox2.responses;

import com.example.mapbox2.models.Driver;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {

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

        @SerializedName("driver")
        private Driver driver;

        @SerializedName("token")
        private String token;

        public Driver getDriver() {
            return driver;
        }

        public String getToken() {
            return token;
        }
    }
}
