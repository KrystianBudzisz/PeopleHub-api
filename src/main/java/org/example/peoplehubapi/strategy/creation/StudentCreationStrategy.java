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
        try {
            Map<String, String> params = command.getParams();
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
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new InvalidPersonCreationException("Error parsing input parameters", e);
        }
    }

    @Override
    public Person update(Person person, UpdatePersonCommand command) {
        try {
            Student student = (Student) person;
            Map<String, String> params = command.getParams();

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
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new InvalidPersonUpdateException("Error parsing input parameters", e);
        }
    }


    @Override
    public String getType() {
        return "STUDENT";
    }
}
