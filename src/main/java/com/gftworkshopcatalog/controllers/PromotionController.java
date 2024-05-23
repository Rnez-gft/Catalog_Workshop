package com.gftworkshopcatalog.controllers;

import com.gftworkshopcatalog.model.PromotionEntity;
import com.gftworkshopcatalog.services.impl.PromotionServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/promotions")
@Tag(name = "Promotions", description = "Everything about the promotions")
public class PromotionController {
    private final PromotionServiceImpl promotionService;

    public PromotionController(PromotionServiceImpl promotionService) {
        super();
        this.promotionService = promotionService;
    }
    @GetMapping
    public ResponseEntity<List<PromotionEntity>> getAllPromotions() {
        List<PromotionEntity> promotions = promotionService.findAllPromotions();
        return ResponseEntity.ok(promotions);
    }
    @PostMapping
    public ResponseEntity<?> addPromotion(@RequestBody PromotionEntity promotionEntity) {
        PromotionEntity createdPromotion = promotionService.addPromotion(promotionEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPromotion);
    }
    @PutMapping("/{id}")
    public ResponseEntity<PromotionEntity> updatePromotion(@PathVariable long id, @RequestBody PromotionEntity promotionDetails){
        PromotionEntity updatedPromotion = promotionService.updatePromotion(id, promotionDetails);
        return ResponseEntity.ok(updatedPromotion);
    }

}
