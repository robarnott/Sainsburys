package com.sainsburys.webscraper.exception;

import java.io.IOException;

public class WebScraperException extends IOException {

	public WebScraperException() {
	}

	public WebScraperException(String message) {
		super(message);
	}

	public WebScraperException(String message, Exception e) {
		super(message, e);
	}
}
