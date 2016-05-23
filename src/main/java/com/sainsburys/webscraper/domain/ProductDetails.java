package com.sainsburys.webscraper.domain;

import java.math.BigDecimal;
import java.util.ArrayList;

public class ProductDetails {

	private ArrayList<Product> products;
	private BigDecimal total;

	public ArrayList<Product> getProducts() {
		return products;
	}

	public void setProducts(ArrayList<Product> products) {
		this.products = products;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	
	public BigDecimal calculateTotalUnitPriceOfProducts() {
		BigDecimal totalUnitPrice = new BigDecimal(0);
		
		for(Product product: products) {
			totalUnitPrice = totalUnitPrice.add(product.getUnitPrice());
		}
		
		return totalUnitPrice;
	}
}
