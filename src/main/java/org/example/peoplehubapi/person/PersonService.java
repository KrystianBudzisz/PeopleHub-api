package org.example.peoplehubapi.person;

import lombok.AllArgsConstructor;
import org.example.peoplehubapi.model.Employee;
import org.example.peoplehubapi.model.Retiree;
import org.example.peoplehubapi.model.Student;
import org.example.peoplehubapi.person.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class PersonService {

    private final PersonRepository personRepository;
    private final PersonMapper personMapper;

//    public PersonDto addPerson(CreatePersonCommand command) {
//        Person person = createPersonBasedOnType(command);
//        personRepository.save(person);
//        return personMapper.toDto(person);
//    }

//    private Person createPersonBasedOnType(CreatePersonCommand command) {
//        switch (command.getType().toLowerCase()) {
//            case "student":
//                return createStudent(command);
//            case "employee":
//                return createEmployee(command);
//            case "retiree":
//                return createRetiree(command);
//            default:
//                throw new IllegalArgumentException("Nieznany typ osoby: " + command.getType());
//        }
//    }
//    private Student createStudent(CreatePersonCommand command) {
//        return new Student(command.getFirstName(),
//                command.getLastName(),
//                command.getPesel(),
//                command.getHeight(),
//                command.getWeight(),
//                command.getEmail(), /* Dane specyficzne dla Studenta */);
//    }
//
//    private Employee createEmployee(CreatePersonCommand command) {
//        return new Employee(command.getFirstName(),
//                command.getLastName(),
//                command.getPesel(),
//                command.getHeight(),
//                command.getWeight(),
//                command.getEmail(), /* Dane specyficzne dla Pracownika */);
//    }
//
//    private Retiree createRetiree(CreatePersonCommand command) {
//        return new Retiree(command.getFirstName(),
//                command.getLastName(),
//                command.getPesel(),
//                command.getHeight(),
//                command.getWeight(),
//                command.getEmail(), /* Dane specyficzne dla Emeryta */);
//    }

    @Transactional(readOnly = true)
    public Page<PersonDto> searchPersons(SearchCriteria criteria, Pageable pageable) {
        Specification<Person> spec = PersonSpecifications.bySearchCriteria(criteria);
        Page<Person> personPage = personRepository.findAll(spec, pageable);
        return personPage.map(personMapper::toDto);
    }


    public PersonDto editPerson(Long id, UpdatePersonCommand updatePersonCommand) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person not found"));
        personMapper.updatePerson(updatePersonCommand, person);
        person = personRepository.save(person);
        return personMapper.toDto(person);
    }

    // Metody do zarządzania stanowiskami, importu CSV itp.
}