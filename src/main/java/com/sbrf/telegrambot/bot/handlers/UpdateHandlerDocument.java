package com.sbrf.telegrambot.bot.handlers;

import com.sbrf.telegrambot.util.UtilMessageSender;
import com.sbrf.telegrambot.util.UtilUpdateScheduleFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Component
public class UpdateHandlerDocument {
    @Autowired
    UtilMessageSender messageSender;
    @Autowired
    UtilUpdateScheduleFile updateScheduleFile;

    public void handle(Update update, boolean isAdmin) throws TelegramApiException {
        if (isAdmin) {
            if (update.getMessage().getDocument().getFileName().split("\\.")[1].equals("xlsx")) {
                updateScheduleFile.update(update);
                messageSender.send(update, "График успешно обновлен");
            } else messageSender.send(update, "Ошибка: файл должен иметь расширение .xlsx");
        } else messageSender.send(update, "Ошибка: недостаточно прав для обновления графика");
    }
}
