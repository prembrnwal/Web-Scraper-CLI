package com.example.Web.Scraper.CLI.repository;

import com.example.Web.Scraper.CLI.model.ScrapedData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapedDataRepository extends JpaRepository<ScrapedData, Long> {
}
