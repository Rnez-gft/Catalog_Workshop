package com.gftworkshopcatalog.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Generated
@Table(name= "promotions")
public class PromotionEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long promotionId;

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
                "promotionId=" + promotionId +
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
