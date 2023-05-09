package com.example.mapbox2.responses;

import com.google.gson.annotations.SerializedName;

public class CountChildsResponse{

	@SerializedName("data")
	private Data data;

	@SerializedName("success")
	private boolean success;

	@SerializedName("message")
	private String message;

	public void setData(Data data){
		this.data = data;
	}

	public Data getData(){
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

		@SerializedName("total_childern")
		private int totalChildern;

		@SerializedName("school_name")
		private String schoolName;

		@SerializedName("childern")
		private int childern;

		public void setTotalChildern(int totalChildern){
			this.totalChildern = totalChildern;
		}

		public int getTotalChildern(){
			return totalChildern;
		}

		public void setSchoolName(String schoolName){
			this.schoolName = schoolName;
		}

		public String getSchoolName(){
			return schoolName;
		}

		public void setChildern(int childern){
			this.childern = childern;
		}

		public int getChildern(){
			return childern;
		}
	}
}