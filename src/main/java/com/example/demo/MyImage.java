package com.example.demo;

import java.util.Date;

public class MyImage {
	private Date date;
	private String filename;
	
	// Constructor
	public MyImage(Date date, String filename) {
		super();
		this.date = date;
		this.filename = filename;
	}
	
	// Getters and Setters
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getfilename() {
		return filename;
	}

	public void setfilename(String filename) {
		this.filename = filename;
	}
	
	
	
	
}
