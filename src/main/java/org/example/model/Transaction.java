package org.example.model;


/**
 * Класс, представляющий финансовую запись. Содержит информацию о типе операции, сумме и названию.
 */
public class Transaction {
    private long userId;
    private final TransactionType type;
    private double sum;
    private String name;

    public Transaction(TransactionType type, double sum, String name) {
        this.type = type;
        this.sum = sum;
        this.name = name;
    }

    public Transaction(long userId, TransactionType type, double sum, String name) {
        this.userId = userId;
        this.type = type;
        this.sum = sum;
        this.name = name;
    }

    public long getId() {
        return userId;
    }

    public TransactionType getType() {
        return type;
    }

    public double getSum() {
        return sum;
    }

    public String getName() {
        return name;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Возвращает строковое представление транзакции для отладки.
     *
     * @return отформатированная строка с полями транзакции
     */
    @Override
    public String toString() {
        return "Transaction{" +
                "type=" + type +
                ", sum=" + sum +
                ", name='" + name + '\'' +
                '}';
    }
}
