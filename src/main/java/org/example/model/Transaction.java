package org.example.model;

public class Transaction {
    private final TransactionType type;
    private double sum;
    private String name;

    public Transaction(TransactionType type, double sum, String name) {
        this.type = type;
        this.sum = sum;
        this.name = name;
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

    @Override
    public String toString() {
        return "Transaction{" +
                "type=" + type +
                ", sum=" + sum +
                ", name='" + name + '\'' +
                '}';
    }
}
