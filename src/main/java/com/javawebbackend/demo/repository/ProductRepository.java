package com.javawebbackend.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.javawebbackend.demo.model.Product;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByStatus(String status);
}