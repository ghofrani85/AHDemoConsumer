package com.example.demo;

import java.util.Date;

public class MyImage {
	private Date date;
	private String imageString;
	
	// Constructor
	public MyImage(Date date, String imageString) {
		super();
		this.date = date;
		this.imageString = imageString;
	}
	
	// Getters and Setters
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getImageString() {
		return imageString;
	}

	public void setImageString(String imageString) {
		this.imageString = imageString;
	}
	
	
	
	
}
