package com.example.Web.Scraper.CLI;

import com.example.Web.Scraper.CLI.model.ScrapedData;
import com.example.Web.Scraper.CLI.repository.ScrapedDataRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class WebScraperCliApplication implements CommandLineRunner {

    @Autowired
    private ScrapedDataRepository repository;

    private WebDriver driver;
    private String currentUrl = "";

    public static void main(String[] args) {
        SpringApplication.run(WebScraperCliApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("========================================");
        System.out.println("   Universal Web Scraper CLI v2.0");
        System.out.println("   Engine: Selenium (Headless)");
        System.out.println("   Storage: H2 Database");
        System.out.println("   Type 'help' for available commands");
        System.out.println("========================================");

        setupDriver();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.print("\nscraper> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;

            try {
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
                } else if (input.toLowerCase().startsWith("click on ")) {
                    handleClickOn(input.substring(9).trim());
                } else if (input.equalsIgnoreCase("history")) {
                    handleShowHistory();
                } else {
                    System.out.println("Unknown command. Type 'help' for examples.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        if (driver != null) {
            driver.quit();
        }
    }

    private void setupDriver() {
        System.out.println("Initializing Browser Engine...");
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    private void printHelp() {
        System.out.println("\nAvailable Commands:");
        System.out.println("  navigate <url>         - Open a new URL (Headless Chrome)");
        System.out.println("  show code              - Display HTML source of the current page");
        System.out.println("  capture <selector>     - Extract data and SAVE to database");
        System.out.println("  click on <selector>    - Trigger a click interaction");
        System.out.println("  history                - Show all saved data from database");
        System.out.println("  help                   - Show this help message");
        System.out.println("  exit/quit              - Close the application");
    }

    private void handleNavigate(String url) {
        if (!url.startsWith("http")) url = "https://" + url;
        try {
            System.out.println("Navigating to " + url + "...");
            driver.get(url);
            currentUrl = url;
            System.out.println("Success! Page Title: " + driver.getTitle());
        } catch (Exception e) {
            System.out.println("Navigation failed: " + e.getMessage());
        }
    }

    private void handleShowCode() {
        if (currentUrl.isEmpty()) {
            System.out.println("Please navigate to a URL first.");
            return;
        }
        System.out.println("\n--- Current Page Source ---");
        System.out.println(driver.getPageSource());
        System.out.println("---------------------------");
    }

    private void handleCapture(String selector) {
        if (currentUrl.isEmpty()) {
            System.out.println("Please navigate to a URL first.");
            return;
        }
        try {
            List<WebElement> elements = driver.findElements(By.cssSelector(selector));
            if (elements.isEmpty()) {
                System.out.println("No elements found matching: " + selector);
            } else {
                System.out.println("Found " + elements.size() + " elements. Saving to database...");
                StringBuilder content = new StringBuilder();
                for (int i = 0; i < elements.size(); i++) {
                    WebElement el = elements.get(i);
                    String text = el.getText();
                    if (text == null || text.isBlank()) text = el.getAttribute("outerHTML");
                    
                    System.out.println("[" + (i + 1) + "] " + text);
                    content.append(text).append("\n---\n");
                }
                
                // Save to Database
                ScrapedData data = new ScrapedData(currentUrl, selector, content.toString());
                repository.save(data);
                System.out.println("Content saved to database successfully.");
            }
        } catch (Exception e) {
            System.out.println("Capture failed: " + e.getMessage());
        }
    }

    private void handleClickOn(String selector) {
        if (currentUrl.isEmpty()) {
            System.out.println("Please navigate to a URL first.");
            return;
        }
        try {
            WebElement element = driver.findElement(By.cssSelector(selector));
            System.out.println("Clicking on <" + element.getTagName() + ">...");
            element.click();
            // Wait a bit for potential navigation/AJAX
            Thread.sleep(2000);
            currentUrl = driver.getCurrentUrl();
            System.out.println("New URL: " + currentUrl);
            System.out.println("New Title: " + driver.getTitle());
        } catch (Exception e) {
            System.out.println("Action failed: " + e.getMessage());
        }
    }

    private void handleShowHistory() {
        List<ScrapedData> history = repository.findAll();
        if (history.isEmpty()) {
            System.out.println("No saved data found in database.");
            return;
        }
        System.out.println("\n--- Scraped Data History ---");
        for (ScrapedData data : history) {
            System.out.println("ID: " + data.getId());
            System.out.println("Date: " + data.getTimestamp());
            System.out.println("URL: " + data.getUrl());
            System.out.println("Selector: " + data.getSelector());
            System.out.println("Content Preview: " + (data.getContent().length() > 50 ? data.getContent().substring(0, 50) + "..." : data.getContent()));
            System.out.println("---------------------------");
        }
    }
}
