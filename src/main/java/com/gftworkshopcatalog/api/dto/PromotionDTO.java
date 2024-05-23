package com.gftworkshopcatalog.api.dto;

import lombok.*;

import java.time.LocalDate;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Generated
public class PromotionDTO {
    private Long id;
    private Integer categoryId;
    private Double discount;
    private String promotionType;
    private Integer volumeThreshold;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;

    @Override
    public String toString() {
        return "PromotionDTO{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", discount=" + discount +
                ", promotionType='" + promotionType + '\'' +
                ", volumeThreshold=" + volumeThreshold +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", isActive=" + isActive +
                '}';
    }
}
