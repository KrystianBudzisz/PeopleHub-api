package org.example.peoplehubapi.csvImport;

import lombok.AllArgsConstructor;
import org.example.peoplehubapi.csvImport.model.ImportStatusDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@RestController
@RequestMapping("/api/imports")
public class ImportController {

    private final ImportService importService;

    @PostMapping("/persons")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasAnyRole('ADMIN', 'IMPORTER')")
    public ImportStatusDTO importPersons(@RequestParam("file") MultipartFile file) {
        return importService.importCsv(file);
    }

    @GetMapping("/status/{importId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'IMPORTER')")
    public ImportStatusDTO getImportStatus(@PathVariable String importId) {
        return importService.getImportStatus(importId);
    }

}


