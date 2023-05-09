package com.example.mapbox2.responses;

import java.util.List;

import com.example.mapbox2.models.Driver;
import com.google.gson.annotations.SerializedName;

public class ShowUserResponse{

	@SerializedName("data")
	private Driver data;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public void setData(Driver data) {
		this.data = data;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Driver getData(){
		return data;
	}

	public boolean isSuccess(){
		return success;
	}

	public String getMessage(){
		return message;
	}
}