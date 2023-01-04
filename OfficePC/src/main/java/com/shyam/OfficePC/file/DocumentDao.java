package com.shyam.OfficePC.file;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentDao extends JpaRepository<Document, Long> {

	Document findByDocName(String fileName);

	//Optional<Document> findById(Long id);

	Optional<Document> findById(Long id);

}
