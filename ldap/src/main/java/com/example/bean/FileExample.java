package com.example.bean;

import java.io.File;
import java.util.ArrayList;

import org.springframework.stereotype.Component;

@Component
public class FileExample {
	
	@SuppressWarnings("unused")
	public ArrayList<String> getFileListing() {
        File dir = new File("C:/Users/akhan324/Downloads/Softwares/apache-tomcat-9.0.41/apache-tomcat-9.0.41/conf/");
        File[] files = dir.listFiles();

        ArrayList<String> filPaths = new ArrayList<String>();
        for (File file : files) {
            filPaths.add(file.getName());
        }
        return filPaths;
    }
}
