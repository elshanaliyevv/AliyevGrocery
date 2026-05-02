package com.example.aliyevgrocery.service;

import com.example.aliyevgrocery.exception.AddressAlreadyExistsException;
import com.example.aliyevgrocery.exception.AddressNotFoundException;
import com.example.aliyevgrocery.exception.UnauthorizedException;
import com.example.aliyevgrocery.exception.UserNotFoundException;
import com.example.aliyevgrocery.mapper.Mapper;
import com.example.aliyevgrocery.model.entity.Address;
import com.example.aliyevgrocery.model.entity.User;
import com.example.aliyevgrocery.model.request.AddressRequest;
import com.example.aliyevgrocery.model.response.AddressResponse;
import com.example.aliyevgrocery.repository.AddressRepo;
import com.example.aliyevgrocery.repository.UserRepo;
import com.example.aliyevgrocery.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepo addressRepo;
    private final UserRepo userRepo;
    private final Mapper mapper;

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getMyAddress() {
        return mapper.toAddressResponse(findAddressByUserId(getAuthenticatedUser().getId()));
    }

    @Override
    @Transactional
    public AddressResponse createAddress(AddressRequest request) {
        User user = getAuthenticatedUser();

        if (addressRepo.findByUserId(user.getId()).isPresent()) {
            throw new AddressAlreadyExistsException("İstifadəçinin artıq ünvanı mövcuddur");
        }

        Address address = mapper.toAddress(request, user);

        return mapper.toAddressResponse(addressRepo.save(address));
    }

    @Override
    @Transactional
    public AddressResponse updateMyAddress(AddressRequest request) {
        Address address = findAddressByUserId(getAuthenticatedUser().getId());

        address.setCity(request.getCity());
        address.setStreet(request.getStreet());
        address.setBuilding(request.getBuilding());
        address.setApartment(request.getApartment());
        address.setNote(request.getNote());

        return mapper.toAddressResponse(addressRepo.save(address));
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        addressRepo.delete(findAddressById(id));
    }

    private Address findAddressByUserId(Long userId) {
        return addressRepo.findByUserId(userId)
                .orElseThrow(() -> new AddressNotFoundException("Ünvan tapılmadı"));
    }

    private Address findAddressById(Long id) {
        return addressRepo.findById(id)
                .orElseThrow(() -> new AddressNotFoundException("Ünvan tapılmadı"));
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new UnauthorizedException("İstifadəçi autentifikasiya olunmayıb");
        }

        return userRepo.findById(userDetails.getId())
                .orElseThrow(() -> new UserNotFoundException("İstifadəçi tapılmadı"));
    }
}
