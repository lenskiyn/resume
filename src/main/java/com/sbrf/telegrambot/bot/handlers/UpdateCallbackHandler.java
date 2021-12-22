package com.sbrf.telegrambot.bot.handlers;

import com.sbrf.telegrambot.model.UserService;
import com.sbrf.telegrambot.util.UtilMessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class UpdateCallbackHandler {
    @Autowired
    UtilMessageSender messageSender;
    @Autowired
    UserService userService;

    public void handle(Update update) throws TelegramApiException {
        String callbackText = update.getCallbackQuery().getData();
        String chatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());

        Update nUpdate = new Update();
        Message message = new Message();
        Chat chat = new Chat();

        chat.setId(Long.parseLong(chatId));
        message.setChat(chat);
        nUpdate.setMessage(message);

        if (callbackText.contains("notifications")) {
            switch (update.getCallbackQuery().getData()) {
                case "notificationsOn":
                    messageSender.send(chatId, userService.setNotified(Long.parseLong(chatId), true));
                    break;
                case "notificationsOff":
                    messageSender.send(chatId, userService.setNotified(Long.parseLong(chatId), false));
                    break;
            }
        }

    }
}
