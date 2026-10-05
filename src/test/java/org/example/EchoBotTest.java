package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

/**
 * Тесты логики ответов эхо-бота.
 */
class EchoBotTest {
    private final EchoBot bot = new EchoBot();

    /**
     * /start возвращает приветствие с именем бота и подсказкой /help.
     */
    @Test
    void startCommand_returnsStartMessage() {
        String response = bot.buildResponse("/start");
        Assertions.assertTrue(response.contains("Echo-Bot"));
        Assertions.assertTrue(response.contains("/help"));
    }

    /**
     * /help возвращает список всех команд.
     */
    @Test
    void helpCommand_returnsHelpMessage() {
        String response = bot.buildResponse("/help");
        Assertions.assertTrue(response.contains("Available commands"));
        Assertions.assertTrue(response.contains("/start"));
        Assertions.assertTrue(response.contains("/help"));
    }

    /**
     * Обычное сообщение возвращается как эхо.
     */
    @Test
    void regularMessage_returnsEcho() {
        String response = bot.buildResponse("Привет");
        Assertions.assertEquals("You wrote: Привет", response);
    }

    /**
     * Пустое сообщение возвращается как эхо с пустым текстом.
     */
    @Test
    void emptyMessage_returnsEchoWithEmptySuffix() {
        String response = bot.buildResponse("");
        Assertions.assertEquals("You wrote: ", response);
    }

    /**
     * Неизвестная команда обрабатывается как обычное сообщение.
     */
    @Test
    void unknownCommand_returnsEcho() {
        String response = bot.buildResponse("/unknown");
        Assertions.assertEquals("You wrote: /unknown", response);
    }
}