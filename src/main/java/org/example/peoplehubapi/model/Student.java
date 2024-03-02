package org.example.peoplehubapi.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.peoplehubapi.person.model.Person;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Student extends Person {
    private String universityName;
    private Integer yearOfStudy;
    private String fieldOfStudy;
    private Double scholarshipAmount;


    public Student(Long id, String firstName, String lastName, String pesel, Double height, Double weight, String email, String universityName, Integer yearOfStudy, String fieldOfStudy, Double scholarshipAmount) {
        super(id, firstName, lastName, pesel, height, weight, email);
        this.universityName = universityName;
        this.yearOfStudy = yearOfStudy;
        this.fieldOfStudy = fieldOfStudy;
        this.scholarshipAmount = scholarshipAmount;
    }
}
