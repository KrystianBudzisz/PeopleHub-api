package org.example.peoplehubapi.strategy.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.peoplehubapi.person.model.Person;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@DiscriminatorValue("RETIREE")
@Entity
public class Retiree extends Person {
    private BigDecimal pensionAmount;
    private int yearsWorked;
}