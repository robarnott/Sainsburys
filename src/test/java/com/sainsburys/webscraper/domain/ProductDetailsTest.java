package com.sainsburys.webscraper.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.Test;

public class ProductDetailsTest {

	@Test
	public void calculatesTotalPricePerUnitOfProducts() {
		ProductDetails productDetails = new ProductDetails();
		
		ArrayList<Product> products = new ArrayList<Product>();
		Product ProductA = new Product();
		Product ProductB = new Product();
		ProductA.setUnitPrice(new BigDecimal(10));
		ProductB.setUnitPrice(new BigDecimal(10));
		
		products.add(ProductA);
		products.add(ProductB);
		
		productDetails.setProducts(products);
		
		BigDecimal totalUnitPriceOfProducts = productDetails.calculateTotalUnitPriceOfProducts();
		
		assertThat(totalUnitPriceOfProducts, equalTo(new BigDecimal(20)));
	}

}
