package org.example;

import org.example.service.FinanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FinanceServiceTest {

    private final long TEST_USER_ID = 999L;

    @BeforeEach
    void setUp() throws SQLException {
        // 1. Переключаем БД на оперативную память (создается заново для каждого теста)
        FinanceService.setDbUrl("jdbc:sqlite::memory:");

        // 2. Инициализируем таблицу
        FinanceService.initialize();
    }

    /**
     * Тестирование функции добавления трат и вызова списка трат
     * @throws SQLException
     */

    @Test
    void testAddAndGetExpenses() throws SQLException {
        FinanceService.addExpense(TEST_USER_ID, 150.50, "Кофе");
        FinanceService.addExpense(TEST_USER_ID, 500.00, "Продукты");

        List<String> expenses = FinanceService.getExpenses(TEST_USER_ID);

        assertEquals(2, expenses.size(), "Должно быть 2 записи о тратах");
        assertEquals("150.5 — Кофе", expenses.get(0)); // Т.к. сортировка по возрастанию ID
        assertEquals("500.0 — Продукты", expenses.get(1));
    }

    /**
     * Тестирование функции добавления дохода и вызова списка доходов
     * @throws SQLException
     */

    @Test
    void testAddAndGetIncomes() throws SQLException {
        FinanceService.addIncome(TEST_USER_ID, 10000.0, "Зарплата");
        FinanceService.addIncome(TEST_USER_ID, 2000.0, "Фриланс");

        List<String> incomes = FinanceService.getIncomes(TEST_USER_ID);

        assertEquals(2, incomes.size());
        assertEquals("10000.0 — Зарплата", incomes.get(0));
    }

    /**
     * Тестирование вызова пустого списка
     * @throws SQLException
     */

    @Test
    void testEmptyList() throws SQLException {
        List<String> expenses = FinanceService.getExpenses(TEST_USER_ID);
        assertTrue(expenses.isEmpty(), "Список трат должен быть пустым");
    }

    /**
     * Тестирование удаления записи траты
     * @throws SQLException
     */

    @Test
    void testDeleteExpense() throws SQLException {
        FinanceService.addExpense(TEST_USER_ID, 100.0, "Такси");
        FinanceService.addExpense(TEST_USER_ID, 200.0, "Обед");

        // Удаляем первую запись
        boolean deleted = FinanceService.deleteExpense(TEST_USER_ID, 1);
        assertTrue(deleted, "Запись должна быть удалена");

        List<String> expenses = FinanceService.getExpenses(TEST_USER_ID);
        assertEquals(1, expenses.size(), "Осталась одна запись");
        assertEquals("200.0 — Обед", expenses.get(0), "Осталась вторая запись");

        // Пытаемся удалить несуществующую
        boolean deletedAgain = FinanceService.deleteExpense(TEST_USER_ID, 5);
        assertFalse(deletedAgain, "Удаление несуществующей записи должно вернуть false");
    }

    /**
     * Тестирование редактирования записи
     * @throws SQLException
     */

    @Test
    void testEditIncome() throws SQLException {
        FinanceService.addIncome(TEST_USER_ID, 1000.0, "Подработка");

        boolean edited = FinanceService.editIncome(TEST_USER_ID, 1, 5000.0, "Премия");
        assertTrue(edited, "Изменения должны примениться");

        List<String> incomes = FinanceService.getIncomes(TEST_USER_ID);
        assertEquals("5000.0 — Премия", incomes.get(0));
    }

    /**
     * Тестирование редактировния пустой записи
     * @throws SQLException
     */

    @Test
    void testEditNonExistentRecord() throws SQLException {
        boolean edited = FinanceService.editExpense(TEST_USER_ID, 1, 100.0, "Неважно");
        assertFalse(edited, "Нельзя изменить несуществующую запись");
    }

    /**
     * Тестирование суммы трат
     * @throws SQLException
     */

    @Test
    void testSums() throws SQLException {
        FinanceService.addExpense(TEST_USER_ID, 100.0, "Трата 1");
        FinanceService.addExpense(TEST_USER_ID, 200.0, "Трата 2");
        FinanceService.addIncome(TEST_USER_ID, 5000.0, "Доход 1");

        double totalExpenses = FinanceService.getSumExpenses(TEST_USER_ID);
        double totalIncomes = FinanceService.getSumIncomes(TEST_USER_ID);

        assertEquals(300.0, totalExpenses, 0.001, "Сумма трат должна быть 300");
        assertEquals(5000.0, totalIncomes, 0.001, "Сумма доходов должна быть 5000");
    }

    @Test
    void testSumsAreZeroWhenEmpty() throws SQLException {
        assertEquals(0.0, FinanceService.getSumExpenses(TEST_USER_ID), 0.001);
        assertEquals(0.0, FinanceService.getSumIncomes(TEST_USER_ID), 0.001);
    }


    /**
     * Тестирование использования бота разными пользователями
     * @throws SQLException
     */

    @Test
    void testDataIsolationBetweenUsers() throws SQLException {
        long user1 = 1L;
        long user2 = 2L;

        FinanceService.addExpense(user1, 100.0, "Трата первого");
        FinanceService.addExpense(user2, 200.0, "Трата второго");

        assertEquals(1, FinanceService.getExpenses(user1).size());
        assertEquals(1, FinanceService.getExpenses(user2).size());

        // Проверяем, что суммы считаются отдельно
        assertEquals(100.0, FinanceService.getSumExpenses(user1), 0.001);
        assertEquals(200.0, FinanceService.getSumExpenses(user2), 0.001);
    }
}
