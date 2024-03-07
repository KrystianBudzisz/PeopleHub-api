package org.example.peoplehubapi.person.specification;

import jakarta.persistence.criteria.*;
import org.example.peoplehubapi.person.model.Person;
import org.example.peoplehubapi.strategy.model.Employee;
import org.example.peoplehubapi.strategy.model.Retiree;
import org.example.peoplehubapi.strategy.model.Student;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PersonSpecification implements Specification<Person> {

    private final PersonSearchCriteria criteria;

    public PersonSpecification(PersonSearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Person> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();

        if (criteria.getType() != null) {
            switch (criteria.getType().toUpperCase()) {
                case "STUDENT":
                    predicates.add(builder.equal(root.type(), Student.class));
                    break;
                case "EMPLOYEE":
                    predicates.add(builder.equal(root.type(), Employee.class));
                    break;
                case "RETIREE":
                    predicates.add(builder.equal(root.type(), Retiree.class));
                    break;

            }
        }

        if ("EMPLOYEE".equalsIgnoreCase(criteria.getType())) {
            if (criteria.getNumberOfProfessionsFrom() != null && criteria.getNumberOfProfessionsTo() != null) {
                Join<Person, Employee> employeeRoot = root.join("employeeDetails", JoinType.LEFT);
                predicates.add(builder.between(employeeRoot.get("numberOfProfessions"), criteria.getNumberOfProfessionsFrom(), criteria.getNumberOfProfessionsTo()));
            }
        }
        if (criteria.getName() != null) {
            predicates.add(builder.like(builder.lower(root.get("firstName")), "%" + criteria.getName().toLowerCase() + "%"));
        }

        if (criteria.getLastName() != null) {
            predicates.add(builder.like(builder.lower(root.get("lastName")), "%" + criteria.getLastName().toLowerCase() + "%"));
        }

        if (criteria.getAgeFrom() != null && criteria.getAgeTo() != null) {
            LocalDate now = LocalDate.now();
            LocalDate maxBirthDate = now.minusYears(criteria.getAgeFrom());
            LocalDate minBirthDate = now.minusYears(criteria.getAgeTo()).minusYears(1);
            predicates.add(builder.between(root.<LocalDate>get("dateOfBirth"), minBirthDate, maxBirthDate));
        }

        if (criteria.getPesel() != null) {
            predicates.add(builder.equal(root.get("pesel"), criteria.getPesel()));
        }


        if (criteria.getGender() != null) {
            predicates.add(builder.equal(root.get("gender"), criteria.getGender()));
        }

        if (criteria.getHeightFrom() != null && criteria.getHeightTo() != null) {
            predicates.add(builder.between(root.get("height"), criteria.getHeightFrom(), criteria.getHeightTo()));
        }

        if (criteria.getWeightFrom() != null && criteria.getWeightTo() != null) {
            predicates.add(builder.between(root.get("weight"), criteria.getWeightFrom(), criteria.getWeightTo()));
        }

        if (criteria.getEmail() != null) {
            predicates.add(builder.like(builder.lower(root.get("email")), "%" + criteria.getEmail().toLowerCase() + "%"));
        }

        if ("EMPLOYEE".equalsIgnoreCase(criteria.getType()) && criteria.getSalaryFrom() != null && criteria.getSalaryTo() != null) {
            predicates.add(builder.between(root.get("salary"), criteria.getSalaryFrom(), criteria.getSalaryTo()));
        }

        if ("STUDENT".equalsIgnoreCase(criteria.getType()) && criteria.getUniversityName() != null) {
            predicates.add(builder.like(builder.lower(root.get("universityName")), "%" + criteria.getUniversityName().toLowerCase() + "%"));
        }
        if (criteria.getEmploymentDateFrom() != null && criteria.getEmploymentDateTo() != null) {
            predicates.add(builder.between(root.<LocalDate>get("employmentDate"), criteria.getEmploymentDateFrom(), criteria.getEmploymentDateTo()));
        }

        if (criteria.getPosition() != null) {
            predicates.add(builder.equal(root.get("position"), criteria.getPosition()));
        }

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
