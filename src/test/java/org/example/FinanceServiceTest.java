package org.example;

import org.example.model.Transaction;
import org.example.model.TransactionType;
import org.example.service.FinanceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * Тесты для {@link FinanceService} — сервиса учёта доходов и расходов Telegram-бота.
 */
class FinanceServiceTest {

    private FinanceService service;

    /**
     * Telegram ID тестового пользователя, от имени которого выполняются операции.
     */
    private final long TEST_USER_ID = 999L;

    /**
     * Подготовка окружения перед каждым тестом: удаляет файл тестовой БД
     * (если он есть) и создаёт новый экземпляр {@link FinanceService}
     * с вызовом {@link FinanceService#initialize()}.
     */
    @BeforeEach
    void setUp() {
        File dbFile = new File("test_finance.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }
        service = new FinanceService("jdbc:sqlite:test_finance.db");
        service.initialize();
    }

    /**
     * Тестирование добавления трат и вывода списка трат
     */
    @Test
    void testAddAndGetExpenses() throws SQLException {
        service.addTransaction(new Transaction(TEST_USER_ID, TransactionType.EXPENSE, 150.50, "Кофе"));
        service.addTransaction(new Transaction(TEST_USER_ID, TransactionType.EXPENSE, 500.00, "Продукты"));

        List<Transaction> expenses = service.getTransaction(TEST_USER_ID, TransactionType.EXPENSE);

        Assertions.assertEquals(2, expenses.size(), "Должно быть 2 записи о тратах");
        Assertions.assertEquals(150.5, expenses.get(0).getSum(), 0.001);
        Assertions.assertEquals("Кофе", expenses.get(0).getName());
        Assertions.assertEquals(500.0, expenses.get(1).getSum(), 0.001);
        Assertions.assertEquals("Продукты", expenses.get(1).getName());
    }

    /**
     * Тестирование добавления дохода и вывода списка доходов
     */
    @Test
    void testAddAndGetIncomes() throws SQLException {
        service.addTransaction(new Transaction(TEST_USER_ID, TransactionType.INCOME, 10000.0, "Зарплата"));
        service.addTransaction(new Transaction(TEST_USER_ID, TransactionType.INCOME, 2000.0, "Фриланс"));

        List<Transaction> incomes = service.getTransaction(TEST_USER_ID, TransactionType.INCOME);

        Assertions.assertEquals(2, incomes.size());
        Assertions.assertEquals(10000.0, incomes.get(0).getSum(), 0.001);
        Assertions.assertEquals("Зарплата", incomes.get(0).getName());
    }

    /**
     * Тестирование вывода пустого списка трат
     */
    @Test
    void testEmptyList() throws SQLException {
        List<Transaction> expenses = service.getTransaction(TEST_USER_ID, TransactionType.EXPENSE);
        Assertions.assertTrue(expenses.isEmpty(), "Список трат должен быть пустым");
    }

    /**
     * Тестирование удаления траты
     */
    @Test
    void testDeleteExpense() throws SQLException {
        service.addTransaction(new Transaction(TEST_USER_ID, TransactionType.EXPENSE, 100.0, "Такси"));
        service.addTransaction(new Transaction(TEST_USER_ID, TransactionType.EXPENSE, 200.0, "Обед"));

        boolean deleted = service.deleteTransaction(TEST_USER_ID, TransactionType.EXPENSE, 1);
        Assertions.assertTrue(deleted, "Запись должна быть удалена");

        List<Transaction> expenses = service.getTransaction(TEST_USER_ID, TransactionType.EXPENSE);
        Assertions.assertEquals(1, expenses.size(), "Осталась одна запись");
        Assertions.assertEquals(200.0, expenses.get(0).getSum(), 0.001);
        Assertions.assertEquals("Обед", expenses.get(0).getName(), "Осталась вторая запись");

        boolean deletedAgain = service.deleteTransaction(TEST_USER_ID, TransactionType.EXPENSE, 5);
        Assertions.assertFalse(deletedAgain, "Удаление несуществующей записи должно вернуть false");
    }

    /**
     * Тестирование редактирования дохода
     */
    @Test
    void testEditIncome() throws SQLException {
        service.addTransaction(new Transaction(TEST_USER_ID, TransactionType.INCOME, 1000.0, "Подработка"));

        boolean edited = service.editTransaction(TEST_USER_ID, TransactionType.INCOME, 1, 5000.0, "Премия");
        Assertions.assertTrue(edited, "Изменения должны примениться");

        List<Transaction> incomes = service.getTransaction(TEST_USER_ID, TransactionType.INCOME);
        Assertions.assertEquals(5000.0, incomes.get(0).getSum(), 0.001);
        Assertions.assertEquals("Премия", incomes.get(0).getName());
    }

    /**
     * Тестирование редактирования пустой записи
     */
    @Test
    void testEditNonExistentRecord() throws SQLException {
        boolean edited = service.editTransaction(TEST_USER_ID, TransactionType.EXPENSE, 1, 100.0, "Неважно");
        Assertions.assertFalse(edited, "Нельзя изменить несуществующую запись");
    }

    /**
     * Тестирование сумм трат и доходов
     */
    @Test
    void testSums() throws SQLException {
        service.addTransaction(new Transaction(TEST_USER_ID, TransactionType.EXPENSE, 100.0, "Трата 1"));
        service.addTransaction(new Transaction(TEST_USER_ID, TransactionType.EXPENSE, 200.0, "Трата 2"));
        service.addTransaction(new Transaction(TEST_USER_ID, TransactionType.INCOME, 5000.0, "Доход 1"));

        double totalExpenses = service.getSum(TEST_USER_ID, TransactionType.EXPENSE);
        double totalIncomes = service.getSum(TEST_USER_ID, TransactionType.INCOME);

        Assertions.assertEquals(300.0, totalExpenses, 0.001, "Сумма трат должна быть 300");
        Assertions.assertEquals(5000.0, totalIncomes, 0.001, "Сумма доходов должна быть 5000");
    }

    @Test
    void testSumsAreZeroWhenEmpty() throws SQLException {
        Assertions.assertEquals(0.0, service.getSum(TEST_USER_ID, TransactionType.EXPENSE), 0.001);
        Assertions.assertEquals(0.0, service.getSum(TEST_USER_ID, TransactionType.INCOME), 0.001);
    }

    /**
     * Тестирование использования бота 2 пользователями
     */
    @Test
    void testDataIsolationBetweenUsers() throws SQLException {
        long user1 = 1L;
        long user2 = 2L;

        service.addTransaction(new Transaction(user1, TransactionType.EXPENSE, 100.0, "Трата первого"));
        service.addTransaction(new Transaction(user2, TransactionType.EXPENSE, 200.0, "Трата второго"));

        Assertions.assertEquals(1, service.getTransaction(user1, TransactionType.EXPENSE).size());
        Assertions.assertEquals(1, service.getTransaction(user2, TransactionType.EXPENSE).size());

        Assertions.assertEquals(100.0, service.getSum(user1, TransactionType.EXPENSE), 0.001);
        Assertions.assertEquals(200.0, service.getSum(user2, TransactionType.EXPENSE), 0.001);
    }
}
