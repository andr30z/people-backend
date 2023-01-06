package com.management.people.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.management.people.dto.PaginationResponse;
import com.management.people.model.Address;
import com.management.people.model.Person;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class PaginationResponseFactoryTests {

  @Test
  void itShouldCreateAPaginationResponse() {
    Person testPerson = new Person(
      1L,
      "Test",
      new Date(),
      new HashSet<Address>()
    );
    List<Person> allPersons = List.of(testPerson);
    Pageable pageable = PageRequest.of(0, 1);
    PaginationResponse<Person> result = PaginationResponseFactory.create(
      new PageImpl<>(allPersons, pageable, 1)
    );

    assertEquals(allPersons, result.getData());
    assertEquals(result.getCurrentPage(), 1);
    assertEquals(result.getTotalPages(), 1);
    assertEquals(result.getTotalItems(), 1);
  }
}
