package com.gftworkshopcatalog.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Generated
@Entity
@Table(name = "categories")
public class CategoryEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "category_id", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductEntity> products;


    @Override
    public String toString() {
        return "CategoryEntity{" +
                "category_Id=" + categoryId +
                ", name='" + name + '\'' +
                ", products=" + products +
                '}';
    }


}



