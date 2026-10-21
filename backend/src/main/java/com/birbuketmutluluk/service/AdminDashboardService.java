package com.birbuketmutluluk.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.birbuketmutluluk.dto.DashboardResponse;
import com.birbuketmutluluk.entity.OrderStatus;
import com.birbuketmutluluk.repository.OrderRepository;
import com.birbuketmutluluk.repository.ProductRepository;
import com.birbuketmutluluk.repository.UserRepository;

@Service
public class AdminDashboardService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public AdminDashboardService(UserRepository userRepository,
                                 ProductRepository productRepository,
                                 OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getStats() {
        return new DashboardResponse(
                userRepository.count(),
                userRepository.countByActiveTrue(),
                productRepository.count(),
                orderRepository.count(),
                orderRepository.countByStatus(OrderStatus.PENDING),
                orderRepository.countByStatus(OrderStatus.PREPARING),
                orderRepository.countByStatus(OrderStatus.DELIVERED),
                orderRepository.sumTotalExcludingStatus(OrderStatus.CANCELLED));
    }
}
