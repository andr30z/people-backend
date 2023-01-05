package com.management.people.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.management.people.model.Address;
import com.management.people.model.Person;

@DataJpaTest
public class AddressRepositoryTests {

    @Autowired
    private AddressRepository underTest;

    @Autowired
    private PersonRepository personRepository;

    @Test
    void itShouldFindByAddressOwnerAndIsMainAddressTrue() {
        var person = new Person(1L, "Test", new Date(), new HashSet<>());
        personRepository.save(person);
        List<Address> addresses = List.of(
                Address.builder()
                        .id(1L)
                        .addressOwner(person)
                        .city("Luziânia")
                        .cep("999999").isMainAddress(false)
                        .publicPlace("Teste")
                        .build(),
                Address.builder()
                        .id(2L)
                        .addressOwner(person)
                        .city("Luziânia")
                        .cep("999998")
                        .isMainAddress(true)
                        .publicPlace("Teste2")
                        .build());

        this.underTest.saveAll(addresses);

        Optional<Address> result = underTest.findByAddressOwnerAndIsMainAddressTrue(person);

        assertEquals(result.isPresent(), true);
        assertEquals(result.get(), addresses.get(1));

    }

    @Test
    void itShouldFindByAddressOwnerAndId() {
        var person = new Person(1L, "Test", new Date(), new HashSet<>());
        personRepository.save(person);
        List<Address> addresses = List.of(
                Address.builder()
                        .id(1L)
                        .addressOwner(person)
                        .city("Luziânia")
                        .cep("999999").isMainAddress(false)
                        .publicPlace("Teste")
                        .build(),
                Address.builder()
                        .id(2L)
                        .addressOwner(person)
                        .city("Luziânia")
                        .cep("999998")
                        .isMainAddress(true)
                        .publicPlace("Teste2")
                        .build());

        this.underTest.saveAll(addresses);

        Address currentAddress = addresses.get(0);
        Optional<Address> result = underTest.findByAddressOwnerAndId(person,
                currentAddress.getId());

        assertEquals(result.isPresent(), true);
        assertEquals(result.get(), currentAddress);

    }
}
