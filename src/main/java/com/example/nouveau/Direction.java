package com.example.nouveau;

public enum Direction{

    NORTH, EAST, SOUTH, WEST;

    //Pour tourner à gauche
    public Direction turnLeft() {
        return values()[(this.ordinal() + 3) % 4];
    }
    //Pour tourner à droite
    public Direction turnRight() {
        return values()[(this.ordinal() + 1) % 4];
    }

    //Avancer droit
    public Direction opposite() {
        return values()[(this.ordinal() + 2) % 4];
    }
}
