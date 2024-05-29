package com.gftworkshopcatalog.repositories;

import com.gftworkshopcatalog.model.ProductEntity;
import lombok.Generated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Generated
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findByCategoryId(Long categoryId);
    @Query("SELECT p FROM ProductEntity p WHERE p.categoryId = :categoryId AND p.name LIKE :namePrefix")
    List<ProductEntity> findByCategoryIdAndNameStartsWith(Long categoryId, String namePrefix);
}
