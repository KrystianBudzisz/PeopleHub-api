package org.example.peoplehubapi.strategy.creation;

import org.example.peoplehubapi.exception.InvalidPersonCreationException;
import org.example.peoplehubapi.exception.InvalidPersonUpdateException;
import org.example.peoplehubapi.person.model.CreatePersonCommand;
import org.example.peoplehubapi.person.model.Person;
import org.example.peoplehubapi.person.model.UpdatePersonCommand;
import org.example.peoplehubapi.strategy.model.Retiree;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Component("RETIREE")
public class RetireeCreationStrategy implements PersonCreationStrategy {

    @Override
    public Person create(CreatePersonCommand command) {
        Map<String, String> params = command.getParams();
        validate(params);
        try {
            return Retiree.builder()
                    .firstName(params.get("firstName"))
                    .lastName(params.get("lastName"))
                    .pesel(params.get("pesel"))
                    .height(Double.parseDouble(params.get("height")))
                    .weight(Double.parseDouble(params.get("weight")))
                    .email(params.get("email"))
                    .pensionAmount(new BigDecimal(params.get("pensionAmount")))
                    .yearsWorked(Integer.parseInt(params.get("yearsWorked")))
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
            Retiree retiree = (Retiree) person;

            retiree.setFirstName(params.getOrDefault("firstName", retiree.getFirstName()));
            retiree.setLastName(params.getOrDefault("lastName", retiree.getLastName()));
            retiree.setPesel(params.getOrDefault("pesel", retiree.getPesel()));
            retiree.setHeight(Double.parseDouble(params.getOrDefault("height", String.valueOf(retiree.getHeight()))));
            retiree.setWeight(Double.parseDouble(params.getOrDefault("weight", String.valueOf(retiree.getWeight()))));
            retiree.setEmail(params.getOrDefault("email", retiree.getEmail()));
            retiree.setPensionAmount(new BigDecimal(params.getOrDefault("pensionAmount", retiree.getPensionAmount().toString())));
            retiree.setYearsWorked(Integer.parseInt(params.getOrDefault("yearsWorked", String.valueOf(retiree.getYearsWorked()))));

            return retiree;
        } catch (NumberFormatException e) {
            throw new InvalidPersonUpdateException("Unexpected error during person update", e);
        }
    }

    @Override
    public Person createFromCsvRecord(String[] record) {
        try {
            return Retiree.builder()
                    .firstName(record[1])
                    .lastName(record[2])
                    .pesel(record[3])
                    .height(Double.parseDouble(record[4]))
                    .weight(Double.parseDouble(record[5]))
                    .email(record[6])
                    .pensionAmount(new BigDecimal(record[7]))
                    .yearsWorked(Integer.parseInt(record[8]))
                    .build();
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new InvalidPersonCreationException("Error parsing input parameters", e);
        }
    }

    @Override
    public String getType() {
        return "RETIREE";
    }

    private void validate(Map<String, String> params) {
        requireNonNullOrEmpty(params, "firstName", "First name is required.");
        requireNonNullOrEmpty(params, "lastName", "Last name is required.");
        requireNonNullOrEmpty(params, "pesel", "PESEL is required.");
        requirePositiveDouble(params, "height", "Height must be a positive number.");
        requirePositiveDouble(params, "weight", "Weight must be a positive number.");
        requireValidEmail(params.get("email"));
        requirePositiveBigDecimal(params, "pensionAmount", "Pension amount must be a positive number.");
        requireNonNegativeInteger(params, "yearsWorked", "Years worked must be a non-negative number.");
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
