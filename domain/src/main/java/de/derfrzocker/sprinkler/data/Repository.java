package de.derfrzocker.sprinkler.data;

public enum Repository {

    BUKKIT("Bukkit"),
    CRAFTBUKKIT("CraftBukkit"),
    SPIGOT("Spigot");

    private final String fancyName;

    Repository(String fancyName) {
        this.fancyName = fancyName;
    }

    public String getFancyName() {
        return fancyName;
    }
}
