package com.example.Web.Scraper.CLI.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class ScrapedData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private String selector;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    private LocalDateTime timestamp;

    public ScrapedData(String url, String selector, String content) {
        this.url = url;
        this.selector = selector;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }
}
