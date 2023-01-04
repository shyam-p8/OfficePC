package com.shyam.OfficePC.file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


@Service
public class FileSaveUtil {
	
	     
	    public void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException 
	    {
	        Path uploadPath = Paths.get(uploadDir);
	         
	        if (!Files.exists(uploadPath)) 
	        {
	            Files.createDirectories(uploadPath);
	        }
	         
	        try (InputStream inputStream = multipartFile.getInputStream())
	        {
	            Path filePath = uploadPath.resolve(fileName);
	         Long l= Files.copy(inputStream, filePath,StandardCopyOption.REPLACE_EXISTING );
	            
	         System.out.println("file upload path ="+ filePath.toString());
	            System.out.println("written byte = %l" + l);
	            
	        } catch (IOException ioe) 
	        {        
	            throw new IOException("Could not save image file: " + fileName, ioe);
	        }
			    
	    }
	    
	    
	    public String copyFrom(String path, String fileName) {
	    	
	    	
	        Path copy_from_1 = Paths.get(path, fileName);

	        Path copy_to_1 = Paths.get("D:/shyam", copy_from_1.getFileName().toString());
	        
	        try {
	          Files.copy(copy_from_1, copy_to_1, REPLACE_EXISTING, COPY_ATTRIBUTES, NOFOLLOW_LINKS);
	          
	          
	        } catch (IOException e) {
	          System.err.println(e);
	        }
			return "file copy done";
	      }
	    
	    
	    
	    
	    
                        	}

