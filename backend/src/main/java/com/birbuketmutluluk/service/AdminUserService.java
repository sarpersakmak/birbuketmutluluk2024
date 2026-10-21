package com.birbuketmutluluk.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.birbuketmutluluk.dto.AdminUserDetailResponse;
import com.birbuketmutluluk.dto.AdminUserResponse;
import com.birbuketmutluluk.dto.OrderSummaryResponse;
import com.birbuketmutluluk.entity.User;
import com.birbuketmutluluk.exception.ApiException;
import com.birbuketmutluluk.exception.ResourceNotFoundException;
import com.birbuketmutluluk.repository.OrderRepository;
import com.birbuketmutluluk.repository.UserRepository;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public AdminUserService(UserRepository userRepository, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminUserResponse> listUsers(String q) {
        List<User> users = (q == null || q.isBlank())
                ? userRepository.findAllByOrderByIdAsc()
                : userRepository.search(q.trim());

        Map<Long, Long> orderCounts = new HashMap<>();
        for (Object[] row : orderRepository.countOrdersGroupedByUser()) {
            orderCounts.put((Long) row[0], (Long) row[1]);
        }

        return users.stream()
                .map(u -> AdminUserResponse.from(u, orderCounts.getOrDefault(u.getId(), 0L)))
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminUserDetailResponse getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı."));

        List<OrderSummaryResponse> orders = orderRepository
                .findByUserIdOrderByCreatedAtDesc(id).stream()
                .map(o -> new OrderSummaryResponse(
                        o.getId(), o.getOrderNumber(), o.getStatus().name(),
                        o.getTotalPrice(), o.getItems().size(), o.getCreatedAt()))
                .toList();

        return new AdminUserDetailResponse(
                AdminUserResponse.from(user, orders.size()), orders);
    }

    @Transactional
    public AdminUserResponse updateStatus(String adminEmail, Long id, boolean active) {
        User target = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı."));
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Kullanıcı bulunamadı."));

        if (target.getId().equals(admin.getId()) && !active) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Kendi hesabınızı pasife alamazsınız.");
        }

        target.setActive(active);
        return AdminUserResponse.from(target, orderRepository.countByUserId(target.getId()));
    }
}
