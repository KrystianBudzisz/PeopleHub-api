package org.example.peoplehubapi.strategy.creation;

import org.example.peoplehubapi.exception.InvalidPersonCreationException;
import org.example.peoplehubapi.exception.InvalidPersonUpdateException;
import org.example.peoplehubapi.person.model.CreatePersonCommand;
import org.example.peoplehubapi.person.model.Person;
import org.example.peoplehubapi.person.model.UpdatePersonCommand;
import org.example.peoplehubapi.strategy.model.Employee;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Component("EMPLOYEE")
public class EmployeeCreationStrategy implements PersonCreationStrategy {

    @Override
    public Person create(CreatePersonCommand command) {
        try {
            Map<String, String> params = command.getParams();
            return Employee.builder()
                    .firstName(params.get("firstName"))
                    .lastName(params.get("lastName"))
                    .pesel(params.get("pesel"))
                    .height(Double.parseDouble(params.get("height")))
                    .weight(Double.parseDouble(params.get("weight")))
                    .email(params.get("email"))
                    .employmentDate(LocalDate.parse(params.get("employmentDate")))
                    .position(params.get("position"))
                    .salary(new BigDecimal(params.get("salary")))
                    .numberOfProfessions(Integer.parseInt(params.getOrDefault("numberOfProfessions", "1")))
                    .build();
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new InvalidPersonCreationException("Error parsing input parameters", e);
        }
    }

    @Override
    public Person update(Person person, UpdatePersonCommand command) {
        try {
            Employee employee = (Employee) person;
            Map<String, String> params = command.getParams();

            employee.setFirstName(params.getOrDefault("firstName", employee.getFirstName()));
            employee.setLastName(params.getOrDefault("lastName", employee.getLastName()));
            employee.setPesel(params.getOrDefault("pesel", employee.getPesel()));
            employee.setHeight(Double.parseDouble(params.getOrDefault("height", String.valueOf(employee.getHeight()))));
            employee.setWeight(Double.parseDouble(params.getOrDefault("weight", String.valueOf(employee.getWeight()))));
            employee.setEmail(params.getOrDefault("email", employee.getEmail()));
            employee.setEmploymentDate(LocalDate.parse(params.getOrDefault("employmentDate", employee.getEmploymentDate().toString())));
            employee.setPosition(params.getOrDefault("position", employee.getPosition()));
            employee.setSalary(new BigDecimal(params.getOrDefault("salary", employee.getSalary().toString())));
            employee.setNumberOfProfessions(Integer.parseInt(params.getOrDefault("numberOfProfessions", String.valueOf(employee.getNumberOfProfessions()))));

            return employee;
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new InvalidPersonUpdateException("Error parsing input parameters", e);
        }
    }


    @Override
    public String getType() {
        return "EMPLOYEE";
    }
}
