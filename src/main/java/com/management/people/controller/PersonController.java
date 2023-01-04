package com.management.people.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.management.people.dto.CreatePersonDTO;
import com.management.people.dto.PaginationResponse;
import com.management.people.model.Person;
import com.management.people.service.PersonService;

import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/v1/person")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    public Person createPerson(@RequestBody @Validated CreatePersonDTO createPersonDTO) {
        return personService.createPerson(createPersonDTO);
    }

    @PutMapping("/{personId}")
    public Person updatePerson(@PathVariable(name = "personId") Long personId,
            @RequestBody @Validated CreatePersonDTO personDTO) {
        return this.personService.updatePerson(personId, personDTO);
    }

    @GetMapping("/{personId}")
    public Person getPerson(@PathVariable(name = "personId") Long personId) {
        return this.personService.getPersonById(personId);
    }

    @Validated
    @GetMapping
    public PaginationResponse<Person> getPersons(
            @Positive @RequestParam(name = "perPage", defaultValue = "15") int perPage,
            @Positive @RequestParam(name = "page", defaultValue = "1") int page) {
        return this.personService.getPersons(page, perPage);
    }

}
