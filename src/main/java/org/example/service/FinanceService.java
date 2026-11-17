package org.example.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FinanceService  {
    private static String DB_URL = "jdbc:sqlite:finance_bot.db";

    public static void setDbUrl(String testUrl) {
        DB_URL = testUrl;
    }

    public static void initialize() {
        String sql = """
            CREATE TABLE IF NOT EXISTS transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                type TEXT NOT NULL CHECK(type IN ('income', 'expense')),
                amount REAL NOT NULL,
                name TEXT NOT NULL
            );
        """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при создании таблицы", e);
        }
    }

    /**
     * /add_expense
     * @param userId
     * @param amount
     * @param name
     * @throws SQLException
     */

    public static void addExpense(long userId, double amount, String name) throws SQLException {
        String sql = "INSERT INTO transactions(user_id, type, amount, name) VALUES(?, 'expense', ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, name);
            pstmt.executeUpdate();
        }
    }

    /**
     * /add_income
     * @param userId
     * @param amount
     * @param name
     * @throws SQLException
     */

    public static void addIncome(long userId, double amount, String name) throws SQLException {
        String sql = "INSERT INTO transactions(user_id, type, amount, name) VALUES(?, 'income', ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, name);
            pstmt.executeUpdate();
        }
    }

    /**
     * /list_expenses
     * @param userId
     * @return
     * @throws SQLException
     */

    public static List<String> getExpenses(long userId) throws SQLException {
        String sql = "SELECT amount, name FROM transactions WHERE user_id = ? AND type = 'expense' ORDER BY id ASC";
        List<String> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
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
     * /list_incomes
     * @param userId
     * @return
     * @throws SQLException
     */

    public static List<String> getIncomes(long userId) throws SQLException {
        String sql = "SELECT amount, name FROM transactions WHERE user_id = ? AND type = 'income' ORDER BY id ASC";
        List<String> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
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
     * /delete_expense
     * @param userId
     * @param index
     * @return
     * @throws SQLException
     */

    public static boolean deleteExpense(long userId, int index) throws SQLException {
        String sql = "DELETE FROM transactions WHERE id = (SELECT id FROM transactions WHERE user_id = ? AND type = 'expense' ORDER BY id ASC LIMIT 1 OFFSET ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setInt(2, index - 1);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * /delete_income
     * @param userId
     * @param index
     * @return
     * @throws SQLException
     */

    public static boolean deleteIncome(long userId, int index) throws SQLException {
        String sql = "DELETE FROM transactions WHERE id = (SELECT id FROM transactions WHERE user_id = ? AND type = 'income' ORDER BY id ASC LIMIT 1 OFFSET ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setInt(2, index - 1);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * /edit_expense
     * @param userId
     * @param index
     * @param newAmount
     * @param newName
     * @return
     * @throws SQLException
     */

    public static boolean editExpense(long userId, int index, double newAmount, String newName) throws SQLException {
        String sql = "UPDATE transactions SET amount = ?, name = ? WHERE id = (SELECT id FROM transactions WHERE user_id = ? AND type = 'expense' ORDER BY id ASC LIMIT 1 OFFSET ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newAmount);
            pstmt.setString(2, newName);
            pstmt.setLong(3, userId);
            pstmt.setInt(4, index - 1);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * /edit_income
     * @param userId
     * @param index
     * @param newAmount
     * @param newName
     * @return
     * @throws SQLException
     */

    public static boolean editIncome(long userId, int index, double newAmount, String newName) throws SQLException {
        String sql = "UPDATE transactions SET amount = ?, name = ? WHERE id = (SELECT id FROM transactions WHERE user_id = ? AND type = 'income' ORDER BY id ASC LIMIT 1 OFFSET ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newAmount);
            pstmt.setString(2, newName);
            pstmt.setLong(3, userId);
            pstmt.setInt(4, index - 1);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * /sum_expenses
     * @param userId
     * @return
     * @throws SQLException
     */

    public static double getSumExpenses(long userId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND type = 'expense'";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        }
    }

    /**
     * /sum_incomes
     * @param userId
     * @return
     * @throws SQLException
     */


    public static double getSumIncomes(long userId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND type = 'income'";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        }
    }
}
