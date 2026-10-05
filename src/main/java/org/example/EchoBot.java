package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            SendMessage answer = new SendMessage();
            answer.setChatId(String.valueOf(chatId));

            if ("/start".equals(message)) {
                answer.setText(MessageIsStart);
            } else if ("/help".equals(message)) {
                answer.setText(MessageIsHelp);
            } else {
                answer.setText("You wrote: " + message);
            }

            try {
                execute(answer);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
