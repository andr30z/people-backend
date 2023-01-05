package com.management.people.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Long id;


    @Column(name = "public_place")
    private String publicPlace; // logradouro

    private String cep;

    private Integer number;

    private String city;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person addressOwner;

    // Logradouro
    // o CEP
    // o Número
    // o Cidade

}
