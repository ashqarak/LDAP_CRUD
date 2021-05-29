package com.example.bean;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class User {

	private String LanID;
	private String accessType;
	private String employeeId;
	private String name;
	private String mailId;
	private String sn;
	private String branch;
	
	
	public User() {
		
	}


	public User(String lanID, String accessType, String employeeId, String name, String mailId, String sn, String branch) {
		super();
		LanID = lanID;
		this.accessType = accessType;
		this.employeeId = employeeId;
		this.name = name;
		this.mailId = mailId;
		this.sn = sn;
		this.branch = branch;
	}

	@Override
	public String toString() {
		return "User [LanID=" + LanID + ", accessType=" + accessType + ", employeeId=" + employeeId + ", name=" + name
				+ ", mailId=" + mailId + ", sn=" + sn + ", branch=" + branch + "]";
	}


	public String getLanID() {
		return LanID;
	}


	public void setLanID(String lanID) {
		LanID = lanID;
	}


	public String getAccessType() {
		return accessType;
	}


	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}


	public String getEmployeeId() {
		return employeeId;
	}


	public void setEmployeeId(String d) {
		this.employeeId = d;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getMailId() {
		return mailId;
	}


	public void setMailId(String mailId) {
		this.mailId = mailId;
	}


	public String getSn() {
		return sn;
	}


	public void setSn(String sn) {
		this.sn = sn;
	}


	public String getBranch() {
		return branch;
	}


	public void setBranch(String branch) {
		this.branch = branch;
	}
	



}
