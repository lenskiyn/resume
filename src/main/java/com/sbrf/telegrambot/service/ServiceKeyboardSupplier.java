package com.sbrf.telegrambot.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class ServiceKeyboardSupplier {
    public static ReplyKeyboardMarkup mainMenu() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("Мой график на месяц");
        row.add("Следующая смена");
        keyboard.add(row);
        row = new KeyboardRow();
        row.add("Кто работает сегодня ?");
        row.add("Завтра ?");
        row.add("Вчера ?");
        keyboard.add(row);
        row = new KeyboardRow();
        //row.add("Администраторы");
        row.add("Excel-файл с графиком");
        keyboard.add(row);
        row = new KeyboardRow();
        row.add("\ud83d\udd04"); //refresh
        row.add("\u2753"); //help
        row.add("\u2699"); //settings
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup settings() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton buttonOn = new InlineKeyboardButton();
        buttonOn.setText("Включить");
        buttonOn.setCallbackData("notificationsOn");
        row.add(buttonOn);
        InlineKeyboardButton buttonOff = new InlineKeyboardButton();
        buttonOff.setText("Выключить");
        buttonOff.setCallbackData("notificationsOff");
        row.add(buttonOff);
        rowList.add(row);

        keyboardMarkup.setKeyboard(rowList);

        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup systemList() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton buttonPPRB = new InlineKeyboardButton();
        buttonPPRB.setText("ППРБ.Карты");
        buttonPPRB.setCallbackData("systemKarts");
        row.add(buttonPPRB);

        rowList.add(row);

        keyboardMarkup.setKeyboard(rowList);
        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup optionsForCardsList() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton buttonWho = new InlineKeyboardButton();
        buttonWho.setText("Кто дежурный ?");
        buttonWho.setCallbackData("optionsForKartsWho");
        row.add(buttonWho);

        InlineKeyboardButton buttonGetSchedule = new InlineKeyboardButton();
        buttonGetSchedule.setText("График");
        buttonGetSchedule.setCallbackData("optionsForCardsGetSchedule");
        row.add(buttonGetSchedule);

        rowList.add(row);

        keyboardMarkup.setKeyboard(rowList);
        return keyboardMarkup;
    }
}