package com.sainsburys.webscraper.service;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class ConnectionManager {

	public ConnectionManager() {
		
	}
	
	public Connection connect(String url) {
		return Jsoup.connect(url);
	}
}
