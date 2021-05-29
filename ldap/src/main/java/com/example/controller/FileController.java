package com.example.controller;

import com.example.bean.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.annotation.Resource;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@ComponentScan("com.example")
@RequestMapping("/")
@Scope("session")
public class FileController {
	
	private static String UPLOADED_FOLDER = "C://OpenLDAP//";
	
	String branch = "";
	@Autowired
	LdapProcessorImpl ldapProcessor;

    @GetMapping("/uploadExcel")
    public String index() {
        return "upload";
    }
    @PostMapping("/upload") 
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes, HttpSession session) {
    	
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/uploadStatusFail";
        }

        try {

            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            session.setAttribute("fileName", file.getOriginalFilename());
            Files.write(path, bytes);
            

            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded '" + file.getOriginalFilename() + "'");
            

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/uploadStatusSuccess";
    }
    
    @GetMapping("/uploadStatusSuccess")
    public String uploadStatusSuccess(Model model, ExcelOptions excelOptions, User user) {
    	model.addAttribute(excelOptions);
    	model.addAttribute(user);
    	ldapProcessor.newConnection();
		List<String> branchNames = ldapProcessor.getBranches();	
		model.addAttribute("branchTypeList", branchNames);
        return "uploadStatusSuccess";
    }
    @GetMapping("/uploadStatusFail")
    public String uploadStatusFail() {
        return "uploadStatusFail";
    }
    
    @PostMapping("/processBranchOption")
    public String selectBranch(@ModelAttribute("user") User user, Model model, 
    							ExcelOptions excelOptions, HttpSession session) {
    	boolean value = false;
    	if(!user.getBranch().isEmpty()) {
    		value = true;
    	}
    	session.setAttribute("branch", user.getBranch());

    	model.addAttribute(excelOptions);
    	if(value) {
    	return "executeExcel";
    	}
    	else {
    		return "index";
    	}
    	
    }
    @PostMapping("/executeFromExcel")
    public String executeFromExcel(@ModelAttribute("excelOptions") ExcelOptions excelOptions, User user,
    								HttpSession session, HttpServletRequest request) throws Exception {
    	boolean value = false;
    	session=request.getSession(false);
    	String branch = (String) session.getAttribute("branch");
    	String fileName = (String) session.getAttribute("fileName");
    	user.setBranch(branch);
    	if(excelOptions.getYes().equals("Yes")) {
    		ldapProcessor.newConnection();
    		value = ldapProcessor.executeFromExcel(branch, fileName);
    	}
    	if(value)
    		return "FileExecuted";
    	else
    		return "FileFail";
    	
    }


}
