package org.example.peoplehubapi.person;

import org.example.peoplehubapi.exception.*;
import org.example.peoplehubapi.person.model.*;
import org.example.peoplehubapi.person.specification.PersonSearchCriteria;
import org.example.peoplehubapi.person.specification.PersonSpecification;
import org.example.peoplehubapi.position.PositionRepository;
import org.example.peoplehubapi.position.model.CreatePositionCommand;
import org.example.peoplehubapi.position.model.Position;
import org.example.peoplehubapi.position.model.PositionDTO;
import org.example.peoplehubapi.position.model.PositionMapper;
import org.example.peoplehubapi.strategy.creation.PersonCreationStrategy;
import org.example.peoplehubapi.strategy.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PersonService {

    private final Map<String, PersonCreationStrategy> strategyMap;
    private final PersonRepository personRepository;
    private final PositionRepository positionRepository;

    public PersonService(List<PersonCreationStrategy> strategies, PersonRepository personRepository, PositionRepository positionRepository) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(PersonCreationStrategy::getType, Function.identity()));
        this.personRepository = personRepository;
        this.positionRepository = positionRepository;
    }

    @Transactional
    public PersonDTO create(CreatePersonCommand command) {
        PersonCreationStrategy strategy = strategyMap.get(command.getType().toUpperCase());
        if (strategy == null) {
            throw new UnsupportedPersonTypeException("Unsupported person type: " + command.getType());
        }
        Person person = strategy.create(command);
        person = personRepository.save(person);
        return PersonMapper.toDTO(person);
    }

    @Transactional(readOnly = true)
    public Page<PersonDTO> searchPersons(PersonSearchCriteria criteria, Pageable pageable) {
        Specification<Person> specification = new PersonSpecification(criteria);
        Page<Person> personPage = personRepository.findAll(specification, pageable);
        return personPage.map(PersonMapper::toDTO);
    }


    @Transactional
    public PersonDTO update(Long id, UpdatePersonCommand command) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException("Person with id " + id + " not found."));


        String strategyKey = person.getClass().getSimpleName().toUpperCase();

        PersonCreationStrategy strategy = strategyMap.get(strategyKey);
        if (strategy == null) {
            throw new UnsupportedPersonTypeException("Unsupported person type for update: " + strategyKey);
        }

        person = strategy.update(person, command);
        try {
            person = personRepository.save(person);
            return PersonMapper.toDTO(person);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new CustomOptimisticLockException("The person with id " + id + " was modified by someone else.");
        }
    }

    public PositionDTO addPosition(Long employeeId, CreatePositionCommand command) {
        Employee employee = personRepository.findById(employeeId)
                .filter(Employee.class::isInstance)
                .map(Employee.class::cast)
                .orElseThrow(() -> new EmpolyeeNotFoundException("Employee not found"));

        List<Position> overlappingPositions = positionRepository.findByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                employeeId, command.getEndDate(), command.getStartDate());

        if (!overlappingPositions.isEmpty()) {
            throw new InvalidPersonCreationException("The position dates overlap with existing position dates.");
        }

        Position newPosition = PositionMapper.fromCreateCommand(command);
        newPosition.setEmployee(employee);
        positionRepository.save(newPosition);

        return PositionMapper.toDTO(newPosition);
    }


}