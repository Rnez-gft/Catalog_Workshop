package com.gftworkshopcatalog.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "products")
public class Product {

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

    @Column(nullable = false)
    private Long stock;

    @Column(nullable = false)
    private String category;

    @Column
    private Double discount;
    
    @Column(nullable = false)
    private Double weight;


	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", description=" + description + ", price=" + price + ", stock="
				+ stock + ", category=" + category + ", discount=" + discount + ", weight=" + weight + "]";
	}
}