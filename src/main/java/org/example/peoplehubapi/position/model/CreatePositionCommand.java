package org.example.peoplehubapi.position.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePositionCommand {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Salary is required")
    @DecimalMin(value = "0", message = "Salary must be positive")
    private BigDecimal salary;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;


}
