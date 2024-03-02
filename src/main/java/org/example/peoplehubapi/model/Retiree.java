package org.example.peoplehubapi.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.peoplehubapi.person.model.Person;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Retiree extends Person {
    private Double pensionAmount;
    private Integer yearsWorked;

    public Retiree(Long id, String firstName, String lastName, String pesel, Double height, Double weight, String email, Double pensionAmount, Integer yearsWorked) {
        super(id, firstName, lastName, pesel, height, weight, email);
        this.pensionAmount = pensionAmount;
        this.yearsWorked = yearsWorked;
    }
}