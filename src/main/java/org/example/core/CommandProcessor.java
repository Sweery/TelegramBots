package org.example.core;

import org.example.service.FinanceService;

public class CommandProcessor {
    public CommandProcessor(FinanceService financeService) {
    }

    public String process(long userId, String text) {
        return "Заглушка: логика бота еще не подключена.";
    }
}