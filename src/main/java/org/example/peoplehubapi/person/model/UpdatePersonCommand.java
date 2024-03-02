package org.example.peoplehubapi.person.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdatePersonCommand {
    private Long id; // Dodane do identyfikacji osoby, którą chcemy edytować
    private String firstName;
    private String lastName;
    // pole 'pesel' jest pominięte, zakładając, że PESEL nie podlega edycji
    private double height;
    private double weight;
    private String email;
}
