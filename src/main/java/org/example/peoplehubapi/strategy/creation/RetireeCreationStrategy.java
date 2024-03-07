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
        try {
            Map<String, String> params = command.getParams();
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
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new InvalidPersonCreationException("Error parsing input parameters", e);
        }
    }

    @Override
    public Person update(Person person, UpdatePersonCommand command) {
        try {
            Retiree retiree = (Retiree) person;
            Map<String, String> params = command.getParams();

            retiree.setFirstName(params.getOrDefault("firstName", retiree.getFirstName()));
            retiree.setLastName(params.getOrDefault("lastName", retiree.getLastName()));
            retiree.setPesel(params.getOrDefault("pesel", retiree.getPesel()));
            retiree.setHeight(Double.parseDouble(params.getOrDefault("height", String.valueOf(retiree.getHeight()))));
            retiree.setWeight(Double.parseDouble(params.getOrDefault("weight", String.valueOf(retiree.getWeight()))));
            retiree.setEmail(params.getOrDefault("email", retiree.getEmail()));
            retiree.setPensionAmount(new BigDecimal(params.getOrDefault("pensionAmount", retiree.getPensionAmount().toString())));
            retiree.setYearsWorked(Integer.parseInt(params.getOrDefault("yearsWorked", String.valueOf(retiree.getYearsWorked()))));

            return retiree;
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new InvalidPersonUpdateException("Error parsing input parameters", e);
        }
    }

    @Override
    public String getType() {
        return "RETIREE";
    }
}
