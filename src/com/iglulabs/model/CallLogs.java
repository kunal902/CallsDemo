package com.iglulabs.model;

import java.util.Date;

public class CallLogs {
	private String phoneNumber;
	private String type;
	private Date date;
	private String durationInSec;
	
	public CallLogs(String phoneNumber, String type, Date date, String durationInSec){
		this.phoneNumber = phoneNumber;
		this.type = type;
		this.date = date;
		this.durationInSec = durationInSec;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDurationInSec() {
		return durationInSec;
	}

	public void setDurationInSec(String durationInSec) {
		this.durationInSec = durationInSec;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
