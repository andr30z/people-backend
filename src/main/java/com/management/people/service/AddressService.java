package com.management.people.service;

import com.management.people.dto.CreateAddressDTO;
import com.management.people.dto.PaginationResponse;
import com.management.people.model.Address;

public interface AddressService {
  Address createAddress(Long personId, CreateAddressDTO createAddressDTO);

  Address updateAddress(
    Long personId,
    Long addressId,
    CreateAddressDTO createAddressDTO
  );

  PaginationResponse<Address> getAddressesByPersonId(
    Long personId,
    int page,
    int perPage
  );

  Address getAddressById(Long addressId);
}
