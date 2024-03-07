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
@Entity
@DiscriminatorValue("STUDENT")
public class Student extends Person {
    private String universityName;
    private int yearOfStudy;
    private String fieldOfStudy;
    private BigDecimal scholarship;
}
