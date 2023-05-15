package ebrainsoft.week1.model;

import lombok.Getter;

@Getter
public enum Category {
    JAVA("Java"),
    JAVASCRIPT("Javascript"),
    DATABASE("Database");

    private String name;

    Category(String name) {
        this.name = name;
    }
}
