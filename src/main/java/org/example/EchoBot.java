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

    private static final String MessageIsStart = """
            Hello. I am an Echo-Bot. \n
            I repeat everything you write. \n
            Type /help to see the available commands.
            """;

    private static final String MessageIsHelp = """
            Available commands: \n
            /start - start using the bot; \n
            /help - show this message; \n
            I can also repeat any message you send.
            """;

    /**
     * Обрабатывает входящие сообщения
     * @param update объект обновления от Telegram
     */

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            SendMessage answer = new SendMessage();
            answer.setChatId(String.valueOf(chatId));
            answer.setText(buildResponse(message));

            try {
                execute(answer);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Формирует текст ответа на основе входящего сообщения.
     * Вынесено в отдельный метод для тестируемости.
     * @param message входящий текст
     * @return текст ответа
     */

    String buildResponse(String message) {
        if ("/start".equals(message)) {
            return MessageIsStart;
        } else if ("/help".equals(message)) {
            return MessageIsHelp;
        } else {
            return "You wrote: " + message;
        }
    }
}
