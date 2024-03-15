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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Component("EMPLOYEE")
public class EmployeeCreationStrategy implements PersonCreationStrategy {
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public Person create(CreatePersonCommand command) {
        Map<String, String> params = command.getParams();
        validate(params);
        try {
            return Employee.builder()
                    .firstName(params.get("firstName"))
                    .lastName(params.get("lastName"))
                    .pesel(params.get("pesel"))
                    .height(Double.parseDouble(params.get("height")))
                    .weight(Double.parseDouble(params.get("weight")))
                    .email(params.get("email"))
                    .employmentDate(LocalDate.parse(params.get("employmentDate"), dateFormatter))
                    .position(params.get("position"))
                    .salary(new BigDecimal(params.get("salary")))
                    .numberOfProfessions(Integer.parseInt(params.getOrDefault("numberOfProfessions", "1")))
                    .build();
        } catch (DateTimeParseException | NumberFormatException e) {
            throw new InvalidPersonCreationException("Unexpected error during person creation", e);
        }
    }

    @Override
    public Person update(Person person, UpdatePersonCommand command) {
        Map<String, String> params = command.getParams();
        validate(params);
        try {
            Employee employee = (Employee) person;
            employee.setFirstName(params.getOrDefault("firstName", employee.getFirstName()));
            employee.setLastName(params.getOrDefault("lastName", employee.getLastName()));
            employee.setPesel(params.getOrDefault("pesel", employee.getPesel()));
            employee.setHeight(Double.parseDouble(params.getOrDefault("height", String.valueOf(employee.getHeight()))));
            employee.setWeight(Double.parseDouble(params.getOrDefault("weight", String.valueOf(employee.getWeight()))));
            employee.setEmail(params.getOrDefault("email", employee.getEmail()));
            employee.setEmploymentDate(LocalDate.parse(params.getOrDefault("employmentDate", employee.getEmploymentDate().toString()), dateFormatter));
            employee.setPosition(params.getOrDefault("position", employee.getPosition()));
            employee.setSalary(new BigDecimal(params.getOrDefault("salary", employee.getSalary().toString())));
            employee.setNumberOfProfessions(Integer.parseInt(params.getOrDefault("numberOfProfessions", String.valueOf(employee.getNumberOfProfessions()))));
            return employee;
        } catch (DateTimeParseException | NumberFormatException e) {
            throw new InvalidPersonUpdateException("Unexpected error during person update", e);
        }
    }

    @Override
    public Employee createFromCsvRecord(String[] record) {
        try {
            Employee employee = new Employee();
            employee.setFirstName(record[1]);
            employee.setLastName(record[2]);
            employee.setPesel(record[3]);
            employee.setHeight(Double.parseDouble(record[4]));
            employee.setWeight(Double.parseDouble(record[5]));
            employee.setEmail(record[6]);
            employee.setEmploymentDate(LocalDate.parse(record[7]));
            employee.setPosition(record[8]);
            employee.setSalary(new BigDecimal(record[9]));
            employee.setNumberOfProfessions(Integer.parseInt(record[10]));
            return employee;
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new InvalidPersonCreationException("Error parsing input parameters", e);
        }
    }

    @Override
    public String getType() {
        return "EMPLOYEE";
    }

    private void validate(Map<String, String> params) {
        requireNonNullOrEmpty(params, "firstName", "First name is required.");
        requireNonNullOrEmpty(params, "lastName", "Last name is required.");
        requireNonNullOrEmpty(params, "pesel", "PESEL is required.");
        requireNonNullOrEmpty(params, "height", "Height is required.");
        requirePositiveDouble(params, "height", "Height must be a positive number.");
        requireNonNullOrEmpty(params, "weight", "Weight is required.");
        requirePositiveDouble(params, "weight", "Weight must be a positive number.");
        requireNonNullOrEmpty(params, "email", "Email is required.");
        requireValidEmail(params.get("email"));
        requireNonNullOrEmpty(params, "employmentDate", "Employment date is required.");
        requireValidDate(params.get("employmentDate"), "Invalid employment date format.");
        requireNonNullOrEmpty(params, "position", "Position is required.");
        requireNonNullOrEmpty(params, "salary", "Salary is required.");
        requirePositiveBigDecimal(params, "salary", "Salary must be a positive number.");
    }


    private void requireValidEmail(String email) {
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            throw new InvalidPersonCreationException("Invalid email format.");
        }
    }

    private void requireValidDate(String dateStr, String errorMessage) {
        try {
            LocalDate.parse(dateStr, dateFormatter);
        } catch (DateTimeParseException e) {
            throw new InvalidPersonCreationException(errorMessage);
        }
    }

    private void requirePositiveDouble(Map<String, String> params, String key, String errorMessage) {
        try {
            double value = Double.parseDouble(params.get(key));
            if (value <= 0) {
                throw new InvalidPersonCreationException(errorMessage);
            }
        } catch (NumberFormatException e) {
            throw new InvalidPersonCreationException(errorMessage + " Must be a valid number.");
        }
    }

    private void requirePositiveBigDecimal(Map<String, String> params, String key, String errorMessage) {
        try {
            BigDecimal value = new BigDecimal(params.get(key));
            if (value.compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidPersonCreationException(errorMessage);
            }
        } catch (NumberFormatException e) {
            throw new InvalidPersonCreationException(errorMessage + " Must be a valid number.");
        }
    }


    private void requireNonNullOrEmpty(Map<String, String> params, String key, String errorMessage) {
        if (!params.containsKey(key) || params.get(key).trim().isEmpty()) {
            throw new InvalidPersonCreationException(errorMessage);
        }
    }

}
