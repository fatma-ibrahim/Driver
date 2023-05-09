package com.example.mapbox2.models;

import com.google.gson.annotations.SerializedName;

public class Driver{

	@SerializedName("trip_id")
	private int tripId;

	@SerializedName("school_id")
	private int schoolId;

	@SerializedName("school_code")
	private String schoolCode;


	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("image_path")
	private String imagePath;

	@SerializedName("mobileNumber")
	private String mobileNumber;

	@SerializedName("name")
	private String name;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("licenseNumber")
	private String licenseNumber;

	@SerializedName("id")
	private int id;

	//@SerializedName("confirmed")
//	private boolean confirmed;

	@SerializedName("email")
	private String email;

	public Driver(int schoolId, String mobileNumber, String name,int tripId, String licenseNumber, int id, String email,String image_path) {
		this.schoolId = schoolId;
		this.mobileNumber = mobileNumber;
		this.name = name;
		this.tripId = tripId;
		this.licenseNumber = licenseNumber;
		this.id = id;
		this.email = email;
		this.imagePath = image_path;
	}

	public String getSchoolCode() {
		return schoolCode;
	}

	public int getTripId(){
		return tripId;
	}

	public int getSchoolId(){
		return schoolId;
	}

	public String getUpdatedAt(){
		return updatedAt;
	}

	public String getImagePath(){
		return imagePath;
	}

	public String getMobileNumber(){
		return mobileNumber;
	}

	public String getName(){
		return name;
	}

	public String getCreatedAt(){
		return createdAt;
	}

	public String getLicenseNumber(){
		return licenseNumber;
	}

	public int getId(){
		return id;
	}

	/*public boolean getConfirmed(){
		return confirmed;
	}*/

	public String getEmail(){
		return email;
	}

}