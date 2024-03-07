package org.example.peoplehubapi.csvImport.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportCommand {

    @NotNull(message = "File must not be null")
    private MultipartFile file;
}
