package com.sbrf.telegrambot.util;


import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.sbrf.telegrambot.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.nio.file.Paths;

@Component
public class UtilGetScheduleFile {
    @Autowired
    UtilMessageSender messageSender;

    public void get(Update update, String scheduleName) throws TelegramApiException {
        try {
            getSchedule(update, scheduleName);
        } catch (Exception t) {
            try {
                downloadFromCloud(scheduleName);
                getSchedule(update, scheduleName);
            } catch (Exception e) {
                messageSender.send(update, "Exception in <UtilGetScheduleFile: " + e);
            }
        }
    }

    public void downloadFromCloud(String scheduleName) {
        Storage storage = StorageOptions.newBuilder()
                .setProjectId(Config.GCP_STORAGE_PROJECT_ID)
                .build().getService();

        Blob blob = storage.get(BlobId.of(Config.GCP_STORAGE_BUCKET_NAME, scheduleName));
        blob.downloadTo(Paths.get(scheduleName));
    }

    private void getSchedule(Update update, String scheduleName) throws TelegramApiException {
        File schedule = new File(scheduleName);
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(update.getMessage().getChatId().toString());
        sendDocument.setDocument(new InputFile(schedule));
        messageSender.send(sendDocument);
    }
}
