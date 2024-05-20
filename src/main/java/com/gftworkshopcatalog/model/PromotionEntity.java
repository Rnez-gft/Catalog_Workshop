package com.gftworkshopcatalog.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name= "promotions")
public class PromotionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(nullable = false)
    private Integer categoryId;

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
                '}';
    }
}
