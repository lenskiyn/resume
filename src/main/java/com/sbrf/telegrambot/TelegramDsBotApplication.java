package com.sbrf.telegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TelegramDsBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(TelegramDsBotApplication.class, args);
    }
}
