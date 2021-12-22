package com.sbrf.telegrambot.util;

import com.sbrf.telegrambot.model.UserService;
import com.sbrf.telegrambot.service.ServiceDateSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class UtilShiftNotifier {
    @Autowired
    UserService userService;
    @Autowired
    DS_ScheduleParser ds_scheduleParser;
    @Autowired
    UtilMessageSender messageSender;

    @Scheduled(cron = "0 30 12 * * *", zone = "Europe/Moscow")
    public void notifyUser() throws TelegramApiException {

        try {
            for (String dayShift : ds_scheduleParser.getShiftsForSpecificDayWithType(ServiceDateSupplier.getTomorrowDay(), "в день")) {
                String userId = userService.getUserIdByName(dayShift.split(":")[0]);
                if (userService.isNotified(Long.parseLong(userId))) {
                    messageSender.send(userId, String.format("Напоминание: Следующая смена завтра в день %d-го", ServiceDateSupplier.getTomorrowDay()));
                }
            }
            for (String nightShift : ds_scheduleParser.getShiftsForSpecificDayWithType(ServiceDateSupplier.getCurrentDay(), "в ночь")) {
                String userId = userService.getUserIdByName(nightShift.split(":")[0]);
                if (userService.isNotified(Long.parseLong(userId))) {
                    messageSender.send(userId, String.format("Напоминание: Следующая смена сегодня в ночь %d-го", ServiceDateSupplier.getCurrentDay()));
                }
            }
        } catch (Exception e) {
            messageSender.send(userService.getUserIdByName("Ленский Н.С."),
                    "Exception in <notifyUser>: " + e);
        }
    }
}
