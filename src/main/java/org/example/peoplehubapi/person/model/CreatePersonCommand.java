package org.example.peoplehubapi.person.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePersonCommand {
    @NotNull(message = "Type cannot be null")
    private String type;

    @NotNull(message = "Parameters cannot be null")
    private Map<@NotEmpty(message = "Parameter name cannot be empty") String, @NotEmpty(message = "Parameter value cannot be empty") String> params;
}
