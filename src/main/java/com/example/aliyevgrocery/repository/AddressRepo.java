package com.example.aliyevgrocery.repository;

import com.example.aliyevgrocery.model.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepo extends JpaRepository<Address, Long> {

    List<Address> findAllByUserId(Long userId);
}
