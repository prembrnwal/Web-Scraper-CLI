package com.example.Web.Scraper.CLI;

import com.microsoft.playwright.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Paths;
import java.util.Scanner;

@SpringBootApplication
public class WebScraperCliApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(WebScraperCliApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("========================================");
        System.out.println("   Welcome to Web Scraper CLI v2.0");
        System.out.println("   Type 'help' for available commands");
        System.out.println("========================================");

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
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
                    String url = input.substring(9).trim();
                    handleNavigate(page, url);
                } else if (input.equalsIgnoreCase("show code")) {
                    handleShowCode(page);
                } else if (input.toLowerCase().startsWith("capture ")) {
                    String selector = input.substring(8).trim();
                    handleCapture(page, selector);
                } else if (input.toLowerCase().startsWith("click on ")) {
                    String selector = input.substring(9).trim();
                    handleClick(page, selector);
                } else if (input.equalsIgnoreCase("screenshot")) {
                    handleScreenshot(page);
                } else {
                    System.out.println("Unknown command. Type 'help' for examples.");
                }
            }
            browser.close();
        } catch (Exception e) {
            System.err.println("Critical Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void printHelp() {
        System.out.println("\nAvailable Commands:");
        System.out.println("  navigate <url>       - Open a new URL");
        System.out.println("  show code            - Display HTML of the current page");
        System.out.println("  capture <selector>   - Extract HTML of elements matching selector");
        System.out.println("  click on <selector>  - Click an element matching selector");
        System.out.println("  screenshot           - Save a screenshot of the current view");
        System.out.println("  help                 - Show this help message");
        System.out.println("  exit/quit            - Close the application");
    }

    private void handleNavigate(Page page, String url) {
        if (!url.startsWith("http")) {
            url = "https://" + url;
        }
        try {
            System.out.println("Navigating to: " + url + "...");
            page.navigate(url);
            System.out.println("Success! Current Title: " + page.title());
        } catch (Exception e) {
            System.out.println("Navigation failed: " + e.getMessage());
        }
    }

    private void handleShowCode(Page page) {
        try {
            System.out.println("\n--- Current Page HTML Source ---");
            System.out.println(page.content());
            System.out.println("-------------------------------");
        } catch (Exception e) {
            System.out.println("Error showing code: " + e.getMessage());
        }
    }

    private void handleCapture(Page page, String selector) {
        try {
            System.out.println("Capturing elements for: " + selector);
            var elements = page.locator(selector);
            int count = elements.count();
            if (count == 0) {
                System.out.println("No elements found matching '" + selector + "'");
            } else {
                System.out.println("Found " + count + " elements:");
                for (int i = 0; i < count; i++) {
                    System.out.println("[" + (i + 1) + "] " + elements.nth(i).innerHTML());
                }
            }
        } catch (Exception e) {
            System.out.println("Capture failed: " + e.getMessage());
        }
    }

    private void handleClick(Page page, String selector) {
        try {
            System.out.println("Attempting to click: " + selector);
            page.click(selector);
            System.out.println("Click successful. Current Title: " + page.title());
        } catch (Exception e) {
            System.out.println("Click failed: " + e.getMessage());
        }
    }

    private void handleScreenshot(Page page) {
        try {
            String filename = "screenshot_" + System.currentTimeMillis() + ".png";
            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(filename)));
            System.out.println("Screenshot saved as: " + filename);
        } catch (Exception e) {
            System.out.println("Screenshot failed: " + e.getMessage());
        }
    }
}
