package org.suryansh.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.suryansh.entity.Product;

import java.time.LocalDate;
import java.util.List;

public interface ProductRepo extends JpaRepository<Product, Long> {
    Page<Product> findByDateOfSaleBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<Product> findByDateOfSaleBetweenAndTitleContainingOrDescriptionContainingOrPriceContaining(
            LocalDate startDate, LocalDate endDate, String title, String description, String price, Pageable pageable);
}
