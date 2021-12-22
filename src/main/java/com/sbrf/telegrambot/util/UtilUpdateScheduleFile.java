package com.sbrf.telegrambot.util;

import com.google.cloud.storage.*;

import com.sbrf.telegrambot.config.Config;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class UtilUpdateScheduleFile {
    @Autowired
    TelegramLongPollingBot bot;
    @Autowired
    DS_ScheduleParser ds_scheduleParser;
    @Autowired
    UtilMessageSender messageSender;

    public void update(Update update) throws TelegramApiException {
        save(update);
        upload(update);
    }

    private void save(Update update) throws TelegramApiException {
        String fileName = update.getMessage().getDocument().getFileName();
        try {
            File file = bot.execute(new GetFile(update.getMessage().getDocument().getFileId()));
            URL url = new URL(String.format("%s", file.getFileUrl(bot.getBotToken())));
            switch (fileName) {
                case Config.SCHEDULE_NAME:
                    FileUtils.copyURLToFile(url, new java.io.File(Config.SCHEDULE_NAME));
                    ds_scheduleParser.parse();
                    break;
//                case Config.PPRB_SCHEDULE_NAME:
//                    FileUtils.copyURLToFile(url, new java.io.File(Config.PPRB_SCHEDULE_NAME));
//                    pprb_scheduleParser.parse();
//                    break;
                default:
                    messageSender.send(update, "Ошибка при сохранении файла на хост: Некорректное имя файла");
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageSender.send(update, "Exception in <UtilSaveFileToHost>: " + e);
        }
    }

    private void upload(Update update) throws TelegramApiException {
        String fileName = update.getMessage().getDocument().getFileName();
        try {
            String projectId = Config.GCP_STORAGE_PROJECT_ID;
            String bucketName = Config.GCP_STORAGE_BUCKET_NAME;
            String objectName = "";
            String filePath = "";
            switch (fileName) {
                case Config.SCHEDULE_NAME:
                    objectName = Config.SCHEDULE_NAME;
                    filePath = Config.SCHEDULE_NAME;
                    break;
                case Config.PPRB_SCHEDULE_NAME:
                    objectName = Config.PPRB_SCHEDULE_NAME;
                    filePath = Config.PPRB_SCHEDULE_NAME;
                    break;
                default:
                    messageSender.send(update, "Ошибка при загрузке файла в облако: Некорректное имя файла");
            }

            Storage storage = StorageOptions.newBuilder().setProjectId(projectId).build().getService();
            BlobId blobId = BlobId.of(bucketName, objectName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            storage.create(blobInfo, Files.readAllBytes(Paths.get(filePath)));
        } catch (Exception e) {
            messageSender.send(update, "Exception in <UtilUpdateScheduleFile>: " + e);
        }
    }
}
