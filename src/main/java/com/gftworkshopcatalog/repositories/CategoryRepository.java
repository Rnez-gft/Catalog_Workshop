package com.gftworkshopcatalog.repositories;

import com.gftworkshopcatalog.model.CategoryEntity;

import lombok.Generated;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Generated
@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
}
