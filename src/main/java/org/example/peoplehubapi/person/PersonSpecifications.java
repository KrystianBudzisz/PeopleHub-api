package org.example.peoplehubapi.person;

import jakarta.persistence.criteria.Predicate;
import org.example.peoplehubapi.person.model.Person;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PersonSpecifications {
    public static Specification<Person> bySearchCriteria(SearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getType() != null) {
                predicates.add(cb.equal(root.get("type"), criteria.getType()));
            }
            if (criteria.getName() != null) {
                predicates.add(cb.like(cb.lower(root.get("firstName")), "%" + criteria.getName().toLowerCase() + "%"));
            }
            if (criteria.getLastName() != null) {
                predicates.add(cb.like(cb.lower(root.get("lastName")), "%" + criteria.getLastName().toLowerCase() + "%"));
            }
            if (criteria.getPesel() != null) {
                predicates.add(cb.equal(root.get("pesel"), criteria.getPesel()));
            }
            if (criteria.getEmail() != null) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + criteria.getEmail().toLowerCase() + "%"));
            }
            if (criteria.getMinHeight() != null && criteria.getMaxHeight() != null) {
                predicates.add(cb.between(root.get("height"), criteria.getMinHeight(), criteria.getMaxHeight()));
            }
            if (criteria.getMinWeight() != null && criteria.getMaxWeight() != null) {
                predicates.add(cb.between(root.get("weight"), criteria.getMinWeight(), criteria.getMaxWeight()));
            }

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
