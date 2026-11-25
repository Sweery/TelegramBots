package org.example.core;

import org.example.service.FinanceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

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
        File dbFile = new File("test_command_processor.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }
        FinanceService service = new FinanceService("jdbc:sqlite:test_command_processor.db");
        service.initialize();
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
     * Проверяет успешное добавление дохода.
     */
    @Test
    void testAddIncome() {
        String result = processor.process(1L, "/add_income 100000 зарплата");
        Assertions.assertEquals("Доход успешно добавлен!", result);
    }

    /**
     * Проверяет обработку ошибки, если сумма не является числом.
     */
    @Test
    void testAddExpenseBadSum() {
        String result = processor.process(1L, "/add_expense кофе 300");
        Assertions.assertEquals("Ошибка. Сумма должна быть числом.", result);
    }

    /**
     * Проверяет обработку ошибки, если сумма не является числом.
     */
    @Test
    void testAddIncomeBadSum() {
        String result = processor.process(1L, "/add_income зарплата зарплата");
        Assertions.assertEquals("Ошибка. Сумма должна быть числом.", result);
    }

    /**
     * Проверяет обработку ошибки, если сумма отрицательная.
     */
    @Test
    void testAddExpenseMinusSum() {
        String result = processor.process(1L, "/add_expense -50 штраф");
        Assertions.assertEquals("Ошибка. Сумма должна быть больше нуля.", result);
    }

    /**
     * Проверяет обработку ошибки, если сумма отрицательная.
     */
    @Test
    void testAddIncomeMinusSum() {
        String result = processor.process(1L, "/add_income -12000 кредит");
        Assertions.assertEquals("Ошибка. Сумма должна быть больше нуля.", result);
    }

    /**
     * Проверяет, что при пустом списке выводится сообщение об отсутствии записей.
     */
    @Test
    void testExpenseListEmpty() {
        String result = processor.process(1L, "/list_expenses");
        Assertions.assertEquals("Список пуст.", result);
    }

    /**
     * Проверяет, что при пустом списке выводится сообщение об отсутствии записей.
     */
    @Test
    void testIncomeListEmpty() {
        String result = processor.process(1L, "/list_incomes");
        Assertions.assertEquals("Список пуст.", result);
    }

    /**
     * Проверяет обработку ошибки при удалении записи с несуществующим ID.
     */
    @Test
    void testExpenseDeleteBadId() {
        String result = processor.process(1L, "/delete_expense 999");
        Assertions.assertEquals("Ошибка. Записи с таким ID не существует.", result);
    }

    /**
     * Проверяет обработку ошибки при удалении записи с несуществующим ID.
     */
    @Test
    void testIncomeDeleteBadId() {
        String result = processor.process(1L, "/delete_income 999");
        Assertions.assertEquals("Ошибка. Записи с таким ID не существует.", result);
    }

    /**
     * Проверяет обработку ошибки при удалении записи траты, если ID не является числом.
     */
    @Test
    void testExpenseDeleteNoNumberId() {
        String result = processor.process(1L, "/delete_expense abc");
        Assertions.assertEquals("Ошибка. ID должен быть числом.", result);
    }

    /**
     * Проверяет обработку ошибки при удалении записи дохода, если ID не является числом.
     */
    @Test
    void testIncomeDeleteNoNumberId() {
        String result = processor.process(1L, "/delete_income abc");
        Assertions.assertEquals("Ошибка. ID должен быть числом.", result);
    }

    /**
     * Проверяет обработку ошибки при редактировании записи траты с несуществующим номером.
     */
    @Test
    void testExpenseEditBadId() {
        String result = processor.process(1L, "/edit_expense 999 999 qwerty");
        Assertions.assertEquals("Ошибка. Записи с таким номером не существует.", result);
    }


    /**
     * Проверяет обработку ошибки при редактировании записи дохода с несуществующим номером.
     */
    @Test
    void testIncomeEditBadId() {
        String result = processor.process(1L, "/edit_income 999 999 qwerty");
        Assertions.assertEquals("Ошибка. Записи с таким номером не существует.", result);
    }

    /**
     * Проверяет обработку ошибки при редактировании записи траты с отрицательным ID.
     */
    @Test
    void testExpenseEditMinusId() {
        String result = processor.process(1L, "/edit_expense 1 -999 qwerty");
        Assertions.assertEquals("Ошибка. Сумма должна быть больше нуля.", result);
    }

    /**
     * Проверяет обработку ошибки при редактировании записи дохода с отрицательным ID.
     */
    @Test
    void testIncomeEditMinusId() {
        String result = processor.process(1L, "/edit_income 1 -999 qwerty");
        Assertions.assertEquals("Ошибка. Сумма должна быть больше нуля.", result);
    }
}
