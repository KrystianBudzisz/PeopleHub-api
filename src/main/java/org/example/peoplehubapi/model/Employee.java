package org.example.peoplehubapi.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.peoplehubapi.person.model.Person;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Employee extends Person {
    private LocalDate employmentStartDate;
    private String currentPosition;
    private Double currentSalary;

    public Employee(Long id, String firstName, String lastName, String pesel, Double height, Double weight, String email, LocalDate employmentStartDate, String currentPosition, Double currentSalary) {
        super(id, firstName, lastName, pesel, height, weight, email);
        this.employmentStartDate = employmentStartDate;
        this.currentPosition = currentPosition;
        this.currentSalary = currentSalary;
    }
}
