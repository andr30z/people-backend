package com.management.people.service.impl;

import com.management.people.dto.CreateAddressDTO;
import com.management.people.dto.PaginationResponse;
import com.management.people.exception.ResourceNotFoundException;
import com.management.people.model.Address;
import com.management.people.model.Person;
import com.management.people.repository.AddressRepository;
import com.management.people.service.AddressService;
import com.management.people.service.PersonService;
import com.management.people.util.PaginationResponseFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AddressServiceImpl implements AddressService {

  private final AddressRepository addressRepository;
  private final PersonService personService;

  public AddressServiceImpl(
    AddressRepository addressRepository,
    PersonService personService
  ) {
    this.addressRepository = addressRepository;
    this.personService = personService;
  }

  @Override
  public Address getAddressById(Long addressId) {
    return this.addressRepository.findById(addressId)
      .orElseThrow(() ->
        new ResourceNotFoundException(
          "Address with ID: " + addressId + " does not exists!"
        )
      );
  }

  @Override
  public Address createAddress(
    Long personId,
    CreateAddressDTO createAddressDTO
  ) {
    Person person = personService.getPersonById(personId);
    var address = new Address();
    BeanUtils.copyProperties(createAddressDTO, address);
    address.setAddressOwner(person);
    return this.addressRepository.save(address);
  }

  @Override
  public PaginationResponse<Address> getAddressesByPersonId(
    Long personId,
    int page,
    int perPage
  ) {
    Page<Address> addressPage =
      this.addressRepository.findAll(PageRequest.of(page - 1, perPage));
    return PaginationResponseFactory.create(addressPage);
  }

  @Override
  public Address updateAddress(
    Long personId,
    Long addressId,
    CreateAddressDTO createAddressDTO
  ) {

    Person owner = this.personService.getPersonById(personId);
    Address address =
      this.addressRepository.findByAddressOwnerAndId(owner, addressId)
        .orElseThrow(() ->
          new ResourceNotFoundException(
            "Address with ID: " +
            addressId +
            " and Owner ID: " +
            personId +
            " does not exists!"
          )
        );
    BeanUtils.copyProperties(createAddressDTO, address);
    return this.addressRepository.save(address);
  }
}
