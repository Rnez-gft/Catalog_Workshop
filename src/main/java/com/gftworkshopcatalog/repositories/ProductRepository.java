package com.gftworkshopcatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gftworkshopcatalog.model.Product;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

	
}
