package com.sainsburys.webscraper;

import com.sainsburys.webscraper.exception.WebScraperException;
import com.sainsburys.webscraper.service.ConnectionManager;
import com.sainsburys.webscraper.service.WebScraperService;

public class WebScraper {
	
	public static void main(String[] args) throws WebScraperException {
		WebScraperService webscraper = new WebScraperService(
				"http://hiring-tests.s3-website-eu-west-1.amazonaws.com/2015_Developer_Scrape/5_products.html", new ConnectionManager());

		String extractedProducts = webscraper.extractProductsToJSon();
		System.out.println(extractedProducts);
	}
}
