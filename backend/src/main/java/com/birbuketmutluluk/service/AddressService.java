package com.birbuketmutluluk.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.birbuketmutluluk.dto.AddressRequest;
import com.birbuketmutluluk.dto.AddressResponse;
import com.birbuketmutluluk.entity.Address;
import com.birbuketmutluluk.entity.User;
import com.birbuketmutluluk.exception.ApiException;
import com.birbuketmutluluk.exception.ResourceNotFoundException;
import com.birbuketmutluluk.repository.AddressRepository;
import com.birbuketmutluluk.repository.UserRepository;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getMyAddresses(String email) {
        User user = resolveUser(email);
        return addressRepository.findByUserIdOrderByIsDefaultDescIdAsc(user.getId()).stream()
                .map(AddressResponse::from)
                .toList();
    }

    @Transactional
    public AddressResponse create(String email, AddressRequest request) {
        User user = resolveUser(email);

        List<Address> existing = addressRepository.findByUserIdOrderByIsDefaultDescIdAsc(user.getId());
        boolean makeDefault = Boolean.TRUE.equals(request.isDefault()) || existing.isEmpty();

        if (makeDefault) {
            existing.forEach(a -> a.setDefault(false));
        }

        Address address = Address.builder()
                .user(user)
                .title(request.title())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phone(request.phone())
                .city(request.city())
                .district(request.district())
                .neighborhood(request.neighborhood())
                .addressDetail(request.addressDetail())
                .postalCode(request.postalCode())
                .isDefault(makeDefault)
                .build();

        return AddressResponse.from(addressRepository.save(address));
    }

    @Transactional
    public AddressResponse update(String email, Long id, AddressRequest request) {
        User user = resolveUser(email);
        Address address = requireOwned(user, id);

        if (Boolean.TRUE.equals(request.isDefault()) && !address.isDefault()) {
            addressRepository.findByUserIdOrderByIsDefaultDescIdAsc(user.getId())
                    .forEach(a -> a.setDefault(false));
            address.setDefault(true);
        }

        address.setTitle(request.title());
        address.setFirstName(request.firstName());
        address.setLastName(request.lastName());
        address.setPhone(request.phone());
        address.setCity(request.city());
        address.setDistrict(request.district());
        address.setNeighborhood(request.neighborhood());
        address.setAddressDetail(request.addressDetail());
        address.setPostalCode(request.postalCode());

        return AddressResponse.from(address);
    }

    @Transactional
    public void delete(String email, Long id) {
        User user = resolveUser(email);
        Address address = requireOwned(user, id);
        boolean wasDefault = address.isDefault();

        addressRepository.delete(address);

        // Varsayılan silindiyse kalanlardan ilkini varsayılan yap.
        if (wasDefault) {
            List<Address> remaining = addressRepository.findByUserIdOrderByIsDefaultDescIdAsc(user.getId());
            if (!remaining.isEmpty()) {
                remaining.get(0).setDefault(true);
            }
        }
    }

    private User resolveUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Kullanıcı bulunamadı."));
    }

    private Address requireOwned(User user, Long id) {
        return addressRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Adres bulunamadı."));
    }
}
