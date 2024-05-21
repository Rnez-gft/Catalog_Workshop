package com.gftworkshopcatalog.controllers;

import com.gftworkshopcatalog.services.impl.PromotionServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/promotions")
@Tag(name = "Promotions", description = "Everything about the promotions")
public class PromotionController {
    PromotionServiceImpl promotionServiceImpl;

    public PromotionController(PromotionServiceImpl promotionServiceImpl) {
        super();
        this.promotionServiceImpl = promotionServiceImpl;
    }
}
