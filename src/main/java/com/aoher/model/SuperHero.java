package com.aoher.model;

import java.util.Objects;

public final class SuperHero {

    private String firstName;
    private String lastName;
    private String heroName;

    // Empty constructor is needed for Jackson to recreate the object from JSON
    public SuperHero() {
    }

    public SuperHero(String firstName, String lastName, String heroName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.heroName = heroName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getHeroName() {
        return heroName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuperHero superHero = (SuperHero) o;
        return Objects.equals(firstName, superHero.firstName) &&
                Objects.equals(lastName, superHero.lastName) &&
                Objects.equals(heroName, superHero.heroName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, heroName);
    }
}
