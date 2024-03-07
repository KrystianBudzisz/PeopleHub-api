package org.example.peoplehubapi.person;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.peoplehubapi.person.model.CreatePersonCommand;
import org.example.peoplehubapi.person.model.PersonDTO;
import org.example.peoplehubapi.person.model.UpdatePersonCommand;
import org.example.peoplehubapi.person.specification.PersonSearchCriteria;
import org.example.peoplehubapi.position.model.CreatePositionCommand;
import org.example.peoplehubapi.position.model.PositionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService personService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public PersonDTO createPerson(@RequestBody @Valid CreatePersonCommand command) {
        return personService.create(command);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public Page<PersonDTO> searchPersons(PersonSearchCriteria criteria,
                                         @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return personService.searchPersons(criteria, pageable);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public PersonDTO updatePerson(@PathVariable Long id, @RequestBody UpdatePersonCommand command) {
        return personService.update(id, command);
    }

    @PostMapping("/{employeeId}/positions")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public PositionDTO addPositionToEmployee(@PathVariable Long employeeId, @RequestBody @Valid CreatePositionCommand command) {
        return personService.addPosition(employeeId, command);
    }

}

