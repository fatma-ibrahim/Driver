package com.example.mapbox2.responses;

import com.example.mapbox2.models.FathersItem;
import com.example.mapbox2.models.School;
import com.example.mapbox2.models.Trip;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StartTripResponse {

	@SerializedName("data")
	private Data data;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public Data getData(){
		return data;
	}

	public boolean isSuccess(){
		return success;
	}

	public String getMessage(){
		return message;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public class Data{

		@SerializedName("fathers")
		private List<FathersItem> fathers;

		@SerializedName("trip")
		private Trip trip;

		@SerializedName("school")
		private School school;

		public List<FathersItem> getFathers(){
			return fathers;
		}

		public Trip getTrip(){
			return trip;
		}

		public School getSchool(){
			return school;
		}
	}
}