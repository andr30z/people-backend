package com.management.people.repository;

import com.management.people.model.Address;
import com.management.people.model.Person;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
  Optional<Address> findByAddressOwnerAndId(Person addressOwner, Long id);

  Optional<Address> findByAddressOwnerAndIsMainAddressTrue(Person addressOwner);
}
