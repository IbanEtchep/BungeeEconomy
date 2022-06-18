package fr.iban.bungeeeconomy.baltop;

import java.util.List;

public class Baltop {

    private List<BaltopPlayer> baltopPlayers;
    private long updatedAt;

    public Baltop(List<BaltopPlayer> baltop) {
        this.baltopPlayers = baltop;
        updatedAt = System.currentTimeMillis();
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public List<BaltopPlayer> getBaltopPlayers() {
        return baltopPlayers;
    }
}
