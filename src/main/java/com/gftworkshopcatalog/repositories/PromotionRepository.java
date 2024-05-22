package com.gftworkshopcatalog.repositories;

import com.gftworkshopcatalog.model.PromotionEntity;
import lombok.Generated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Generated
@Repository
public interface PromotionRepository extends JpaRepository<PromotionEntity, Long> {

}
