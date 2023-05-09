package com.example.mapbox2.responses;

import java.util.List;

import com.example.mapbox2.models.FatherAttendence;
import com.google.gson.annotations.SerializedName;

public class AttendenceResponse{

	@SerializedName("data")
	private List<FatherAttendence> data;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public void setData(List<FatherAttendence> data){
		this.data = data;
	}

	public List<FatherAttendence> getData(){
		return data;
	}

	public void setSuccess(boolean success){
		this.success = success;
	}

	public boolean isSuccess(){
		return success;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}
}