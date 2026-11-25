package org.example.service;

import org.example.model.Transaction;
import org.example.model.TransactionType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис учёта доходов и расходов пользователя для Telegram-бота. Обеспечивает хранение и обработку финансовых записей (транзакций) в базе данных
 * SQLite. Каждая запись привязана к пользователю по его Telegram ID и имеет тип (доход или расход).
 */
public class FinanceService {

    /**
     * JDBC-URL для подключения к базе данных SQLite.
     */
    private final String dbUrl;

    public FinanceService(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    /**
     * Инициализирует базу данных: загружает драйвер SQLite и создаёт таблицу.
     */
    public void initialize() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Драйвер SQLite не найден в зависимостях проекта", e);
        }
        String sql = """
            CREATE TABLE IF NOT EXISTS transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                type TEXT NOT NULL CHECK(type IN ('income', 'expense')),
                amount REAL NOT NULL,
                name TEXT NOT NULL
            );
        """;
        try (Connection conn = DriverManager.getConnection(dbUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании таблицы", e);
        }
    }

    /**
     * Функция добавления траты в бд
     * @param transaction телеграм id пользователя
     * @throws SQLException
     */
    public void addTransaction(Transaction transaction) throws SQLException {
        String sql = "INSERT INTO transactions(user_id, type, amount, name) VALUES(?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, transaction.getId());
            pstmt.setString(2, transaction.getType().name().toLowerCase());
            pstmt.setDouble(3, transaction.getSum());
            pstmt.setString(4, transaction.getName());
            pstmt.executeUpdate();
        }
    }

    /**
     * Функция вывода списка трат
     * @param userId телеграм id пользователя
     * @return
     * @throws SQLException
     */
    public List<Transaction> getTransaction(long userId, TransactionType type) throws SQLException {
        String sql = "SELECT amount, name FROM transactions WHERE user_id = ? AND type = ? ORDER BY id ASC";
        List<Transaction> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setString(2, type.name().toLowerCase());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Transaction(
                            userId,
                            type,
                            rs.getDouble("amount"),
                            rs.getString("name")
                    ));
                }
            }
        }
        return list;
    }

    /**
     * Функция удаления траты по номеру
     * @param userId телеграм id пользователя
     * @param index номер траты которую нужно удалить
     * @return
     * @throws SQLException
     */
    public boolean deleteTransaction(long userId, TransactionType type, int index) throws SQLException {
        if (index < 1)
            return false;
        String sql = """
                DELETE FROM transactions 
                WHERE id = (
                    SELECT id FROM transactions 
                    WHERE user_id = ? AND type = ?
                    ORDER BY id ASC LIMIT 1 OFFSET ?
                )
                """;
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setString(2, type.name().toLowerCase());
            pstmt.setInt(3, index - 1);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Функция изменения траты
     * @param userId телеграм id пользователя
     * @param index номер изменяемой траты
     * @param newAmount новая сумма траты
     * @param newName новое наименование траты
     * @return
     * @throws SQLException
     */
    public boolean editTransaction(long userId, TransactionType type, int index, double newAmount, String newName) throws SQLException {
        if (index < 1)
            return false;
        String sql = """
                UPDATE transactions 
                SET amount = ?, name = ? 
                WHERE id = (
                    SELECT id FROM transactions 
                    WHERE user_id = ? AND type = ?
                    ORDER BY id ASC LIMIT 1 OFFSET ?
                )
                """;
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newAmount);
            pstmt.setString(2, newName);
            pstmt.setLong(3, userId);
            pstmt.setString(4, type.name().toLowerCase());
            pstmt.setInt(5, index - 1);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Функция вывода суммы всех трат пользователя
     * @param userId телеграм id пользователя
     * @return
     * @throws SQLException
     */
    public double getSum(long userId, TransactionType type) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND type = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setString(2, type.name().toLowerCase());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        }
    }
}
