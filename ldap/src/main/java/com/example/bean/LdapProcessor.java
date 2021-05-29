package com.example.bean;


import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import javax.naming.NamingException;

import org.springframework.stereotype.Component;

import com.example.bean.*;

@Component
public interface LdapProcessor {
	
	public void newConnection();
	public boolean addUserInGroup(User user);
	public void sendMail();
	public List<String>  getGroups(String Branch);
	public List<String>  getMembers(String Branch);
	public boolean removeUser(User user) throws NamingException;
	public List<String> getBranches();
	public boolean addBranch(User user);
	public boolean executeFromExcel(String branch, String fileName) throws Exception;
	

}
