package com.example.nouveau;

/**
 * Représente les quatre directions cardinales possibles dans un labyrinthe.
 * <p>
 * Cette énumération permet de manipuler les directions et de calculer les directions à droite, à gauche et opposées.
 * </p>
 */
public enum Direction{

    NORTH, EAST, SOUTH, WEST;

    /**
     * Retourne la direction obtenue en tournant à gauche à partir de la direction actuelle.
     * @return direction à gauche de la direction actuelle
     */
    public Direction turnLeft() {
        return values()[(this.ordinal() + 3) % 4]; // retrieves the direction index, adds 3 to obtain its left direction
    }

    /**
     * Retourne la direction obtenue en tournant à droite à partir de la direction actuelle.
     * @return direction à droite de la direction actuelle
     */
    public Direction turnRight() {
        return values()[(this.ordinal() + 1) % 4]; // retrieves the direction index, adds 1 to obtain its right direction
    }

    /**
     * Retourne la direction opposée de la direction actuelle.
     * @return direction opposée de la direction actuelle
     */
    public Direction opposite() {
        return values()[(this.ordinal() + 2) % 4]; // retrieves the direction index, adds 2 to obtain its opposite direction
    }
}
