package com.example.controller;

import com.example.bean.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.annotation.Resource;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@ComponentScan("com.example.bean")
@RequestMapping("/")
public class MyController {
	
	@Autowired 
	private LdapProcessorImpl ldapProcessor;
	@Autowired 
	private FileExample fileexample;
	
	@RequestMapping(value={"/", "index"})
	public String index() {
		return "index";
	}
	
	@GetMapping("/addUser")
    public String showAddUserForm(User user, Model model) {
		model.addAttribute("user", user);
		ldapProcessor.newConnection();
		List<String> branchNames = ldapProcessor.getBranches();	
		model.addAttribute("branchTypeList", branchNames);
		
        return "addUser";
    }
	
	@RequestMapping(value = "/loadGroupNames", headers = "Accept=*/*", method = RequestMethod.GET)
	public @ResponseBody
		List<String> loadGroups(@RequestParam(value = "branchId", required = true) String branch) throws IllegalStateException {
			ldapProcessor.newConnection();
	        
	        return ldapProcessor.getGroups(branch);
    }

    @PostMapping("/addUser")
    public String submitAddUserForm(@ModelAttribute("user") User user, Model model) {
    	boolean userAdded = false;
    	model.addAttribute(user.toString());
    	if(!user.equals(null)) {
    		ldapProcessor.newConnection();
    		userAdded = ldapProcessor.addUserInGroup(user);
    	}
		
		if(userAdded)
			return "showMessage";
		else
			return "showFailure";
    }
    
    @GetMapping("/removeUser")
    public String showRemoveUserForm(User user, Model model) {
    	model.addAttribute("user", user);
		ldapProcessor.newConnection();
		List<String> branchNames = ldapProcessor.getBranches();
		model.addAttribute("branchTypeList", branchNames);
		return "removeUser";
    }
    
    @PostMapping("/removeUser")
    public String removeUser(@ModelAttribute("user")User user, Model model) throws NamingException {
    	boolean userRemoved= false;
    	model.addAttribute(user.toString());
    	if(!user.equals(null)) {
    		ldapProcessor.newConnection();
    		userRemoved = ldapProcessor.removeUser(user);
    	}
		if(userRemoved)
			return "removeUserSuccess";
		else
			return "removeUserFail";
    }
    @RequestMapping("/addBranch")
    public String addBranchForm(User user, Model model) {
    	model.addAttribute(user);
    	return "addBranch"; 	
    }
    @PostMapping("/addBranch")
    public String addBranch(@ModelAttribute("user")User user, Model model) {
    	boolean branchAdded = false;
    	model.addAttribute(user.toString());
    	if(!user.equals(null)) {
    		ldapProcessor.newConnection();
    		branchAdded = ldapProcessor.addBranch(user);
    	}
		if(branchAdded)
			return "branchSuccess";
		else
			return "branchFail";
    }
    
/*   
    @RequestMapping(value = "/loadMemberNames", headers = "Accept=*", method = RequestMethod.GET)
	public @ResponseBody
		List<String> loadMembers(@RequestParam(value = "branchId", required = true) String branch) throws IllegalStateException {
			ldapProcessor.newConnection();
	        
	        return ldapProcessor.getMembers(branch);
    }
  
  */
  
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public String downloadLogs(Model model) {

        model.addAttribute("files", fileexample.getFileListing());

        return "downloadLogs";
    }

    
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity downloadFileFromLocal(@PathVariable String fileName, HttpSession session) {
    	String fileBasePath = "C:/Users/akhan324/Downloads/Softwares/apache-tomcat-9.0.41/apache-tomcat-9.0.41/conf/";
		Path path = Paths.get(fileBasePath + fileName);
    	UrlResource resource = null;
    	try {
    		resource = new UrlResource(path.toUri());
    	} catch (MalformedURLException e) {
    		e.printStackTrace();
    	}
    	Object contentType = "application/octet-stream" ;
		return ResponseEntity.ok()
    			.contentType(MediaType.parseMediaType((String) contentType))
    			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
    			.body(resource);
		
    }
   
    	
    	
}
