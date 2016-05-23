package com.sainsburys.webscraper.service;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.unbescape.html.HtmlEscape;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sainsburys.webscraper.domain.Product;
import com.sainsburys.webscraper.domain.ProductDetails;
import com.sainsburys.webscraper.exception.WebScraperException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class WebScraperService {

	private static final BigDecimal KILOBYTE = new BigDecimal(1024);
	private final String url;
	private ConnectionManager connectionManager;
	
    public WebScraperService(final String url, ConnectionManager connectionManager) {
        this.url = url;
        this.connectionManager = connectionManager;
    }
    
    public String extractProductsToJSon() throws WebScraperException {
    	try {
    		Connection connection = connectionManager.connect(url);
        
			Elements elements = extractProductElements(connection);

			ArrayList<Product> productList = extractProductList(elements);

			ProductDetails products = setProductDetails(productList);

			return convertToJson(products);

		} catch (Exception e) {
			throw new WebScraperException(String.format("Unable to scrape from url: %s", url), e);
		}
    }

	private Elements extractProductElements(Connection connection) throws IOException {
		Document document = connection.get();
        Elements elements = document.select("div.product");
		return elements;
	}

	public ArrayList<Product> extractProductList(Elements elements) throws IOException {
		ArrayList<Product> productList = new ArrayList<Product>();
        
        for (Element element : elements) {
            Product product = extractProductFromElement(element);
            
            productList.add(product);
        }
		return productList;
	}

	public Product extractProductFromElement(Element element) throws IOException {
		Product product = new Product();
		
		Elements productLink = element.getElementsByTag("a");
		String pricePerUnit = extractPricePerUnit(element);
         
		product.setTitle(productLink.text());
		product.setUnitPrice(extractUnitPrice(pricePerUnit));
		
		setDetailsFromLink(productLink, product);
		return product;
	}

    private String convertToJson(ProductDetails products) throws JsonProcessingException {
    	return new ObjectMapper().writeValueAsString(products);
    }
    
	private ProductDetails setProductDetails(ArrayList<Product> productList) {
		ProductDetails products = new ProductDetails();
        products.setProducts(productList);
        products.setTotal(products.calculateTotalUnitPriceOfProducts());
		return products;
	}

	private String extractPricePerUnit(Element element) {
		return HtmlEscape.unescapeHtml(element.getElementsByClass("pricePerUnit").text());
	}

	private Product setDetailsFromLink(Elements productLink, Product product) throws IOException {
		String linkUrl = productLink.attr("href");
		Connection productDetailsConnection = connectionManager.connect(linkUrl);
		
		Document document = productDetailsConnection.get();
		
		product.setSize(extractSizeToString(productDetailsConnection));
		product.setDescription(document.select("productcontent div.productText").first().text());
		
		return product;
	}

	private String extractSizeToString(Connection connection) {
		return String.valueOf(calulateSizeInKilobytes(connection)) +"kb";
	}

	private BigDecimal calulateSizeInKilobytes(Connection connection) {
		return new BigDecimal(connection.response().bodyAsBytes().length).divide(KILOBYTE).setScale(2, RoundingMode.HALF_UP);
	}
    
    private BigDecimal extractUnitPrice(String price){
        int indexStart = price.indexOf("£");
        int indexEnd = price.indexOf("/unit");
        return new BigDecimal(price.substring(indexStart + 1, indexEnd));
    }
}
