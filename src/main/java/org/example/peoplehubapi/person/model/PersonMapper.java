package org.example.peoplehubapi.person.model;

import org.hibernate.MappingException;

import java.lang.reflect.Field;

public class PersonMapper {

    public static PersonDTO toDTO(Person person) {
        if (person == null) {
            return null;
        }
        PersonDTO dto = new PersonDTO();
        dto.setFirstName(person.getFirstName());
        dto.setLastName(person.getLastName());
        dto.setPesel(person.getPesel());
        dto.setHeight(person.getHeight());
        dto.setWeight(person.getWeight());
        dto.setEmail(person.getEmail());

        Field[] fields = person.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(person);
                dto.addAdditionalAttribute(field.getName(), value);
            } catch (IllegalAccessException e) {
                throw new MappingException("Error accessing field value during DTO mapping", e);
            }
        }

        return dto;
    }

}



