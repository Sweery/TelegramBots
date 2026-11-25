package org.example;

import org.example.bot.TelegramBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Точка входа в Telegram-Bot
 */
public class Main {
    /**
     * Запускает Telegram-бота.
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new TelegramBot());
            System.out.println("Бот успешно запущен!");
        } catch (TelegramApiException e) {
            System.err.println("Не удалось запустить Telegram-бота");
        }
    }
}