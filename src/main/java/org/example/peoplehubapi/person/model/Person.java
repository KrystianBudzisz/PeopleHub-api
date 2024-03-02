package org.example.peoplehubapi.person.model;

import jakarta.persistence.*;
import lombok.*;


@Table(name = "persons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED) // Strategia dziedziczenia
public abstract class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String pesel; // Unikalny identyfikator

    @Column(nullable = true)
    private Double height;

    @Column(nullable = true)
    private Double weight;

    @Column(nullable = false, unique = true)
    private String email;



}
