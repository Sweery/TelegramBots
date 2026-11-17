package org.example.service; // Убедись, что пакет совпадает с твоей папкой

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FinanceService {

    private final String dbUrl;

    public FinanceService(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public void initialize() {
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
     * @param userId телеграм id пользователя
     * @param amount размер траты
     * @param name наименование траты
     * @throws SQLException
     */

    public void addExpense(long userId, double amount, String name) throws SQLException {
        String sql = "INSERT INTO transactions(user_id, type, amount, name) VALUES(?, 'expense', ?, ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, name);
            pstmt.executeUpdate();
        }
    }

    /**
     * Функция добавления дохода в бд
     * @param userId телеграм id пользователя
     * @param amount размер дохода
     * @param name наименование дохода
     * @throws SQLException
     */

    public void addIncome(long userId, double amount, String name) throws SQLException {
        String sql = "INSERT INTO transactions(user_id, type, amount, name) VALUES(?, 'income', ?, ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, name);
            pstmt.executeUpdate();
        }
    }

    /**
     * Функция вывода списка трат
     * @param userId телеграм id пользователя
     * @return
     * @throws SQLException
     */
    public List<String> getExpenses(long userId) throws SQLException {
        String sql = "SELECT amount, name FROM transactions WHERE user_id = ? AND type = 'expense' ORDER BY id ASC";
        List<String> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getDouble("amount") + " — " + rs.getString("name"));
                }
            }
        }
        return list;
    }

    /**
     * Функция вывода списка доходов
     * @param userId телеграм id пользователя
     * @return
     * @throws SQLException
     */

    public List<String> getIncomes(long userId) throws SQLException {
        String sql = "SELECT amount, name FROM transactions WHERE user_id = ? AND type = 'income' ORDER BY id ASC";
        List<String> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getDouble("amount") + " — " + rs.getString("name"));
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

    public boolean deleteExpense(long userId, int index) throws SQLException {
        String sql = "DELETE FROM transactions WHERE id = (SELECT id FROM transactions WHERE user_id = ? AND type = 'expense' ORDER BY id ASC LIMIT 1 OFFSET ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setInt(2, index - 1);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Функция удаления дохода по номеру
     * @param userId телеграм id пользователя
     * @param index номер дохода который нужно удалить
     * @return
     * @throws SQLException
     */

    public boolean deleteIncome(long userId, int index) throws SQLException {
        String sql = "DELETE FROM transactions WHERE id = (SELECT id FROM transactions WHERE user_id = ? AND type = 'income' ORDER BY id ASC LIMIT 1 OFFSET ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setInt(2, index - 1);
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
    public boolean editExpense(long userId, int index, double newAmount, String newName) throws SQLException {
        String sql = "UPDATE transactions SET amount = ?, name = ? WHERE id = (SELECT id FROM transactions WHERE user_id = ? AND type = 'expense' ORDER BY id ASC LIMIT 1 OFFSET ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newAmount);
            pstmt.setString(2, newName);
            pstmt.setLong(3, userId);
            pstmt.setInt(4, index - 1);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Функция изменения дохода
     * @param userId телеграм id пользователя
     * @param index номер изменяемого дохода
     * @param newAmount новая сумма дохода
     * @param newName новое наименование дохода
     * @return
     * @throws SQLException
     */
    public boolean editIncome(long userId, int index, double newAmount, String newName) throws SQLException {
        String sql = "UPDATE transactions SET amount = ?, name = ? WHERE id = (SELECT id FROM transactions WHERE user_id = ? AND type = 'income' ORDER BY id ASC LIMIT 1 OFFSET ?)";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newAmount);
            pstmt.setString(2, newName);
            pstmt.setLong(3, userId);
            pstmt.setInt(4, index - 1);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Функция вывода суммы всех трат пользователя
     * @param userId телеграм id пользователя
     * @return
     * @throws SQLException
     */
    public double getSumExpenses(long userId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND type = 'expense'";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        }
    }

    /**
     * Функция вывода суммы всех доходов пользователя
     * @param userId телеграм id пользователя
     * @return
     * @throws SQLException
     */
    public double getSumIncomes(long userId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND type = 'income'";
        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        }
    }
}