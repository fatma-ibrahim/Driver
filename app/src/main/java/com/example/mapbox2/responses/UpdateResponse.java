package com.example.mapbox2.responses;

import com.example.mapbox2.models.Driver;
import com.google.gson.annotations.SerializedName;

public class UpdateResponse{

	@SerializedName("data")
	private Driver data;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public void setData(Driver data){
		this.data = data;
	}

	public Driver getData(){
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

	public class Data{

		@SerializedName("trip_id")
		private Object tripId;

		@SerializedName("school_id")
		private int schoolId;

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

		@SerializedName("confirmed")
		private int confirmed;

		@SerializedName("email")
		private String email;

		public void setTripId(Object tripId){
			this.tripId = tripId;
		}

		public Object getTripId(){
			return tripId;
		}

		public void setSchoolId(int schoolId){
			this.schoolId = schoolId;
		}

		public int getSchoolId(){
			return schoolId;
		}

		public void setUpdatedAt(String updatedAt){
			this.updatedAt = updatedAt;
		}

		public String getUpdatedAt(){
			return updatedAt;
		}

		public void setImagePath(String imagePath){
			this.imagePath = imagePath;
		}

		public String getImagePath(){
			return imagePath;
		}

		public void setMobileNumber(String mobileNumber){
			this.mobileNumber = mobileNumber;
		}

		public String getMobileNumber(){
			return mobileNumber;
		}

		public void setName(String name){
			this.name = name;
		}

		public String getName(){
			return name;
		}

		public void setCreatedAt(String createdAt){
			this.createdAt = createdAt;
		}

		public String getCreatedAt(){
			return createdAt;
		}

		public void setLicenseNumber(String licenseNumber){
			this.licenseNumber = licenseNumber;
		}

		public String getLicenseNumber(){
			return licenseNumber;
		}

		public void setId(int id){
			this.id = id;
		}

		public int getId(){
			return id;
		}

		public void setConfirmed(int confirmed){
			this.confirmed = confirmed;
		}

		public int getConfirmed(){
			return confirmed;
		}

		public void setEmail(String email){
			this.email = email;
		}

		public String getEmail(){
			return email;
		}
	}
}