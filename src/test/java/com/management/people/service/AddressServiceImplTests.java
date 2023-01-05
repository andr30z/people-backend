package com.management.people.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

import com.management.people.dto.CreateAddressDTO;
import com.management.people.exception.ResourceNotFoundException;
import com.management.people.model.Address;
import com.management.people.model.Person;
import com.management.people.repository.AddressRepository;
import com.management.people.repository.PersonRepository;
import com.management.people.service.impl.AddressServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AddressServiceImplTests {

    @Mock
    private AddressRepository addressRepository;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private PersonService personService;

    private AddressServiceImpl underTest;

    private AutoCloseable autoCloseable;

    private final Person testPerson = new Person(1L, "Test", new Date(), new HashSet<Address>());
    private final Address testAddress = Address.builder()
            .id(2L)
            .addressOwner(testPerson)
            .city("Luziânia")
            .cep("999998")
            .isMainAddress(true)
            .publicPlace("Teste2")
            .build();

    @BeforeEach
    void setUp() throws IOException {

        autoCloseable = MockitoAnnotations.openMocks(this);

        underTest = new AddressServiceImpl(
                addressRepository,
                personRepository,
                personService

        );
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void itShouldGetPersonById(){
        when(addressRepository.findById(anyLong())).thenReturn(Optional.of(testAddress));

        Address address= underTest.getAddressById(testPerson.getId());
        verify(addressRepository, times(1))
        .findById(anyLong());
        assertEquals(address, testAddress);
    }

    @Test
    void itShouldThrowErrorWhenPersonIsNotPresent(){
        when(addressRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> underTest.getAddressById(testAddress.getId()))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining(
            "Address with ID: " + testAddress.getId() + " does not exists!"
        );
    }

    @Test
    void itShouldCreateAnAddress() {
        var personCopy = new Person();
        BeanUtils.copyProperties(testPerson, personCopy);
        var addressCopy = new Address();
        BeanUtils.copyProperties(testAddress, addressCopy);

        CreateAddressDTO createAddressDTO = CreateAddressDTO.builder().build();
        BeanUtils.copyProperties(addressCopy, createAddressDTO);

        when(personService.getPersonById(anyLong())).thenReturn(personCopy);
        when(personRepository.save(any())).thenReturn(personCopy);
        when(addressRepository.save(any())).thenReturn(addressCopy);

        Address result = underTest.createAddress(personCopy.getId(), createAddressDTO);

        verify(addressRepository, times(1))
                .save(any());
        verify(personRepository, times(1))
                .save(any());

        assertEquals(result, addressCopy);

        assertTrue(personCopy.getAddresses().contains(addressCopy));

    }

    @Test
    void itShouldUpdateAddress() {
        var addressCopy = new Address();
        String NEW_CEP = "2221144";
        BeanUtils.copyProperties(testAddress, addressCopy);
        CreateAddressDTO createAddressDTO = CreateAddressDTO.builder().build();
        BeanUtils.copyProperties(addressCopy, createAddressDTO);

        // new cep
        addressCopy.setCep(NEW_CEP);
        createAddressDTO.setCep(NEW_CEP);

        when(personService.getPersonById(anyLong())).thenReturn(testPerson);
        when(addressRepository.save(any())).thenReturn(addressCopy);
        when(addressRepository.findByAddressOwnerAndId(any(), anyLong())).thenReturn(Optional.of(testAddress));

        Address updatedAddress = underTest.updateAddress(testPerson.getId(), testAddress.getId(), createAddressDTO);
        verify(addressRepository, times(1)).save(any());
        assertEquals(updatedAddress.getCep(), createAddressDTO.getCep());
    }

    @Test
    void itShouldMarkPersonMainAddress() {
        var addressCopy = new Address();
        BeanUtils.copyProperties(testAddress, addressCopy);

        when(personService.getPersonById(anyLong())).thenReturn(testPerson);
        when(addressRepository.findByAddressOwnerAndIsMainAddressTrue(any())).thenReturn(Optional.of(addressCopy));
        when(addressRepository.saveAll(any())).thenReturn(new ArrayList<Address>());

        underTest.markMainAddress(testPerson.getId(), testAddress.getId());

    }

}
