package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KeepAliveService {

    private static final Logger logger = LoggerFactory.getLogger(KeepAliveService.class);
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${APP_URL:}")
    private String appUrl;

    // Runs every 10 minutes (600,000 milliseconds)
    @Scheduled(fixedRate = 600000)
    public void keepAlive() {
        if (appUrl == null || appUrl.isEmpty()) {
            logger.warn("⚠️ APP_URL environment variable is not set. Self-ping skipped.");
            return;
        }

        try {
            String url = appUrl.endsWith("/") ? appUrl + "api/health" : appUrl + "/api/health";
            logger.info("Sending keep-alive ping to: {}", url);
            String response = restTemplate.getForObject(url, String.class);
            logger.info("Keep-alive ping successful: {}", response);
        } catch (Exception e) {
            logger.error("❌ Keep-alive ping failed: {}", e.getMessage());
        }
    }
}
