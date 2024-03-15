package org.example.peoplehubapi.strategy.creation;

import org.example.peoplehubapi.person.model.CreatePersonCommand;
import org.example.peoplehubapi.person.model.Person;
import org.example.peoplehubapi.person.model.UpdatePersonCommand;

public interface PersonCreationStrategy {
    Person create(CreatePersonCommand command);

    String getType();

    Person update(Person person, UpdatePersonCommand command);

    Person createFromCsvRecord(String[] record);


}
