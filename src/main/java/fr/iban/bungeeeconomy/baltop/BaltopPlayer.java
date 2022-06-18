package fr.iban.bungeeeconomy.baltop;

public class BaltopPlayer {

    private String name;
    private double balance;

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
