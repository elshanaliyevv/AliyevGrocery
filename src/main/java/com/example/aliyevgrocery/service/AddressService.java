package com.example.aliyevgrocery.service;

import com.example.aliyevgrocery.model.request.AddressRequest;
import com.example.aliyevgrocery.model.response.AddressResponse;

public interface AddressService {

    AddressResponse getMyAddress();

    AddressResponse createAddress(AddressRequest request);

    AddressResponse updateMyAddress(AddressRequest request);

    void deleteAddress(Long id);
}
