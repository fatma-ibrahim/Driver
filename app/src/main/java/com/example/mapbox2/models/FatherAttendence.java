package com.example.mapbox2.models;

import java.io.Serializable;
import java.util.List;

import com.example.mapbox2.models.ChildrenItem;
import com.google.gson.annotations.SerializedName;

public class FatherAttendence implements Serializable {

	@SerializedName("lng")
	private String lng;

	@SerializedName("children")
	private List<ChildrenItem> children;

	@SerializedName("lit")
	private String lit;

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;

	@SerializedName("image_path")
	private String imagePath;

	public String getImagePath() {
		return imagePath;
	}

	public void setLng(String lng){
		this.lng = lng;
	}

	public String getLng(){
		return lng;
	}

	public void setChildren(List<ChildrenItem> children){
		this.children = children;
	}

	public List<ChildrenItem> getChildren(){
		return children;
	}

	public void setLit(String lit){
		this.lit = lit;
	}

	public String getLit(){
		return lit;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}
}