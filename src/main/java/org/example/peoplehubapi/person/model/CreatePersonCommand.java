package org.example.peoplehubapi.person.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatePersonCommand {
    private String type; // np. "student", "employee", "retiree"

    private String firstName;
    private String lastName;
    private String pesel;
    private double height;
    private double weight;
    private String email;

}
