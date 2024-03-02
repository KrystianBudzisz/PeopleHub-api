package org.example.peoplehubapi.person;

import lombok.AllArgsConstructor;
import org.example.peoplehubapi.person.model.CreatePersonCommand;
import org.example.peoplehubapi.person.model.Person;
import org.example.peoplehubapi.person.model.UpdatePersonCommand;
import org.example.peoplehubapi.person.model.PersonDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/persons")
public class PersonController {

    private final PersonService personService;

//    @PostMapping("/add")
//    public ResponseEntity<PersonDto> addPerson(@RequestBody CreatePersonCommand createPersonCommand) {
//        PersonDto personDto = personService.addPerson(createPersonCommand);
//        return new ResponseEntity<>(personDto, HttpStatus.CREATED);
//    }
//    @PostMapping
//    public ResponseEntity<PersonDto> addPerson(@RequestBody CreatePersonCommand command) {
//        Person person = PersonFactory.createPerson(command);
//        Person savedPerson = personService.addPerson(person);
//        return new ResponseEntity<>(new PersonDto(savedPerson), HttpStatus.CREATED);
//    }

    @GetMapping("/search")
    public ResponseEntity<Page<PersonDto>> searchPersons(SearchCriteria criteria, Pageable pageable) {
        Page<PersonDto> persons = personService.searchPersons(criteria, pageable);
        return new ResponseEntity<>(persons, HttpStatus.OK);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<PersonDto> editPerson(@PathVariable Long id, @RequestBody UpdatePersonCommand editPersonCommand) {
        PersonDto personDto = personService.editPerson(id, editPersonCommand);
        return ResponseEntity.ok(personDto);
    }
}

