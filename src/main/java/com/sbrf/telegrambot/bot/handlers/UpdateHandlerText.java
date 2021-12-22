package com.sbrf.telegrambot.bot.handlers;

import com.sbrf.telegrambot.config.Config;
import com.sbrf.telegrambot.model.UserService;
import com.sbrf.telegrambot.service.ServiceDateSupplier;
import com.sbrf.telegrambot.service.ServiceKeyboardSupplier;
import com.sbrf.telegrambot.service.ServiceRepliesSupplier;
import com.sbrf.telegrambot.util.UtilGetScheduleFile;
import com.sbrf.telegrambot.util.UtilMessageSender;
import com.sbrf.telegrambot.util.DS_ScheduleParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class UpdateHandlerText {
    @Autowired
    UtilMessageSender messageSender;
    @Autowired
    UpdateHandlerTextSettings handlerUserSettings;
    @Autowired
    DS_ScheduleParser scheduleParser;
    @Autowired
    UtilGetScheduleFile getScheduleFile;
    @Autowired
    UserService userService;

    public void handle(Update update) throws TelegramApiException {

        if (update.getMessage().getText().startsWith("н")) messageSender.send(update, handlerUserSettings.handle(update));
        else {
            switch (update.getMessage().getText()) {
                case "/start":
                    messageSender.send(update, ServiceRepliesSupplier.getReply("/start"));
                case "\u2753": //help
                    if (userService.isAdmin(update.getMessage().getFrom().getId())) {
                        messageSender.send(update, ServiceRepliesSupplier.getReply("/help admin"), ServiceKeyboardSupplier.mainMenu());
                    } else
                        messageSender.send(update, ServiceRepliesSupplier.getReply("/help"), ServiceKeyboardSupplier.mainMenu());
                    break;
                case "Excel-файл с графиком":
                    getScheduleFile.get(update, Config.SCHEDULE_NAME);
                    return;
//                case "Администраторы":
//                    messageSender.send(update, "Выберете систему", ServiceKeyboardSupplier.systemList());
//                    break;
                case "Следующая смена":
                    messageSender.send(update, scheduleParser.getNextShift(update.getMessage().getFrom().getId().toString()));
                    break;
                case "Мой график на месяц":
                    messageSender.send(update, scheduleParser.getAllShiftsForUser(userService.getUserNameById(update.getMessage().getFrom().getId())));
                    break;
                case "Кто работает сегодня ?":
                    messageSender.send(update, scheduleParser.getShiftsForSpecificDay(ServiceDateSupplier.getCurrentDay()));
                    break;
                case "Завтра ?":
                    messageSender.send(update, scheduleParser.getShiftsForSpecificDay(ServiceDateSupplier.getTomorrowDay()));
                    break;
                case "Вчера ?":
                    messageSender.send(update, scheduleParser.getShiftsForSpecificDay(ServiceDateSupplier.getYesterday()));
                    break;
                case "\ud83d\udd04": //refresh
                    messageSender.send(update, "Клавиатура успешно обновлена", ServiceKeyboardSupplier.mainMenu());
                    break;
                case "\u2699": //settings
                    messageSender.send(update, "Настройка функции напоминания о предстоящей смене", ServiceKeyboardSupplier.settings());
                    break;
                default:
                    messageSender.send(update, String.format("\"%s\" - данная команда не поддерживается или устарела\n\n" +
                                    "Попробуйте обновить список доступных команд с помощью кнопки - %s"
                            , update.getMessage().getText(), "\ud83d\udd04"));
                    break;
            }
        }
    }
}
