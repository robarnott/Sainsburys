package com.sainsburys.webscraper.service;
//import static org.hamcrest.CoreMatchers.equalTo;
//import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import com.sainsburys.webscraper.domain.Product;
import com.sainsburys.webscraper.exception.WebScraperException;

public class WebScraperServiceTest {
    
	private static final String APPLE_DESCRIPTION = "Apple";
	private static final String APRICOTS_DESCRIPTION = "Apricots";
	private static final String PRODUCT_APRICOTS = "Product - Apricots";
	private static final String PRODUCT_APPLES = "Product - Apples";
	
	private static final String PRODUCT_URL_A = "productUrlA";
	private static final String PRODUCT_URL_B = "productUrlB";
	
	private WebScraperService webScraper;
	private Element elementA = mock(Element.class);
	private Element elementB = mock(Element.class);
	
	private Elements productLinkA = mock(Elements.class);
	private Elements productLinkB = mock(Elements.class);
	
	private Elements pricePerUnitElementA = mock(Elements.class);
	private Elements pricePerUnitElementB = mock(Elements.class);
	
	private Connection productDetailsConnectionA = mock(Connection.class);
	private Connection productDetailsConnectionB = mock(Connection.class);
	
	private Document documentA = mock(Document.class);
	private Document documentB = mock(Document.class);
	
	private Response responseA = mock(Response.class);
	private Response responseB = mock(Response.class);
	
	private ConnectionManager connectionManager = mock(ConnectionManager.class);
	
	private Elements descriptionElementsA = mock(Elements.class);
	private Elements descriptionElementsB = mock(Elements.class);
	
	private Element descriptionElementA = mock(Element.class);
	private Element descriptionElementB = mock(Element.class);
	
	private Elements productElements = new Elements();
	byte[] bytes = "bytes".getBytes();
	
	@Before
	public void setup() throws IOException {
		webScraper = new WebScraperService("http://hiring-tests.s3-website-eu-west-1.amazonaws.com/2015_Developer_Scrape/5_products.html", connectionManager);
		
		mockProductLinkA(PRODUCT_APRICOTS);
		mockPricePerUnitA("£10/unit");
		mockDescriptionA(APRICOTS_DESCRIPTION);
		mockProductDetailsA();

		mockProductLinkB(PRODUCT_APPLES);
		mockPricePerUnitB("£10/unit");
		mockDescriptionB(APPLE_DESCRIPTION);
		mockProductDetailsB();
		
		when(responseA.bodyAsBytes()).thenReturn(bytes);
		when(responseB.bodyAsBytes()).thenReturn(bytes);
	}

	@Test
	public void extractsAProductFromAnElement() throws IOException {
		webScraper = new WebScraperService("testUrl", connectionManager);
		Product extractedProduct = webScraper.extractProductFromElement(elementA);
		assertThat(extractedProduct.getTitle(), equalTo(PRODUCT_APRICOTS));
		assertThat(extractedProduct.getUnitPrice(), equalTo(new BigDecimal(10)));
		assertThat(extractedProduct.getDescription(), equalTo(APRICOTS_DESCRIPTION));
	}
	
	@Test
	public void extractsAListOfProductsFromElements() throws IOException {
		webScraper = new WebScraperService("testUrl", connectionManager);
		productElements.add(elementA);
		productElements.add(elementB);
		ArrayList<Product> extractedProductList = webScraper.extractProductList(productElements);
		assertThat(extractedProductList.size(), equalTo(2));
		
		assertThat( extractedProductList, contains(
			    hasProperty("title", is(PRODUCT_APRICOTS)),
			    hasProperty("title", is(PRODUCT_APPLES))
			));
	}

	@Test(expected=WebScraperException.class)
	public void throwsWebScraperExceptionWhenScrapingFails() throws WebScraperException {
		webScraper = new WebScraperService("malformedUrl", connectionManager);
		webScraper.extractProductsToJSon();
	}
	
	
//	----------------- Mocking methods
	
	private void mockProductDetailsA() throws IOException {
		when(productDetailsConnectionA.get()).thenReturn(documentA);
		when(productDetailsConnectionA.response()).thenReturn(responseA);
		when(connectionManager.connect(PRODUCT_URL_A)).thenReturn(productDetailsConnectionA);
	}

	private void mockProductDetailsB() throws IOException {
		when(productDetailsConnectionB.get()).thenReturn(documentB);
		when(productDetailsConnectionB.response()).thenReturn(responseB);
		when(connectionManager.connect(PRODUCT_URL_B)).thenReturn(productDetailsConnectionB);
	}
	
	private void mockDescriptionA(String description) {
		when(descriptionElementA.text()).thenReturn(description);
		when(descriptionElementsA.first()).thenReturn(descriptionElementA);
		when(documentA.select(anyString())).thenReturn(descriptionElementsA);
	}
	
	private void mockDescriptionB(String description) {
		when(descriptionElementB.text()).thenReturn(description);
		when(descriptionElementsB.first()).thenReturn(descriptionElementB);
		when(documentB.select(anyString())).thenReturn(descriptionElementsB);
	}

	private void mockPricePerUnitA(String pricePerUnit) {
		when(elementA.getElementsByClass("pricePerUnit")).thenReturn(pricePerUnitElementA);
		when(pricePerUnitElementA.text()).thenReturn(pricePerUnit);
	}

	private void mockPricePerUnitB(String pricePerUnit) {
		when(elementB.getElementsByClass("pricePerUnit")).thenReturn(pricePerUnitElementB);
		when(pricePerUnitElementB.text()).thenReturn(pricePerUnit);
	}
	
	private void mockProductLinkA(String title) {
		when(elementA.getElementsByTag(anyString())).thenReturn(productLinkA);
		when(productLinkA.text()).thenReturn(title);
		when(productLinkA.attr("href")).thenReturn(PRODUCT_URL_A);
	}
	
	private void mockProductLinkB(String title) {
		when(elementB.getElementsByTag(anyString())).thenReturn(productLinkB);
		when(productLinkB.text()).thenReturn(title);
		when(productLinkB.attr("href")).thenReturn(PRODUCT_URL_B);
	}
	
}