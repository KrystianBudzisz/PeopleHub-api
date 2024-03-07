package org.example.peoplehubapi.csvImport;

import org.example.peoplehubapi.csvImport.model.ImportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImportRepository extends JpaRepository<ImportStatus, Long> {

}
