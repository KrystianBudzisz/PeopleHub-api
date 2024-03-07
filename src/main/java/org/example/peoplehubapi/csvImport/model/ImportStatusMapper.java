package org.example.peoplehubapi.csvImport.model;

public class ImportStatusMapper {

    public static ImportStatusDTO toDTO(ImportStatus importStatus) {
        ImportStatusDTO dto = new ImportStatusDTO();
        dto.setId(importStatus.getId());
        dto.setStatus(importStatus.getStatus());
        dto.setCreationTimestamp(importStatus.getCreationTimestamp());
        dto.setStartTimestamp(importStatus.getStartTimestamp());
        dto.setCompletionTimestamp(importStatus.getCompletionTimestamp());
        dto.setRowsProcessed(importStatus.getRowsProcessed());
        return dto;
    }

}

