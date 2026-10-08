package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Эхо-бот: повторяет сообщения и обрыбатывает команды /start и /help
 */

public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(TelegramBot.class);

    @Override
    public String getBotUsername() {
        return System.getenv("BOT_USERNAME");
    }

    @Override
    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }

    private static final BotLogic bot = new BotLogic();

    /**
     * Обрабатывает входящие сообщения
     * @param update объект обновления от Telegram
     */

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String userName = update.getMessage().getFrom().getUserName();

            log.info("Входящее сообщение от chatId={}, user={}: {}", chatId, userName, message);

            String answer = bot.handleMessage(chatId, message);

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
