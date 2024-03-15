package org.example.peoplehubapi.strategy.creation;

import org.example.peoplehubapi.exception.InvalidPersonCreationException;
import org.example.peoplehubapi.exception.InvalidPersonUpdateException;
import org.example.peoplehubapi.person.model.CreatePersonCommand;
import org.example.peoplehubapi.person.model.Person;
import org.example.peoplehubapi.person.model.UpdatePersonCommand;
import org.example.peoplehubapi.strategy.model.Student;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Component("STUDENT")
public class StudentCreationStrategy implements PersonCreationStrategy {
    @Override
    public Person create(CreatePersonCommand command) {
        Map<String, String> params = command.getParams();
        validate(params);
        try {
            return Student.builder()
                    .firstName(params.get("firstName"))
                    .lastName(params.get("lastName"))
                    .pesel(params.get("pesel"))
                    .height(Double.parseDouble(params.get("height")))
                    .weight(Double.parseDouble(params.get("weight")))
                    .email(params.get("email"))
                    .universityName(params.get("universityName"))
                    .yearOfStudy(Integer.parseInt(params.get("yearOfStudy")))
                    .fieldOfStudy(params.get("fieldOfStudy"))
                    .scholarship(new BigDecimal(params.get("scholarship")))
                    .build();
        } catch (NumberFormatException e) {
            throw new InvalidPersonCreationException("Unexpected error during person creation", e);
        }
    }

    @Override
    public Person update(Person person, UpdatePersonCommand command) {
        Map<String, String> params = command.getParams();
        validate(params);
        try {
            Student student = (Student) person;
            student.setFirstName(params.getOrDefault("firstName", student.getFirstName()));
            student.setLastName(params.getOrDefault("lastName", student.getLastName()));
            student.setPesel(params.getOrDefault("pesel", student.getPesel()));
            student.setHeight(Double.parseDouble(params.getOrDefault("height", String.valueOf(student.getHeight()))));
            student.setWeight(Double.parseDouble(params.getOrDefault("weight", String.valueOf(student.getWeight()))));
            student.setEmail(params.getOrDefault("email", student.getEmail()));
            student.setUniversityName(params.getOrDefault("universityName", student.getUniversityName()));
            student.setYearOfStudy(Integer.parseInt(params.getOrDefault("yearOfStudy", String.valueOf(student.getYearOfStudy()))));
            student.setFieldOfStudy(params.getOrDefault("fieldOfStudy", student.getFieldOfStudy()));
            student.setScholarship(new BigDecimal(params.getOrDefault("scholarship", student.getScholarship().toString())));
            return student;
        } catch (NumberFormatException e) {
            throw new InvalidPersonUpdateException("Unexpected error during person update", e);
        }
    }

    @Override
    public Person createFromCsvRecord(String[] record) {
        try {
            return Student.builder()
                    .firstName(record[1])
                    .lastName(record[2])
                    .pesel(record[3])
                    .height(Double.parseDouble(record[4]))
                    .weight(Double.parseDouble(record[5]))
                    .email(record[6])
                    .universityName(record[7])
                    .yearOfStudy(Integer.parseInt(record[8]))
                    .fieldOfStudy(record[9])
                    .scholarship(new BigDecimal(record[10]))
                    .build();
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new InvalidPersonCreationException("Error parsing input parameters", e);
        }
    }

    @Override
    public String getType() {
        return "STUDENT";
    }

    private void validate(Map<String, String> params) {
        requireNonNullOrEmpty(params, "firstName", "First name is required.");
        requireNonNullOrEmpty(params, "lastName", "Last name is required.");
        requireNonNullOrEmpty(params, "pesel", "PESEL is required.");
        validatePesel(params.get("pesel"));
        requirePositiveDouble(params, "height", "Height must be a positive number.");
        requirePositiveDouble(params, "weight", "Weight must be a positive number.");
        requireValidEmail(params.get("email"));
        requireNonNullOrEmpty(params, "universityName", "University name is required.");
        requireNonNegativeInteger(params, "yearOfStudy", "Year of study must be a non-negative number.");
        requireNonNullOrEmpty(params, "fieldOfStudy", "Field of study is required.");
        requirePositiveBigDecimal(params, "scholarship", "Scholarship must be a positive number.");
    }

    private void validatePesel(String pesel) {
        if (!pesel.matches("\\d{11}")) {
            throw new InvalidPersonCreationException("PESEL must consist of 11 digits.");
        }
    }

    private void requireValidEmail(String email) {
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            throw new InvalidPersonCreationException("Invalid email format.");
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

    private void requireNonNegativeInteger(Map<String, String> params, String key, String errorMessage) {
        try {
            int value = Integer.parseInt(params.get(key));
            if (value < 0) {
                throw new InvalidPersonCreationException(errorMessage);
            }
        } catch (NumberFormatException e) {
            throw new InvalidPersonCreationException(errorMessage + " Must be a valid integer.");
        }
    }

    private void requireNonNullOrEmpty(Map<String, String> params, String key, String errorMessage) {
        if (!params.containsKey(key) || params.get(key).trim().isEmpty()) {
            throw new InvalidPersonCreationException(errorMessage);
        }
    }

}
