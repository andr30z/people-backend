package com.management.people.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.management.people.dto.CreatePersonDTO;
import com.management.people.dto.PaginationResponse;
import com.management.people.exception.ResourceNotFoundException;
import com.management.people.model.Address;
import com.management.people.model.Person;
import com.management.people.repository.PersonRepository;
import com.management.people.service.impl.PersonServiceImpl;
import java.io.IOException;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class PersonServiceImplTests {

  @Mock
  private PersonRepository personRepository;

  @Captor
  ArgumentCaptor<Person> personCaptor;

  @Captor
  private ArgumentCaptor<Pageable> pageableArgumentCaptor;

  private PersonServiceImpl underTest;
  private AutoCloseable autoCloseable;
  private final Person testPerson = new Person(
    1L,
    "Test",
    new Date(),
    new HashSet<Address>()
  );

  @BeforeEach
  void setUp() throws IOException {
    autoCloseable = MockitoAnnotations.openMocks(this);

    underTest = new PersonServiceImpl(personRepository);
  }

  @AfterEach
  void tearDown() throws Exception {
    autoCloseable.close();
  }


  @Test
  void itShouldGetPersonById() {
    when(personRepository.findById(anyLong()))
      .thenReturn(Optional.of(testPerson));

    underTest.getPersonById(testPerson.getId());
    verify(personRepository, times(1)).findById(anyLong());
  }

  @Test
  void itShouldThrowErrorWhenPersonIsNotPresent() {
    when(personRepository.findById(anyLong())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> underTest.getPersonById(testPerson.getId()))
      .isInstanceOf(ResourceNotFoundException.class)
      .hasMessageContaining(
        "Person with ID: " + testPerson.getId() + " does not exists!"
      );
  }

  @Test
  void itShouldCreateAPerson() {
    var personCopy = new Person();
    BeanUtils.copyProperties(testPerson, personCopy);

    CreatePersonDTO createAddressDTO = CreatePersonDTO.builder().build();
    BeanUtils.copyProperties(personCopy, createAddressDTO);
    createAddressDTO.setName("TEST NAME");
    when(personRepository.save(any())).thenReturn(personCopy);

    underTest.createPerson(createAddressDTO);

    verify(personRepository, times(1)).save(personCaptor.capture());

    assertEquals(personCaptor.getValue().getName(), createAddressDTO.getName());
  }

  @Test
  void itShouldUpdateAddress() {
    var personCopy = new Person();
    BeanUtils.copyProperties(testPerson, personCopy);

    CreatePersonDTO createAddressDTO = CreatePersonDTO.builder().build();
    BeanUtils.copyProperties(personCopy, createAddressDTO);
    createAddressDTO.setName("TEST NAME");
    when(personRepository.findById(anyLong()))
      .thenReturn(Optional.of(personCopy));
    when(personRepository.save(any())).thenReturn(personCopy);

    underTest.updatePerson(personCopy.getId(), createAddressDTO);

    verify(personRepository, times(1)).save(personCaptor.capture());

    assertEquals(personCaptor.getValue().getName(), createAddressDTO.getName());
  }

  @Test
  void itShouldSearchAllPersons() {
    var personCopy = new Person();
    BeanUtils.copyProperties(testPerson, personCopy);
    personCopy.setId(233L);
    List<Person> allPersons = List.of(testPerson, personCopy);
    Pageable pageable = PageRequest.of(0, 3);
    when(personRepository.findAll(any(Pageable.class)))
      .thenReturn(new PageImpl<>(allPersons, pageable, 2));

    PaginationResponse<Person> persons = underTest.getPersons(1, 15);
    verify(personRepository, times(1))
      .findAll(pageableArgumentCaptor.capture());
    Pageable capturedPageable = pageableArgumentCaptor.getValue();
    // testing for 0 because the index for database pageable queries starts at 0 and
    // I'm passing 1 as page (1-1 = first page)
    assertEquals(capturedPageable.getPageNumber(), 0);
    assertEquals(capturedPageable.getPageSize(), 15);
    assertEquals(allPersons, persons.getData());
  }
}
