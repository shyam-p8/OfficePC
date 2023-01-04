package com.shyam.OfficePC.file;

import java.util.Arrays;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
public class Document {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column
	    private String docName;

	    @Column
	    @Lob
	    private byte[] file;
	    
	    

		public Document() {
			super();
			// TODO Auto-generated constructor stub
		}

		public Document(Long id, String docName, byte[] file) {
			super();
			this.id = id;
			this.docName = docName;
			this.file = file;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getDocName() {
			return docName;
		}

		public void setDocName(String docName) {
			this.docName = docName;
		}

		public byte[] getFile() {
			return file;
		}

		public void setFile(byte[] file) {
			this.file = file;
		}

		@Override
		public String toString() {
			return "Document [id=" + id + ", docName=" + docName + ", file=" + Arrays.toString(file) + "]";
		}

	    
	    
	    
}
