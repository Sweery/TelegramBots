package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class BotLogic {
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

    public SendMessage handleMessage(long chatId, String text) {
        SendMessage answer = new SendMessage();
        answer.setChatId(String.valueOf(chatId));
        if (text == null || text.isBlank()) {
            answer.setText("You sent an empty message");
            return answer;
        }
        if ("/start".equals(text)) {
            answer.setText(MessageIsStart);
            return answer;
        } else if ("/help".equals(text)) {
            answer.setText(MessageIsHelp);
            return answer;
        } else {
            answer.setText("You wrote: " + text);
            return answer;
        }
    }
}
