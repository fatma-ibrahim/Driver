package com.example.mapbox2.models;

import com.google.gson.annotations.SerializedName;

public class FathersItem{

	@SerializedName("lng")
	private String lng;

	@SerializedName("lit")
	private String lit;

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;

	public String getLng(){
		return lng;
	}

	public String getLit(){
		return lit;
	}

	public String getName(){
		return name;
	}

	public int getId(){
		return id;
	}
}