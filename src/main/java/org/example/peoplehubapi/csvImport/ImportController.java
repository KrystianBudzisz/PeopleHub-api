package org.example.peoplehubapi.csvImport;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.peoplehubapi.csvImport.model.ImportCommand;
import org.example.peoplehubapi.csvImport.model.ImportStatusDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/imports")
public class ImportController {

    private final ImportService csvImportService;

    @PostMapping("/persons")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasAnyRole('ADMIN', 'IMPORTER')")
    public ImportStatusDTO importPersons(@Valid ImportCommand command) {
        return csvImportService.importCsv(command);
    }


    @GetMapping("/status/{importId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'IMPORTER')")
    public ImportStatusDTO getImportStatus(@PathVariable String importId) {
        return csvImportService.getImportStatus(importId);
    }


}


