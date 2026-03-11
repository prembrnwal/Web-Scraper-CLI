package com.example.Web.Scraper.CLI;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class WebScraperCliApplication implements CommandLineRunner {

    private String currentUrl = "";
    private Document currentDoc = null;

    public static void main(String[] args) {
        SpringApplication.run(WebScraperCliApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("========================================");
        System.out.println("   Welcome to Web Scraper CLI (Lite)");
        System.out.println("   Version: Jsoup-based (Low Disk Space)");
        System.out.println("   Type 'help' for available commands");
        System.out.println("========================================");

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.print("\nscraper> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;

            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                running = false;
                System.out.println("Exiting Scraper...");
            } else if (input.equalsIgnoreCase("help")) {
                printHelp();
            } else if (input.toLowerCase().startsWith("navigate ")) {
                handleNavigate(input.substring(9).trim());
            } else if (input.equalsIgnoreCase("show code")) {
                handleShowCode();
            } else if (input.toLowerCase().startsWith("capture ")) {
                handleCapture(input.substring(8).trim());
            } else if (input.toLowerCase().contains("rate of ")) {
                String crop = input.substring(input.toLowerCase().indexOf("rate of ") + 8).trim();
                handleRateQuery(crop);
            } else if (input.equalsIgnoreCase("screenshot") || input.toLowerCase().startsWith("click on ")) {
                System.out.println("Notice: This command requires Playwright (Headless Browser).");
                System.out.println("Please free up ~1.5GB on your C: drive to enable these features.");
            } else {
                System.out.println("Unknown command. Type 'help' for examples.");
            }
        }
    }

    private void printHelp() {
        System.out.println("\nAvailable Commands (Lite Version):");
        System.out.println("  navigate <url>         - Open a new URL");
        System.out.println("  rate of <crop>         - Get crop prices (e.g., 'rate of wheat')");
        System.out.println("  show code              - Display HTML of the current page");
        System.out.println("  capture <selector>     - Extract data matching CSS selector");
        System.out.println("  help                   - Show this help message");
        System.out.println("  exit/quit              - Close the application");
    }

    private void handleNavigate(String url) {
        if (!url.startsWith("http")) url = "https://" + url;
        try {
            System.out.println("Connecting to " + url + "...");
            currentDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .timeout(15000)
                    .get();
            currentUrl = url;
            System.out.println("Success! Page Title: " + currentDoc.title());
        } catch (Exception e) {
            System.out.println("Navigation failed: " + e.getMessage());
        }
    }

    private void handleShowCode() {
        if (currentDoc == null) {
            System.out.println("Please navigate to a URL first.");
            return;
        }
        System.out.println("\n--- Current Page Source ---");
        System.out.println(currentDoc.outerHtml());
        System.out.println("---------------------------");
    }

    private void handleCapture(String selector) {
        if (currentDoc == null) {
            System.out.println("Please navigate to a URL first.");
            return;
        }
        try {
            Elements elements = currentDoc.select(selector);
            if (elements.isEmpty()) {
                System.out.println("No elements found matching: " + selector);
            } else {
                System.out.println("Found " + elements.size() + " elements:");
                for (int i = 0; i < elements.size(); i++) {
                    Element el = elements.get(i);
                    System.out.println("[" + (i + 1) + "] <" + el.tagName() + ">: " + el.text());
                    if (el.tagName().equals("a")) System.out.println("    Link: " + el.absUrl("href"));
                    if (el.tagName().equals("img")) System.out.println("    Image: " + el.absUrl("src"));
                }
            }
        } catch (Exception e) {
            System.out.println("Capture failed: " + e.getMessage());
        }
    }

    private void handleRateQuery(String cropName) {
        if (currentDoc == null) {
            System.out.println("Please navigate to https://www.kisaanhelpline.com/mandi-bhav first.");
            return;
        }
        try {
            System.out.println("Searching for the rate of: " + cropName + "...");
            // Use Jsoup to find blocks containing the crop name (case-insensitive)
            Elements cards = currentDoc.select("div");
            int displayed = 0;
            
            System.out.println("\n--- Latest Rates for " + cropName + " ---");
            for (Element card : cards) {
                String fullText = card.text().toLowerCase();
                if (fullText.contains(cropName.toLowerCase()) && fullText.contains("max price") && displayed < 5) {
                    // This heuristic looks for specific classes used on KisaanHelpline
                    Element titleEl = card.select("h4, h5").first();
                    Element priceEl = card.select("button.mandi_result_card_btn").first();
                    
                    if (priceEl != null) {
                        String title = titleEl != null ? titleEl.text() : cropName;
                        String price = priceEl.text().replace("Max Price :", "").trim();
                        
                        System.out.println("[" + (displayed + 1) + "] " + title);
                        System.out.println("    Rate: " + price);
                        System.out.println("-------------------------------");
                        displayed++;
                        
                        // Move to next card to avoid duplicates
                        card.addClass("processed"); 
                    }
                }
            }
            if (displayed == 0) {
                System.out.println("Could not find specific price cards for '" + cropName + "' on the current page.");
                System.out.println("Try capturing 'table' or 'div' to see the structure.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
