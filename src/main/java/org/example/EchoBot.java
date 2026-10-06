package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Эхо-бот: повторяет сообщения и обрыбатывает команды /start и /help
 */

public class EchoBot extends TelegramLongPollingBot {
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
            SendMessage answer = bot.handleMessage(chatId, message);

            try {
                execute(answer);
            } catch (TelegramApiException e) {
                System.err.println("Сообщение не отправлено");
                e.printStackTrace();
            }
        }
    }
}
