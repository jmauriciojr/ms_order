package com.ambev.ms_order.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ambev.ms_order.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
	Optional<Order> findByExternalId(String externalId);
}