package com.example.mapbox2.models;

import com.google.gson.annotations.SerializedName;

public class Trip{

	@SerializedName("school_id")
	private int schoolId;

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("geofence")
	private String geofence;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private int id;

	@SerializedName("status")
	private int status;

	public int getSchoolId(){
		return schoolId;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public String getGeofence(){
		return geofence;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public int getId(){
		return id;
	}

	public int getStatus(){
		return status;
	}
}