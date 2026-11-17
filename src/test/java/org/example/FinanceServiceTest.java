package org.example;

import org.example.service.FinanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FinanceServiceTest {

    private FinanceService service;
    private final long TEST_USER_ID = 999L;

    @BeforeEach
    void setUp() throws SQLException {
        File dbFile = new File("test_finance.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }
        service = new FinanceService("jdbc:sqlite:test_finance.db");
        service.initialize();
    }

    /**
     * Тестирование добавления трат и вывода списка трат
     * @throws SQLException
     */

    @Test
    void testAddAndGetExpenses() throws SQLException {
        service.addExpense(TEST_USER_ID, 150.50, "Кофе");
        service.addExpense(TEST_USER_ID, 500.00, "Продукты");

        List<String> expenses = service.getExpenses(TEST_USER_ID);

        assertEquals(2, expenses.size(), "Должно быть 2 записи о тратах");
        assertEquals("150.5 — Кофе", expenses.get(0));
        assertEquals("500.0 — Продукты", expenses.get(1));
    }

    /**
     * Тестирование добавление дохода и вывода списка доходов
     * @throws SQLException
     */
    @Test
    void testAddAndGetIncomes() throws SQLException {
        service.addIncome(TEST_USER_ID, 10000.0, "Зарплата");
        service.addIncome(TEST_USER_ID, 2000.0, "Фриланс");

        List<String> incomes = service.getIncomes(TEST_USER_ID);

        assertEquals(2, incomes.size());
        assertEquals("10000.0 — Зарплата", incomes.get(0));
    }

    /**
     * Тестирование вывода пустого списка трат
     * @throws SQLException
     */
    @Test
    void testEmptyList() throws SQLException {
        List<String> expenses = service.getExpenses(TEST_USER_ID);
        assertTrue(expenses.isEmpty(), "Список трат должен быть пустым");
    }

    /**
     * Тестирование удаления траты
     * @throws SQLException
     */

    @Test
    void testDeleteExpense() throws SQLException {
        service.addExpense(TEST_USER_ID, 100.0, "Такси");
        service.addExpense(TEST_USER_ID, 200.0, "Обед");

        boolean deleted = service.deleteExpense(TEST_USER_ID, 1);
        assertTrue(deleted, "Запись должна быть удалена");

        List<String> expenses = service.getExpenses(TEST_USER_ID);
        assertEquals(1, expenses.size(), "Осталась одна запись");
        assertEquals("200.0 — Обед", expenses.get(0), "Осталась вторая запись");

        boolean deletedAgain = service.deleteExpense(TEST_USER_ID, 5);
        assertFalse(deletedAgain, "Удаление несуществующей записи должно вернуть false");
    }

    /**
     * Тестирование редактирования дохода
     * @throws SQLException
     */
    @Test
    void testEditIncome() throws SQLException {
        service.addIncome(TEST_USER_ID, 1000.0, "Подработка");

        boolean edited = service.editIncome(TEST_USER_ID, 1, 5000.0, "Премия");
        assertTrue(edited, "Изменения должны примениться");

        List<String> incomes = service.getIncomes(TEST_USER_ID);
        assertEquals("5000.0 — Премия", incomes.get(0));
    }

    /**
     * Тестирование редактирования пустой записи
     * @throws SQLException
     */

    @Test
    void testEditNonExistentRecord() throws SQLException {
        boolean edited = service.editExpense(TEST_USER_ID, 1, 100.0, "Неважно");
        assertFalse(edited, "Нельзя изменить несуществующую запись");
    }

    /**
     * Тестирование сумм трат и доходов
     * @throws SQLException
     */
    @Test
    void testSums() throws SQLException {
        service.addExpense(TEST_USER_ID, 100.0, "Трата 1");
        service.addExpense(TEST_USER_ID, 200.0, "Трата 2");
        service.addIncome(TEST_USER_ID, 5000.0, "Доход 1");

        double totalExpenses = service.getSumExpenses(TEST_USER_ID);
        double totalIncomes = service.getSumIncomes(TEST_USER_ID);

        assertEquals(300.0, totalExpenses, 0.001, "Сумма трат должна быть 300");
        assertEquals(5000.0, totalIncomes, 0.001, "Сумма доходов должна быть 5000");
    }

    @Test
    void testSumsAreZeroWhenEmpty() throws SQLException {
        assertEquals(0.0, service.getSumExpenses(TEST_USER_ID), 0.001);
        assertEquals(0.0, service.getSumIncomes(TEST_USER_ID), 0.001);
    }


    /**
     * Тестирование использования бота 2 пользователями
     * @throws SQLException
     */

    @Test
    void testDataIsolationBetweenUsers() throws SQLException {
        long user1 = 1L;
        long user2 = 2L;

        service.addExpense(user1, 100.0, "Трата первого");
        service.addExpense(user2, 200.0, "Трата второго");

        assertEquals(1, service.getExpenses(user1).size());
        assertEquals(1, service.getExpenses(user2).size());

        assertEquals(100.0, service.getSumExpenses(user1), 0.001);
        assertEquals(200.0, service.getSumExpenses(user2), 0.001);
    }
}