package com.management.people.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.management.people.model.Address;
import com.management.people.model.Person;

@DataJpaTest
@TestInstance(Lifecycle.PER_CLASS)
public class AddressRepositoryTests {

        @Autowired
        private AddressRepository underTest;

        @Autowired
        private PersonRepository personRepository;
        private final Person person = new Person(1L, "Test", new Date(), new HashSet<>());
        private final Person person2 = new Person(2L, "Test2", new Date(), new HashSet<>());
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
                                        .build(),
                        Address.builder()
                                        .id(3L)
                                        .addressOwner(person2)
                                        .city("Luziânia")
                                        .cep("999998")
                                        .isMainAddress(true)
                                        .publicPlace("Teste2")
                                        .build());

        @BeforeAll
        void setUp() {
                this.personRepository.saveAll(List.of(person, person2));
                this.underTest.saveAll(addresses);
        }

        @AfterAll
        void close() {
                this.personRepository.deleteAll();
                this.underTest.deleteAll();
        }

        @Test
        void itShouldFindByAddressOwnerAndIsMainAddressTrue() {
                Optional<Address> result = underTest.findByAddressOwnerAndIsMainAddressTrue(person);

                assertEquals(result.isPresent(), true);
                assertEquals(result.get(), addresses.get(1));
        }

        @Test
        void itShouldFindAllByOwner() {
                Pageable paging = PageRequest.of(0, 3);
                Page<Address> result = underTest.findAllByAddressOwner(person, paging);
                List<Address> addressesResults = result.get().collect(Collectors.toList());

                Address addressOwnedBySomeoneElse = addresses.get(2);
                List<Address> addressesOwnedByTestPerson = List.of(addresses.get(0),
                                addresses.get(1));

                assertTrue(addressesResults.containsAll(addressesOwnedByTestPerson));
                assertFalse(addressesResults.contains(addressOwnedBySomeoneElse));

        }

        @Test
        void itShouldFindByAddressOwnerAndId() {

                Address currentAddress = addresses.get(0);
                Optional<Address> result = underTest.findByAddressOwnerAndId(person,
                                currentAddress.getId());

                assertEquals(result.isPresent(), true);
                assertEquals(result.get(), currentAddress);

        }

}
