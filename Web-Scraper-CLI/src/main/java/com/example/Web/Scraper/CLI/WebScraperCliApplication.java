package com.example.Web.Scraper.CLI;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class WebScraperCliApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(WebScraperCliApplication.class, args);
	}

	@Override
	public void run(String... args) {
		if (args.length == 0) {
			System.out.println("Usage: java -jar <jar-file> <url> [css-selector]");
			System.out.println("Example: java -jar app.jar https://example.com \"h1\"");
			System.out.println("\nNote: you can also pass args using Maven:");
			System.out.println("mvn spring-boot:run -Dspring-boot.run.arguments=\"https://example.com h1\"");
			return;
		}

		String url = args[0];
		String selector = args.length > 1 ? args[1] : null;

		try {
			System.out.println("Connecting to " + url + "...");
			Document doc = Jsoup.connect(url)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
					.timeout(10000)
					.get();

			System.out.println("\n--- Scraper Results ---");
			System.out.println("Page Title: " + doc.title());

			if (selector != null) {
				System.out.println("\nQuerying for selector: '" + selector + "'");
				Elements elements = doc.select(selector);
				if (elements.isEmpty()) {
					System.out.println("No elements found matching the selector.");
				} else {
					System.out.println("Found " + elements.size() + " elements:");
					for (int i = 0; i < elements.size(); i++) {
						Element el = elements.get(i);
						System.out.printf("[%d] %s%n", i + 1, el.text());
						if (el.tagName().equalsIgnoreCase("a")) {
							System.out.printf("    Link: %s%n", el.attr("abs:href"));
						} else if (el.tagName().equalsIgnoreCase("img")) {
							System.out.printf("    Image Src: %s%n", el.attr("abs:src"));
						}
					}
				}
			} else {
				System.out.println("\nNo CSS selector provided.");
				System.out.println("Tip: Provide a CSS selector as the second argument to extract specific elements.");
			}
			System.out.println("-----------------------");

		} catch (IllegalArgumentException e) {
			System.err.println("Error: Invalid URL. Make sure it starts with http:// or https://");
		} catch (IOException e) {
			System.err.println("Error fetching the URL: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("An unexpected error occurred: " + e.getMessage());
		}
	}
}
