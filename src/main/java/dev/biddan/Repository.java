package dev.biddan;

public class Repository {

    private final String owner;
    private final String name;

    public Repository(String owner, String name) {
        this.owner = owner;
        this.name = name;
    }

    public String getFullName() {
        return owner + "/" + name;
    }
}
