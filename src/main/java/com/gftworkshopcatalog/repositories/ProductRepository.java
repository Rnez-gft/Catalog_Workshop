package com.gftworkshopcatalog.repositories;

import lombok.Generated;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gftworkshopcatalog.model.ProductEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Generated
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long>{
    List<ProductEntity> findByCategoryId(Integer categoryId);
	
}
