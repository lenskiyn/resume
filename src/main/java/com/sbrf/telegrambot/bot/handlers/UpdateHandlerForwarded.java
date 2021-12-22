package com.sbrf.telegrambot.bot.handlers;

import com.sbrf.telegrambot.model.UserService;
import com.sbrf.telegrambot.util.UtilMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class UpdateHandlerForwarded {
    @Autowired
    UserService userService;
    @Autowired
    UtilMessageSender messageSender;

    public void handle(Update update) throws TelegramApiException {
        if (userService.isAdmin(update.getMessage().getFrom().getId())) {
            messageSender.send(update, update.getMessage().getForwardFrom().getId().toString() + " " +
                    update.getMessage().getForwardFrom().getFirstName() + " " +
                    update.getMessage().getForwardFrom().getLastName());
        }
    }
}
