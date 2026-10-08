package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Точка входа в Telegram-Bot
 */

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    /**
     * Запускает бота
     * @param args аргументыкомандной строки
     */

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new TelegramBot());
            System.out.println("Бот успешно запущен!");
        } catch (TelegramApiException e) {
            log.error("Не удалось запустить Telegram-бота: {}", e.getMessage(), e);
        }
    }
}