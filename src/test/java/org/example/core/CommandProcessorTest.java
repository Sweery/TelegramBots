package org.example.core;

import org.example.service.FinanceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Тесты для проверки корректности обработки команд бота.
 */
public class CommandProcessorTest {
    private CommandProcessor processor;

    /**
     * Подготовка тестового окружения перед каждым тестом.
     */
    @BeforeEach
    void setUp() {
        FinanceService service = new FinanceService();
        processor = new CommandProcessor(service);
    }

    /**
     * Проверяет успешное добавление траты.
     */
    @Test
    void testAddExpense() {
        String result = processor.process(1L, "/add_expense 300 кофе");
        Assertions.assertEquals("Трата успешно добавлена!", result);
    }

    /**
     * Проверяет обработку ошибки, если сумма не является числом.
     */
    @Test
    void testAddExpenseBadSum() {
        // Ввели буквы вместо цифр
        String result = processor.process(1L, "/add_expense кофе 300");
        Assertions.assertEquals("Ошибка. Сумма должна быть числом.", result);
    }

    /**
     * Проверяет обработку ошибки, если сумма отрицательная.
     */
    @Test
    void testAddExpenseMinusSum() {
        // Ввели отрицательную сумму
        String result = processor.process(1L, "/add_expense -50 штраф");
        Assertions.assertEquals("Ошибка. Сумма должна быть больше нуля.", result);
    }

    /**
     * Проверяет, что при пустом списке выводится сообщение об отсутствии записей.
     */
    @Test
    void testListEmpty() {
        String result = processor.process(1L, "/list_expenses");
        Assertions.assertEquals("Список пуст.", result);
    }

    /**
     * Проверяет обработку ошибки при удалении записи с несуществующим ID.
     */
    @Test
    void testDeleteBadId() {
        // Сервис вернет false, так как он пустой
        String result = processor.process(1L, "/delete_expense 999");
        Assertions.assertEquals("Ошибка. Записи с таким ID не существует.", result);
    }
}