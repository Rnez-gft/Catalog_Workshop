package com.gftworkshopcatalog.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Generated
@Builder
@Entity
@Table(name = "categories")
public class CategoryEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(nullable = false)
    private String name;


    @Override
    public String toString() {
        return "CategoryEntity{" +
                "category_Id=" + categoryId +
                ", name='" + name + '\'' +
                '}';
    }


}



