package fr.iban.bungeeeconomy.baltop;

public class BaltopPlayer {

    private final String name;
    private final double balance;

    public BaltopPlayer(String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public String getName() {
        return name;
    }
}
