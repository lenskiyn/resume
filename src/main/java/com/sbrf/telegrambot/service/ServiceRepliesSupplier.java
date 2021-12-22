package com.sbrf.telegrambot.service;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

public class ServiceRepliesSupplier {

    public static String getReply(String commandName) {
        try {
            JsonReader jsonReader = new JsonReader(new FileReader("replies.txt"));
            HashMap<String, String> replies = new Gson().fromJson(jsonReader, HashMap.class);
            return replies.get(commandName);
        } catch (FileNotFoundException e) {
            return "Exception in <getReply>: " + e.getMessage();
        }
    }
}
