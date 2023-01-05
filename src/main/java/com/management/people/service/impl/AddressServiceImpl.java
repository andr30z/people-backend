package com.management.people.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.management.people.dto.CreateAddressDTO;
import com.management.people.dto.PaginationResponse;
import com.management.people.exception.BadRequestException;
import com.management.people.exception.ResourceNotFoundException;
import com.management.people.model.Address;
import com.management.people.model.Person;
import com.management.people.repository.AddressRepository;
import com.management.people.repository.PersonRepository;
import com.management.people.service.AddressService;
import com.management.people.service.PersonService;
import com.management.people.util.PaginationResponseFactory;

@Service
public class AddressServiceImpl implements AddressService {

  private final AddressRepository addressRepository;

  private final PersonRepository personRepository;
  private final PersonService personService;

  public AddressServiceImpl(
      AddressRepository addressRepository,
      PersonRepository personRepository,
      PersonService personService) {
    this.addressRepository = addressRepository;
    this.personService = personService;
    this.personRepository = personRepository;
  }

  @Override
  public Address getAddressById(Long addressId) {
    return this.addressRepository.findById(addressId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Address with ID: " + addressId + " does not exists!"));
  }

  @Override
  public Address createAddress(
      Long personId,
      CreateAddressDTO createAddressDTO) {
    Person person = personService.getPersonById(personId);
    var address = new Address();
    BeanUtils.copyProperties(createAddressDTO, address);
    address.setAddressOwner(person);
    Address savedAddress = this.addressRepository.save(address);
    person.getAddresses().add(savedAddress);
    personRepository.save(person);
    return savedAddress;
  }

  @Override
  public PaginationResponse<Address> getAddressesByPersonId(
      Long personId,
      int page,
      int perPage) {

    Person person = this.personService.getPersonById(personId);
    Page<Address> addressPage = this.addressRepository.findAllByAddressOwner(person, PageRequest.of(page - 1, perPage));
    return PaginationResponseFactory.create(addressPage);
  }

  @Override
  public Address updateAddress(
      Long personId,
      Long addressId,
      CreateAddressDTO createAddressDTO) {

    Person owner = this.personService.getPersonById(personId);
    Address address = this.addressRepository.findByAddressOwnerAndId(owner, addressId)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Address with ID: " +
                addressId +
                " and Owner ID: " +
                personId +
                " does not exists!"));
    BeanUtils.copyProperties(createAddressDTO, address);
    return this.addressRepository.save(address);
  }

  @Override
  public Address markMainAddress(Long personId, Long addressId) {
    Address address = getAddressById(addressId);
    if (address.getAddressOwner().getId() != personId)
      throw new BadRequestException("The address does not belong to the person with ID: " + personId);

    Person currentPerson = this.personService.getPersonById(personId);

    Optional<Address> mainAddress = this.addressRepository
        .findByAddressOwnerAndIsMainAddressTrue(currentPerson);

    List<Address> addressesToSave = new ArrayList<>();

    if (mainAddress.isPresent() && mainAddress.get().getId() != addressId) {
      mainAddress.get().setMainAddress(false);
      addressesToSave.add(mainAddress.get());
    }
    address.setMainAddress(true);

    this.addressRepository.saveAll(addressesToSave);
    return address;
  }

  @Override
  public Address removeMainAddress(Long personId) {

    Person currentPerson = this.personService.getPersonById(personId);
    Address mainAddress = this.addressRepository.findByAddressOwnerAndIsMainAddressTrue(currentPerson)
        .orElseThrow(() -> new ResourceNotFoundException(
            "The person with ID: " + personId + " does not have an address marked as main!"));

    mainAddress.setMainAddress(false);
    return this.addressRepository.save(mainAddress);

  }
}
