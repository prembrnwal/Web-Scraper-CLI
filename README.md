# Universal Web Scraper CLI 🕸️

A robust, interactive command-line interface (CLI) application for web scraping and browser automation. Built with **Java** and **Spring Boot**, this tool leverages **Selenium WebDriver** running in headless mode to render dynamic, JavaScript-heavy websites (like SPAs) and seamlessly persists scraped structural data into a **PostgreSQL** database via **Spring Data JPA**.

---

## 🚀 Key Features

* **Interactive Command Line Shell:** Navigate the web, inspect code, click elements, and scrape data dynamically from your terminal.
* **Headless Browser Automation:** Uses WebDriverManager and Selenium Headless Chrome to fully render web pages, handle JavaScript execution, and interact with complex web elements seamlessly—something static scraping tools cannot do.
* **Robust Data Extraction:** Extracts text or outer HTML content using standard CSS Selectors and automatically saves the output.
* **Persistent Storage:** Integrates a PostgreSQL relational database through Spring Data JPA to provide a historical log (`history`) of all captured web data out of the box.
* **Navigation & Interaction:** Allows clicking on elements (`click on <selector>`) and intelligently waiting for AJAX navigation or route changes.

---

## 💻 Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot (CommandLineRunner)
* **Web Scraping & Automation:** Selenium WebDriver, WebDriverManager, Jsoup
* **Database & ORM:** PostgreSQL, Spring Data JPA / Hibernate
* **Build Tool:** Maven

---

## 🛠️ Installation & Setup

### Prerequisites
1. **Java 21** or higher.
2. **Maven** installed.
3. **PostgreSQL** database running locally or remotely.

### 1. Database Configuration
Open `src/main/resources/application.properties` and configure your PostgreSQL database credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 2. Build the Application
Navigate to the root directory containing `pom.xml` and build the project using Maven:

```bash
mvn clean install
```

### 3. Run the CLI
Start the application from the terminal:
```bash
mvn spring-boot:run
```
Alternatively, you can run the compiled `.jar` file:
```bash
java -jar target/web-scraper-cli-0.0.1-SNAPSHOT.jar
```

---

## 📖 Usage Guide

Once the CLI boots up, you'll be presented with the `scraper>` prompt. Here is a list of supported commands:

| Command | Description | Example |
|---|---|---|
| `navigate <url>` | Opens the specified URL in the headless browser. | `navigate google.com` |
| `show code` | Dumps the fully-rendered HTML source code of the current page. | `show code` |
| `click on <selector>` | Clicks a specific web element via its CSS selector, allowing navigation through UI events. | `click on button.submit-btn` |
| `capture <selector>` | Extracts data from elements matching the CSS selector and immediately saves them to PostgreSQL. | `capture h1.article-title` |
| `history` | Fetches and displays all previously scraped and safely stored data from the database. | `history` |
| `help` | Prints a list of available commands. | `help` |
| `exit` / `quit` | Safely closes the WebDriver and terminates the application. | `exit` |

### Example Workflow
```text
scraper> navigate example.com
Navigating to https://example.com...
Success! Page Title: Example Domain

scraper> click on a
Clicking on <a>...
New URL: https://www.iana.org/domains/example
New Title: IANA — Example domains

scraper> capture h1
Found 1 elements. Saving to database...
[1] Example Domains
Content saved to database successfully.

scraper> history
--- Scraped Data History ---
ID: 1
Date: 2026-03-21T00:30:00.000
URL: https://www.iana.org/domains/example
Selector: h1
Content Preview: Example Domains
---------------------------
```

---

## 💡 Why This Project Stands Out (For Recruiters/Resume)

* **Solves Real-world Scraping Issues:** Traditional HTTP clients (like cURL or basic Jsoup) fail to parse Modern Javascript Frameworks (React, Vue, Angular). By utilizing a headless browser engine, this tool naturally circumvents SPA rendering limitations.
* **Statefulness & Interactivity:** It is designed not as a single-run script, but as a long-lived interactive shell that holds the browser state in memory, allowing users to log in or traverse multi-step forms via `click on` before capturing data.
* **Enterprise Grade Architecture:** Integrates Spring Boot dependency injection and standard JPA data tier patterns to ensure the code remains scalable, maintainable, and easily extendable for REST APIs or UI integrations.

