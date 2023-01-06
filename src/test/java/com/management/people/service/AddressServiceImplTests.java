package com.management.people.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.management.people.dto.CreateAddressDTO;
import com.management.people.exception.ResourceNotFoundException;
import com.management.people.model.Address;
import com.management.people.model.Person;
import com.management.people.repository.AddressRepository;
import com.management.people.repository.PersonRepository;
import com.management.people.service.impl.AddressServiceImpl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;

@ExtendWith(MockitoExtension.class)
public class AddressServiceImplTests {

  @Mock
  private AddressRepository addressRepository;

  @Mock
  private PersonRepository personRepository;

  @Mock
  private PersonService personService;

  @Captor
  ArgumentCaptor<List<Address>> addressesCaptor;

  @Captor
  ArgumentCaptor<Address> addressCaptor;

  private AddressServiceImpl underTest;

  private AutoCloseable autoCloseable;

  private final Person testPerson = new Person(
    1L,
    "Test",
    new Date(),
    new HashSet<Address>()
  );
  private final Address testAddress = Address
    .builder()
    .id(2L)
    .addressOwner(testPerson)
    .city("Luziânia")
    .cep("999998")
    .isMainAddress(false)
    .publicPlace("Teste2")
    .build();

  @BeforeEach
  void setUp() throws IOException {
    autoCloseable = MockitoAnnotations.openMocks(this);

    underTest =
      new AddressServiceImpl(
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
  void itShouldGetAddressById() {
    when(addressRepository.findById(anyLong()))
      .thenReturn(Optional.of(testAddress));

    Address address = underTest.getAddressById(testPerson.getId());
    verify(addressRepository, times(1)).findById(anyLong());
    assertEquals(address, testAddress);
  }

  @Test
  void itShouldThrowErrorWhenPersonIsNotPresent() {
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

    Address result = underTest.createAddress(
      personCopy.getId(),
      createAddressDTO
    );

    verify(addressRepository, times(1)).save(any());
    verify(personRepository, times(1)).save(any());

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
    when(addressRepository.findByAddressOwnerAndId(any(), anyLong()))
      .thenReturn(Optional.of(testAddress));

    Address updatedAddress = underTest.updateAddress(
      testPerson.getId(),
      testAddress.getId(),
      createAddressDTO
    );
    verify(addressRepository, times(1)).save(any());
    assertEquals(updatedAddress.getCep(), createAddressDTO.getCep());
  }

  @Test
  void itShouldMarkPersonMainAddress() {
    var addressCopy = new Address();
    BeanUtils.copyProperties(testAddress, addressCopy);
    addressCopy.setMainAddress(false);
    when(personService.getPersonById(anyLong())).thenReturn(testPerson);
    when(addressRepository.findById(anyLong()))
      .thenReturn(Optional.of(addressCopy));
    when(addressRepository.findByAddressOwnerAndIsMainAddressTrue(any()))
      .thenReturn(Optional.of(addressCopy));
    when(addressRepository.saveAll(any())).thenReturn(new ArrayList<Address>());

    Address result = underTest.markMainAddress(
      testPerson.getId(),
      testAddress.getId()
    );

    verify(addressRepository, times(1)).saveAll(addressesCaptor.capture());

    List<Address> savedAddresses = addressesCaptor.getValue();
    assertEquals(savedAddresses.size(), 1);
    assertTrue(result.isMainAddress());
  }

  @Test
  void itShouldMarkPersonMainAddressAndUnmarkOldMainAddress() {
    var addressCopy = new Address();
    var oldMainAddress = new Address();
    BeanUtils.copyProperties(testAddress, addressCopy);
    BeanUtils.copyProperties(testAddress, oldMainAddress);

    oldMainAddress.setId(99L);
    addressCopy.setMainAddress(false);

    oldMainAddress.setMainAddress(true);

    when(personService.getPersonById(anyLong())).thenReturn(testPerson);
    when(addressRepository.findById(anyLong()))
      .thenReturn(Optional.of(addressCopy));
    when(addressRepository.findByAddressOwnerAndIsMainAddressTrue(any()))
      .thenReturn(Optional.of(oldMainAddress));

    when(addressRepository.saveAll(any())).thenReturn(new ArrayList<Address>());

    Address result = underTest.markMainAddress(
      testPerson.getId(),
      testAddress.getId()
    );

    verify(addressRepository, times(1)).saveAll(addressesCaptor.capture());

    List<Address> savedAddresses = addressesCaptor.getValue();
    assertEquals(savedAddresses.size(), 2);

    Address oldAddress = savedAddresses.get(0);
    assertFalse(oldAddress.isMainAddress());

    assertTrue(result.isMainAddress());
  }

  @Test
  void itShouldRemoveUserMainAddress() {
    var oldMainAddress = new Address();
    BeanUtils.copyProperties(testAddress, oldMainAddress);
    oldMainAddress.setMainAddress(true);

    when(personService.getPersonById(anyLong())).thenReturn(testPerson);
    when(addressRepository.findByAddressOwnerAndIsMainAddressTrue(any()))
      .thenReturn(Optional.of(oldMainAddress));

    when(addressRepository.save(any())).thenReturn(oldMainAddress);

    underTest.removeMainAddress(testPerson.getId());

    verify(addressRepository, times(1)).save(addressCaptor.capture());

    Address savedAddress = addressCaptor.getValue();
    assertFalse(savedAddress.isMainAddress());
  }
}
