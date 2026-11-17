package org.example.bot;

import org.example.core.CommandProcessor;
import org.example.service.FinanceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Класс Telegram-бота, отвечающий за взаимодействие с API Telegram.
 */

public class TelegramBot extends TelegramLongPollingBot {
    private final Logger log = LoggerFactory.getLogger(TelegramBot.class);

    private final CommandProcessor commandProcessor;

    public TelegramBot() {
        FinanceService financeService = new FinanceService();
        this.commandProcessor = new CommandProcessor(financeService);
    }

    @Override
    public String getBotUsername() {
        return System.getenv("BOT_USERNAME");
    }

    @Override
    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }

    /**
     * Обрабатывает входящие сообщения от пользователей.
     * @param update объект обновления, содержащий информацию о входящем сообщении
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom().getUserName();

            log.info("Входящее сообщение от chatId={}, user={}: {}", chatId, userName, message);

            String answer = commandProcessor.process(chatId, message);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(String.valueOf(chatId));
            sendMessage.setText(answer);

            try {
                execute(sendMessage);
                log.debug("Ответ отправлен chatId={}", chatId);
            } catch (TelegramApiException e) {
                log.error("Не удалось отправить сообщение chatId={}: {}", chatId, e.getMessage(), e);
            }
        }
    }
}
