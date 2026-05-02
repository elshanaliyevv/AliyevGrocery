package com.example.aliyevgrocery.repository;

import com.example.aliyevgrocery.model.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepo extends JpaRepository<Address, Long> {

    Optional<Address> findByUserId(Long userId);
}
