package org.example.peoplehubapi.person.model;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PersonDTO {
    private String firstName;
    private String lastName;
    private String pesel;
    private double height;
    private double weight;
    private String email;
    private Map<String, Object> additionalAttributes = new HashMap<>();

    public void addAdditionalAttribute(String key, Object value) {
        this.additionalAttributes.put(key, value);
    }


}
