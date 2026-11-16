package org.example.core;

import org.example.model.Transaction;
import org.example.model.TransactionType;
import org.example.service.FinanceService;

import java.util.Arrays;
import java.util.List;


/**
 * Класс, содержащий основную логику обработки команд Telegram-бота.
 */
public class CommandProcessor {
    private final FinanceService financeService;

    public CommandProcessor(FinanceService financeService) {
        this.financeService = financeService;
    }

    /**
     * Главный метод обработки входящего сообщения.
     * Разбивает входящий текст на команду и аргументы, определяет тип команды
     * и вызывает соответствующий вспомогательный метод.
     *
     * @param userId идентификатор пользователя в Telegram
     * @param text   полный текст сообщения от пользователя
     * @return строка с ответом для пользователя
     */
    public String process(long userId, String text) {
        String[] parts = text.trim().split("\\s+");
        String command = parts[0];
        try {
            switch (command) {
                case "/start":
                    return "Привет! Я финансовый бот. " +
                            "С моей помощью ты можешь записывать свои траты и доходы, " +
                            "а также получать сводку по бюджету.";
                case "/help":
                    return "Справка по командам: \n" +
                            "/add_expense <сумма> <название> - добавить трату \n" +
                            "/add_income <сумма> <название> - добавить доход \n" +
                            "/list_expenses - список трат \n" +
                            "/list_incomes - список доходов \n" +
                            "/sum_expenses - сумма трат \n" +
                            "/delete_expense <ID> - удалить трату \n" +
                            "/edit_expense <ID> <сумма> <название> - изменить трату";
                case "/add_expense":
                    return addTransaction(userId, parts, TransactionType.EXPENSE,
                            "Трата успешно добавлена!");
                case "/add_income":
                    return addTransaction(userId, parts, TransactionType.INCOME,
                            "Доход успешно добавлен!");
                case "/list_expenses":
                    return listTransactions(userId, TransactionType.EXPENSE,
                            "Ваши траты: ");
                case "/list_incomes":
                    return listTransactions(userId, TransactionType.INCOME,
                            "Ваши доходы: ");
                case "/delete_expense":
                    return deleteTransaction(userId, parts, TransactionType.EXPENSE);
                case "/delete_income":
                    return deleteTransaction(userId, parts, TransactionType.INCOME);
                case "/edit_expense":
                    return editTransaction(userId, parts, TransactionType.EXPENSE);
                case "/edit_income":
                    return editTransaction(userId, parts, TransactionType.INCOME);
                case "/sum_expenses":
                    return getSum(userId, TransactionType.EXPENSE,
                            "Сумма ваших трат: ");
                case "/sum_incomes":
                    return getSum(userId, TransactionType.INCOME,
                            "Сумма ваших доходов: ");
                default:
                    return "Неизвестная команда. Введите /help для справки.";
            }
        } catch (Exception e) {
            return "Ошибка выполнения команды. Проверьте введенные данные.";
        }
    }

    /**
     * Обрабатывает добавление новой транзакции
     * @param userId идентификатор пользователя
     * @param parts массив слов из сообщения пользователя
     * @param type тип транзакции (расход или доход)
     * @param s сообщение, которое будет возвращено при успешном добавлении
     * @return сообщение об успехе или текст ошибки
     */
    private String addTransaction(long userId, String[] parts, TransactionType type, String s) {
        if (parts.length <= 3)
            return "Формат: /" + (type == TransactionType.EXPENSE ? "/add_expense" : "/add_income") +
                    " <сумма> <название> ";
        double amount;
        try {
            amount = Double.parseDouble(parts[1]);
            if (amount <= 0)
                return "Ошибка. Сумма не может быть меньше либо равна нулю.";
        } catch (Exception e) {
            return "Сумма должна быть числом.";
        }
        String name = String.join(" ", Arrays.copyOfRange(parts, 2, parts.length));
        financeService.addTransaction(userId, type, amount, name);
        return s;
    }

    /**
     * Формирует нумерованный список транзакций заданного типа для вывода пользователю.
     * @param userId идентификатор пользователя
     * @param type   тип запрашиваемых транзакций (расход или доход)
     * @param title  заголовок списка
     * @return отформатированная строка со списком
     */
    private String listTransactions(long userId, TransactionType type, String title) {
        List<Transaction> list = financeService.getTransactions(userId, type);
        if (list.isEmpty())
            return "Список пуст.";
        StringBuilder str = new StringBuilder(title).append("\n");
        for (Transaction t : list) {
            str.append("[ID: ").append(t.getId()).append("] ").append(t.toString()).append("\n");
        }
        return str.toString();
    }

    /**
     * Удаляет транзакцию по её уникальному идентификатору.
     * @param userId идентификатор пользователя
     * @param parts  массив слов из сообщения пользователя
     * @param type   тип транзакции (расход или доход)
     * @return сообщение об успешном удалении или текст ошибки
     */
    private String deleteTransaction(long userId, String[] parts, TransactionType type) {
        if (parts.length < 2)
            return "Формат: /" + (type == TransactionType.EXPENSE ? "/delete_expense" : "/delete_income") +
                    " <номер> ";
        int id;
        try {
            id = Integer.parseInt(parts[1]);
        } catch (Exception e) {
            return "Ошибка. ID должен быть числом.";
        }
        boolean deleted = financeService.deleteTransaction(userId, id);
        if (!deleted)
            return "Ошибка. Записи с таким ID не существует.";
        return "Запись успешно удалена.";
    }

    /**
     * Редактирует существующую транзакцию по её идентификатору.
     * @param userId идентификатор пользователя
     * @param parts  массив слов из сообщения пользователя
     * @param type   тип транзакции (расход или доход)
     * @return сообщение об успешном изменении или текст ошибки
     */
    private String editTransaction(long userId, String[] parts, TransactionType type) {
        if (parts.length < 4)
            return "Формат: /" + (type == TransactionType.EXPENSE ? "/edit_expense" : "/edit_income") +
                    " <номер> <новая сумма> <новое название> ";
        int id;
        double newAmount;
        try {
            id = Integer.parseInt(parts[1]);
            newAmount = Double.parseDouble(parts[2]);
            if (newAmount <= 0)
                return "Ошибка. Сумма должна быть больше нуля.";
        } catch (Exception e) {
            return "Ошибка. ID и сумма должны быть числами.";
        }
        String newName = String.join(" ", Arrays.copyOfRange(parts, 3, parts.length));
        boolean updated = financeService.updateTransaction(userId, id, newAmount, newName);
        if (!updated)
            return "Ошибка. Записи с таким номером не существует.";
        return "Запись успешно отредактирована!";
    }

    /**
     * Вычисляет и форматирует статистику по транзакциям.
     * @param userId идентификатор пользователя
     * @param type   тип транзакций (расход или доход)
     * @param title  заголовок для вывода
     * @return отформатированная строка со статистикой
     */
    private String getSum(long userId, TransactionType type, String title) {
        List<Transaction> list = financeService.getTransactions(userId, type);
        double sum = financeService.getSum(userId, type);
        StringBuilder str = new StringBuilder(title).append(sum).append("\n \n Последние 3 записи: \n");
        if (list.isEmpty())
            str.append("Записей нет.");
        else {
            int start = Math.max(0, list.size() - 3);
            for (int i = start; i < list.size(); i++) {
                str.append(list.get(i).toString()).append("\n");
            }
        }
        return str.toString();
    }
}
