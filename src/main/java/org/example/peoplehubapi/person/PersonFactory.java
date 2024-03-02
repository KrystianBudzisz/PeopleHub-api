package org.example.peoplehubapi.person;

import org.example.peoplehubapi.model.Employee;
import org.example.peoplehubapi.model.Retiree;
import org.example.peoplehubapi.model.Student;
import org.example.peoplehubapi.person.model.CreatePersonCommand;
import org.example.peoplehubapi.person.model.Person;

public class PersonFactory {
    public static Person createPerson(CreatePersonCommand command) {
        switch (command.getType().toLowerCase()) {
            case "student":
                return new Student(/* inicjalizacja pól specyficznych dla Studenta z command */);
            case "employee":
                return new Employee(/* inicjalizacja pól specyficznych dla Employee z command */);
            case "retiree":
                return new Retiree(/* inicjalizacja pól specyficznych dla Retiree z command */);
            default:
                throw new IllegalArgumentException("Nieznany typ osoby: " + command.getType());
        }
    }
}
