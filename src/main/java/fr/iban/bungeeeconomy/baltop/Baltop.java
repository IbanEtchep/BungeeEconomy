package fr.iban.bungeeeconomy.baltop;

import java.util.List;

public class Baltop {

    private final List<BaltopPlayer> baltopPlayers;
    private final long updatedAt;

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
