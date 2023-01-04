package com.management.people.service;

import com.management.people.dto.CreatePersonDTO;
import com.management.people.dto.PaginationResponse;
import com.management.people.model.Person;

public interface PersonService {
    Person createPerson(CreatePersonDTO personDTO);

    Person updatePerson(Long personId, CreatePersonDTO personDTO);

    Person getPersonById(Long personId);

    PaginationResponse<Person> getPersons(int page, int perPage);
}
