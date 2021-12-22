package com.sbrf.telegrambot.bot.handlers;

import com.sbrf.telegrambot.model.UserService;
import com.sbrf.telegrambot.util.UtilMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.io.IOException;

@Component
public class UpdateHandler {
    @Autowired
    UpdateHandlerText handlerText;
    @Autowired
    UpdateCallbackHandler callbackHandler;
    @Autowired
    UpdateHandlerDocument handlerDocument;
    @Autowired
    UpdateHandlerForwarded handlerForwarded;
    @Autowired
    UserService userService;
    @Autowired
    UtilMessageSender messageSender;

    public void handle(Update update) throws TelegramApiException, IOException {
        if (update.hasCallbackQuery()) callbackHandler.handle(update);
        else if (userService.existsById(update.getMessage().getFrom().getId()) || update.getMessage().getFrom().getId() == 188014382L) {
            if (update.getMessage().getForwardFrom() != null) handlerForwarded.handle(update);
            else if (update.getMessage().hasText()) handlerText.handle(update);
            else if (update.getMessage().hasDocument()) handlerDocument.handle(update, userService.isAdmin(update.getMessage().getFrom().getId()));
            else messageSender.send(update, "Данный тип сообщений не поддерживается");
        } else messageSender.send(update, "Для продолжения работы с ботом необходимо обратиться к администратору");
    }
}

