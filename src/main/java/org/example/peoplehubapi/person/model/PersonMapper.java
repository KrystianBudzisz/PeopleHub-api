package org.example.peoplehubapi.person.model;

import org.springframework.stereotype.Component;

@Component
public class PersonMapper {

//    public Person fromCreateCommand(CreatePersonCommand command) {
//        return Person.builder()
//                .firstName(command.getFirstName())
//                .lastName(command.getLastName())
//                .pesel(command.getPesel())
//                .height(command.getHeight())
//                .weight(command.getWeight())
//                .email(command.getEmail())
//                .build();
//    }

    public PersonDto toDto(Person person) {
        return PersonDto.builder()
                .id(person.getId())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .pesel(person.getPesel())
                .height(person.getHeight())
                .weight(person.getWeight())
                .email(person.getEmail())
                .build();
    }

    public void updatePerson(UpdatePersonCommand command, Person person) {
        person.setFirstName(command.getFirstName());
        person.setLastName(command.getLastName());
        person.setHeight(command.getHeight());
        person.setWeight(command.getWeight());
        person.setEmail(command.getEmail());
        // PESEL i ID nie są aktualizowane, więc ich nie dotykamy
    }
}
