package org.example.peoplehubapi.position.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PositionDTO {
    private Long id;
    private String name;
    private BigDecimal salary;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long employeeId;

}
