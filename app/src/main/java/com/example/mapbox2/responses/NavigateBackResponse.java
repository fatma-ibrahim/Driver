package com.example.mapbox2.responses;

import com.example.mapbox2.models.FathersItem;
import com.example.mapbox2.models.Trip;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NavigateBackResponse{

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

		public List<FathersItem> getFathers(){
			return fathers;
		}

		public Trip getTrip(){
			return trip;
		}
	}
}