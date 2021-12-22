package com.sbrf.telegrambot.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class UtilMessageSender {
    @Autowired
    TelegramLongPollingBot bot;

    public void send(Update update, String text) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText(text);
        bot.execute(sendMessage);
    }

    public void send(Update update, String text, ReplyKeyboardMarkup keyboardMarkup) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(keyboardMarkup);
        bot.execute(sendMessage);
    }

    public void send(Update update, String text, InlineKeyboardMarkup keyboardMarkup) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(keyboardMarkup);
        bot.execute(sendMessage);
    }

    public void send(String chatId, String text) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        bot.execute(sendMessage);
    }

    public void send(SendDocument sendDocument) throws TelegramApiException {
        bot.execute(sendDocument);
    }

    public void send(Update update, String text, String number) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        String compiled = "[+79998593911](tel:+79998593911)";
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText(compiled);
        sendMessage.setParseMode("Markdown");
        bot.execute(sendMessage);
    }
}
