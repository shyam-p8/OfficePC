package com.shyam.OfficePC.file;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletResponse;



@RestController
public class FileUpload {
	
	
	@Autowired     
	private FileSaveUtil fileSaveUtil;
	
	@Autowired
	private DocumentDao documentDao;
	
	
	
	    @PostMapping("/save")
	    public String saveFile(@RequestParam("image") MultipartFile image) throws IOException 
	    {	
	    	
	        String fileName = StringUtils.cleanPath(image.getOriginalFilename());
	        	         
	        String uploadDir = "D:/shyam/" ;	 
	        
	        fileSaveUtil.saveFile(uploadDir, fileName, image);
	         
	        return "ok";
	    }
	    
	    @GetMapping("/copy")
	    public String copyFromH(@RequestParam("fileName") String fileName, @RequestParam("path") String path)
	    {
	        
	        String msg = (String) this.fileSaveUtil.copyFrom(path, fileName);
	
	        return msg;
	    }
	    
	    @PostMapping("/upload")
	    public ResponseEntity uploadToLocalFileSystem(@RequestParam("file") MultipartFile file) {
	    	if(file.isEmpty())
	    	{
	    		return ResponseEntity.ok("file not found, plz attache the file");
	    	}
	    	
	    	String fileName = StringUtils.cleanPath(file.getOriginalFilename());
	    	String fileBasePath = "D:/shyam/img/";
			Path path = Paths.get(fileBasePath + fileName);
	    	try {
	    		Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
	    	
	    	return ResponseEntity.ok("file uploaded successfully");
	    }
	    
	    //handler to upload multiple file in database.
	    
	    @PostMapping("/multi-upload")
	    public ResponseEntity multiUpload(@RequestParam("files") MultipartFile[] files) {
	    	//List<Object> fileDownloadUrls = new ArrayList<>();
	    	
	    	int l = files.length;
	    	Arrays.asList(files).stream().forEach(file -> uploadToLocalFileSystem(file));
	    	return ResponseEntity.ok("total upload files = "+l);
	    }
	  // link for upload and download file code : https://www.devglan.com/spring-boot/spring-boot-file-upload-download 
	  
	    
	    //handler to upload file in database.
	    @PostMapping("/upload/db")
	    public ResponseEntity uploadToDB(@RequestParam("file") MultipartFile file) {
	    	Document doc = new Document();
	    	String fileName = StringUtils.cleanPath(file.getOriginalFilename());
	    	doc.setDocName(fileName);
	    	try {
	    		doc.setFile(file.getBytes());
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
	    	
	    	documentDao.save(doc);
	    	String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
	    			.path("/files/download/")
	    			.path(fileName).path("/db")
	    			.toUriString();
	    	return ResponseEntity.ok(fileDownloadUri);
	    } 
	    
	    //Spring Boot File Download from Local File System 
	    	    
	    @GetMapping("/download/{fileName:.+}")
	    public ResponseEntity downloadFileFromLocal(@PathVariable String fileName) {
	    	String fileBasePath = "D:/shyam/img/";
	    	Path path = Paths.get(fileBasePath + fileName);
	    	Resource resource = null;
	    	try {
	    		resource = new UrlResource(path.toUri());
	    	} catch (MalformedURLException e) {
	    		e.printStackTrace();
	    	}
	    	
	    	 String contentType = "application/octet-stream";
	    	 
	    	return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
	    			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
	    			.body(resource);
	    }
	    
	    
	    // Implement File Download API
	    // filecode = file first name ex. shyam.jpg --> filecode = shyam
	    
	    @GetMapping("/downloadFile/{fileCode}")
	    public ResponseEntity<?> downloadFile(@PathVariable("fileCode") String fileCode) {
	        FileDownloadUtil downloadUtil = new FileDownloadUtil();
	         
	        Resource resource = null;
	        try {
	            resource = downloadUtil.getFileAsResource(fileCode);
	        } catch (IOException e) {
	            return ResponseEntity.internalServerError().build();
	        }
	         
	        if (resource == null) {
	            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
	        }
	         
	        String contentType = "application/octet-stream";
	        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";
	         
	        return ResponseEntity.ok()
	                .contentType(MediaType.parseMediaType(contentType))
	                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
	                .body(resource);       
	    }
	    
	    
	    //Spring Boot File Download from Database
	    
	    @GetMapping("/downloadDB/{id}")
	    public ResponseEntity downloadFromDB(@PathVariable Long id) {
	    	
	    	Optional<Document> document = documentDao.findById(id);
	    	String fileName = document.get().getDocName();
	    	    	   
	    return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream")).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(document.get().getFile());
	    }
	    
	    @GetMapping("/downloadDB/file/{fileName}")
	    public ResponseEntity downloadFromDB2(@PathVariable String fileName) {
	    	
	    	Document doc = documentDao.findByDocName(fileName);
	    	
	    	System.out.println("document detail retrived from DB = "+doc);
	     	
	    	return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream")).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(doc.getFile());
	    }
	   
	   //Zip Multiple Files and Download
	    
	    @GetMapping(value = "/zip-download", produces="application/zip")
	    public void zipDownload(@RequestParam("name") List<String> name, HttpServletResponse response) throws IOException {
	    	ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
	    	String fileBasePath = "D:/shyam/img/";
	    	for (String fileName : name) {
	    		FileSystemResource resource = new FileSystemResource(fileBasePath + fileName);
	    		ZipEntry zipEntry = new ZipEntry(resource.getFilename());
	    		zipEntry.setSize(resource.contentLength());
	    		zipOut.putNextEntry(zipEntry);
	    		StreamUtils.copy(resource.getInputStream(), zipOut);
	    		zipOut.closeEntry();
	    	}
	    	zipOut.finish();
	    	zipOut.close();
	    	response.setStatus(HttpServletResponse.SC_OK);
	    	 String zipFileName ="abc";
			response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipFileName  + "\"");
	    }
	    
	    
	    
}
