package com.management.people.controller;

import com.management.people.dto.CreateAddressDTO;
import com.management.people.dto.PaginationResponse;
import com.management.people.model.Address;
import com.management.people.service.AddressService;
import jakarta.validation.constraints.Positive;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/address")
public class AddressController {

  private final AddressService addressService;

  public AddressController(AddressService addressService) {
    this.addressService = addressService;
  }

  @GetMapping("/persons/{personId}")
  public PaginationResponse<Address> getAddressByPeron(
      @PathVariable(name = "personId") Long personId,
      @Positive @RequestParam(name = "page", defaultValue = "1") int page,
      @Positive @RequestParam(name = "perPage", defaultValue = "15") int perPage) {
    return this.addressService.getAddressesByPersonId(personId, page, perPage);
  }

  @PostMapping("/persons/{personId}")
  public Address createAddress(
      @PathVariable(name = "personId") Long personId,
      @RequestBody @Validated CreateAddressDTO createAddressDTO) {
    return this.addressService.createAddress(personId, createAddressDTO);
  }

  @PutMapping("/{addressId}/persons/{personId}")
  public Address updateAddress(
      @PathVariable(name = "personId") Long personId,
      @PathVariable(name = "addressId") Long addressId,
      @RequestBody @Validated CreateAddressDTO createAddressDTO) {
    return this.addressService.updateAddress(
        personId,
        addressId,
        createAddressDTO);
  }

  @PatchMapping("/{addressId}/main-address/persons/{personId}")
  public Address markFavorite(@PathVariable(name = "personId") Long personId,
      @PathVariable(name = "addressId") Long addressId) {
    return this.addressService.markMainAddress(personId, addressId);
  }

  @PatchMapping("/main-address/persons/{personId}")
  public Address removeMainAddress(@PathVariable(name = "personId") Long personId) {
    return this.addressService.removeMainAddress(personId);
  }
}
