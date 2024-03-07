package org.example.peoplehubapi.csvImport.model;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ImportStatusDTO {
    private Long id;
    private String status;
    private LocalDateTime creationTimestamp;
    private LocalDateTime startTimestamp;
    private LocalDateTime completionTimestamp;
    private long rowsProcessed;

}