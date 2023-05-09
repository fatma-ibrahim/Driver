package com.example.mapbox2.models;

import com.google.gson.annotations.SerializedName;

public class School{

	@SerializedName("code")
	private String code;

	@SerializedName("lng")
	private String lng;

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("lit")
	private String lit;

	@SerializedName("name")
	private String name;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("id")
	private int id;

	public String getCode(){
		return code;
	}

	public String getLng(){
		return lng;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public String getLit(){
		return lit;
	}

	public String getName(){
		return name;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public int getId(){
		return id;
	}
}