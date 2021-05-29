package com.example.bean;

public class GlobalSession {
	
	private User user;
	private ExcelOptions excelOptions;
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public ExcelOptions getExcelOptions() {
		return excelOptions;
	}
	public void setExcelOptions(ExcelOptions excelOptions) {
		this.excelOptions = excelOptions;
	}
	@Override
	public String toString() {
		return "GlobalSession [user=" + user + ", excelOptions=" + excelOptions + "]";
	}
	
	

}
