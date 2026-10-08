package org.example;

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

    public String handleMessage(String text) {
        if (text == null || text.isBlank()) {
            return "You sent an empty message";
        }
        if ("/start".equals(text)) {
            return MESSAGE_IS_START;
        } else if ("/help".equals(text)) {
            return MESSAGE_IS_HELP;
        } else {
            return "You wrote: " + text;
        }
    }
}
