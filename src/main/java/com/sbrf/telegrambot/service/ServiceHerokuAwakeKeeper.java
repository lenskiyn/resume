package com.sbrf.telegrambot.service;

import com.sbrf.telegrambot.util.UtilMessageSender;
import com.sbrf.telegrambot.util.DS_ScheduleParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

@Component
public class ServiceHerokuAwakeKeeper {
    @Autowired
    DS_ScheduleParser ds_scheduleParser;
    @Autowired
    UtilMessageSender sender;

    @Scheduled(cron = "0 */25 * * * *", zone = "Europe/Moscow")
    public void keepAwake() throws TelegramApiException {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet get = new HttpGet("https://sbrf-telegram-work.herokuapp.com/");
            HttpResponse response = httpClient.execute(get);
        } catch (IOException e) {
            sender.send("188014382", "Произошла ошибка при пинге бота: " + e.getMessage());
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void preParseSchedule() throws TelegramApiException {
        try {
            ds_scheduleParser.parse();
        } catch (IOException e) {
            sender.send("188014382", "Произошла ошибка при старте бота: " + e.getMessage());
        }
    }
}
