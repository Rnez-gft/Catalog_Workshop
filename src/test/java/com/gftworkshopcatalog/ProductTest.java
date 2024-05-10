package com.gftworkshopcatalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.gftworkshopcatalog.model.Product;

public class ProductTest {

	  @Test
	  @DisplayName("Test para getters y setters")
	    public void test_GettersAndSetters() {
	        Product product = new Product();

	        product.setId(1L);
	        product.setName("Product Name");
	        product.setDescription("Product Description");
	        product.setPrice(10.0);
	        product.setStock(100L);
	        product.setCategory("Product Category");
	        product.setDiscount(0.1);
	        product.setWeight(0.3);

	        assertEquals(1L, product.getId());
	        assertEquals("Product Name", product.getName());
	        assertEquals("Product Description", product.getDescription());
	        assertEquals(10.0, product.getPrice());
	        assertEquals(100L, product.getStock());
	        assertEquals("Product Category", product.getCategory());
	        assertEquals(0.1, product.getDiscount());
	        assertEquals(0.3, product.getWeight());
	    }

	    @Test
	    public void testConstructor() {
	        Product product = new Product(1L, "Product Name", "Product Description", 10.0, 100L, "Product Category", 0.1, 0.3);

	        assertEquals(1L, product.getId());
	        assertEquals("Product Name", product.getName());
	        assertEquals("Product Description", product.getDescription());
	        assertEquals(10.0, product.getPrice());
	        assertEquals(100L, product.getStock());
	        assertEquals("Product Category", product.getCategory());
	        assertEquals(0.1, product.getDiscount());
	        assertEquals(0.3, product.getWeight());
	    }

	    @Test
	    public void testValidation() {
	        Product product = new Product();

	        product.setName(null);
	        product.setPrice(null);
	        product.setStock(null);
	        product.setCategory(null);

	        assertNull(product.getName());
	        assertNull(product.getPrice());
	        assertNull(product.getStock());
	        assertNull(product.getCategory());
	    }
	    
	    @Test
	    public void test_toString() {
	    	Product product = new Product(1L, "Product Name", "Product Description", 10.0, 100L, "Product Category", 0.1, 0.3);
	        String expected = "Product [id=1, name=Product Name, description=Product Description, price=10.0, stock=100, category=Product Category, discount=0.1, weight=0.3]";
	        assertEquals(expected, product.toString());
	    }

}