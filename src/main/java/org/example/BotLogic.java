package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotLogic {
    private static final String MESSAGE_IS_START = """
            Hello. I am an Echo-Bot. \n
            I repeat everything you write. \n
            Type /help to see the available commands.
            """;

    private static final String MESSAGE_IS_HELP = """
            Available commands: \n
            /start - start using the bot; \n
            /help - show this message; \n
            I can also repeat any message you send.
            """;

    private static final Logger log = LoggerFactory.getLogger(BotLogic.class);

    public String handleMessage(long chatId, String text) {
        log.debug("Обработка сообщение: chatId={}, text={}", chatId, text);
        if (text == null || text.isBlank()) {
            log.warn("Пустое сообщение от chatId={}", chatId);
            return "You sent an empty message";
        }
        if ("/start".equals(text)) {
            log.info("Команда /start от chatId={}", chatId);
            return MESSAGE_IS_START;
        } else if ("/help".equals(text)) {
            log.info("Команда /help от chatId={}", chatId);
            return MESSAGE_IS_HELP;
        } else {
            log.debug("Ответ от chatId={}", chatId);
            return "You wrote: " + text;
        }
    }
}
