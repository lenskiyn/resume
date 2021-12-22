package com.sbrf.telegrambot.bot.handlers;

import com.sbrf.telegrambot.model.User;
import com.sbrf.telegrambot.model.UserService;
import com.sbrf.telegrambot.util.DS_ScheduleParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

@Component
public class UpdateHandlerTextSettings {

    @Autowired
    UserService userService;
    @Autowired
    DS_ScheduleParser scheduleParser;

    public String handle(Update update) {
        String[] parsedText = update.getMessage().getText().split(":");
        String operation = parsedText.length > 1 ? parsedText[1].toLowerCase(Locale.ROOT) : "undefined";

        try {
            switch (operation) {
                case "добавить":
                    long id = Long.parseLong(parsedText[2]);
                    String name = parsedText[3];
                    return userService.save(new User(id, name)) ? "Пользователь успешно добавлен" : "Пользователь уже существует";
                case "добавить всех":
                    return addMultipleUsersToDB(update.getMessage().getText().substring(16));
                case "удалить":
                    id = Long.parseLong(parsedText[2]);
                    return userService.delete(id) ? "Пользователь успешно удален" : "Пользователь с таким id не существует в базе";
                case "удалить всех":
                    return userService.deleteAll() ? "Все пользователи успешно удалено" : "Что-то пошло не так";
                case "найти всех":
                    StringBuilder result = new StringBuilder();
                    String[] users = userService.getAll().toString().split("},");
                    for (String s : users) result.append(s).append("\n").append("\n");
                    result.append("Всего пользователей: ").append(users.length);
                    return result.toString();
                case "админ":
                    id = Long.parseLong(parsedText[2]);
                    boolean bool = parsedText[3].equals("да");
                    return userService.setAdmin(id, bool);
                case "имя":
                    id = Long.parseLong(parsedText[2]);
                    name = parsedText[3];
                    return userService.updateUserNameById(id, name);
                case "уведомления":
                    id = Long.parseLong(parsedText[2]);
                    bool = parsedText[3].equals("да");
                    return userService.setNotified(id, bool);
                case "смена":
                    return scheduleParser.getNextShift(userService.getUserIdByName((parsedText[2])));
                case "все смены":
                    return scheduleParser.getAllShiftsForUser((parsedText[2]));
                case "тест цвета":
                    name = parsedText[2];
                    return scheduleParser.getCellsColorNameFor(name);
            }
            scheduleParser.parse();
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception in <UpdateHandlerTextSettings>: " + e;
        }
        return String.format("Операция \"%s\"  не поддерживается", update.getMessage().getText());
    }

    private String addMultipleUsersToDB(String input) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String s : input.split("@")) {
            long id = Long.parseLong(s.split(":")[0]);
            String name = s.split(":")[1];
            if (!userService.save(new User(id, name))) {
                stringBuilder.append(String.format("Пользователь %s уже есть в бд %n", name));
            }
        }
        return stringBuilder.length() == 0 ? "Пользователи успешно добавлены" : stringBuilder.toString();
    }
}
