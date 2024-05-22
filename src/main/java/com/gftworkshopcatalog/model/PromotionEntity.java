package com.gftworkshopcatalog.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Generated
@Table(name= "promotions")
public class PromotionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private Long categoryId;

    @Column(nullable = false)
    private Double discount;

    @Column(nullable = false)
    private String promotionType;

    @Column
    private Integer volumeThreshold;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Boolean isActive;

    @Override
    public String toString() {
        return "PromotionEntity{" +
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
