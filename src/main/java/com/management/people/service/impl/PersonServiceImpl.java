package com.management.people.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.management.people.dto.CreatePersonDTO;
import com.management.people.dto.PaginationResponse;
import com.management.people.exception.ResourceNotFoundException;
import com.management.people.model.Person;
import com.management.people.repository.PersonRepository;
import com.management.people.service.PersonService;
import com.management.people.util.PaginationResponseFactory;

@Service
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public PaginationResponse<Person> getPersons(int page, int perPage) {
        Page<Person> personsPage = this.personRepository.findAll(PageRequest.of(page - 1, perPage));
        return PaginationResponseFactory.create(personsPage);
    }

    @Override
    public Person getPersonById(Long personId) {
        return this.personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person with ID: " + personId + " does not exists!"));
    }

    @Override
    public Person createPerson(CreatePersonDTO personDTO) {
        var person = new Person();
        BeanUtils.copyProperties(personDTO, person);
        return this.personRepository.save(person);
    }

    @Override
    public Person updatePerson(Long personId, CreatePersonDTO personDTO) {
        Person person = getPersonById(personId);

        BeanUtils.copyProperties(personDTO, person);
        return this.personRepository.save(person);
    }

}
