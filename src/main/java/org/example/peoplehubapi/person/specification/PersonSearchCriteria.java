package org.example.peoplehubapi.person.specification;

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
public class PersonSearchCriteria {
    private String type;
    private String name;
    private String lastName;
    private Integer ageFrom;
    private Integer ageTo;
    private String pesel;
    private String gender;
    private Double heightFrom;
    private Double heightTo;
    private Double weightFrom;
    private Double weightTo;
    private String email;
    private BigDecimal salaryFrom;
    private BigDecimal salaryTo;
    private String universityName;
    private Integer numberOfProfessionsFrom;
    private Integer numberOfProfessionsTo;
    private LocalDate employmentDateFrom;
    private LocalDate employmentDateTo;
    private String position;
}