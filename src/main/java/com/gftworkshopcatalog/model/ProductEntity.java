package com.gftworkshopcatalog.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Generated
@Builder
@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "category_id")
    private CategoryEntity category;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private Integer current_stock;

    @Column(nullable = false)
    private Integer min_stock;



    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", category=" + categoryId +
                ", weight=" + weight +
                ", current_stock=" + current_stock +
                ", min_stock=" + min_stock +
                '}';
    }
}