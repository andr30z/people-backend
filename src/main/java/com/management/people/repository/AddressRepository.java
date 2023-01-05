package com.management.people.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.management.people.model.Address;
import com.management.people.model.Person;

public interface AddressRepository extends JpaRepository<Address, Long> {
  Optional<Address> findByAddressOwnerAndId(Person addressOwner, Long id);

  Page<Address> findAllByAddressOwner(Person addressOwner, Pageable pageable);

  Optional<Address> findByAddressOwnerAndIsMainAddressTrue(Person addressOwner);
}
