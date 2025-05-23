package com.example.nouveau;

/**
 * Represent the four possible cardinal directions in a maze.
 * <p>
 * This enumeration allows us to manipulate the directions and to calculate the left, right and opposite directions.
 * </p>
 */
public enum Direction{

    NORTH, EAST, SOUTH, WEST;

    /**
     * Return the direction obtained by turning left from the current direction.
     * @return direction to the left of the current direction
     */
    public Direction turnLeft() {
        return values()[(this.ordinal() + 3) % 4]; // retrieves the direction index, adds 3 to obtain its left direction
    }

    /**
     * Return the direction obtained by turning right from the current direction.
     * @return direction to the right of the current direction
     */
    public Direction turnRight() {
        return values()[(this.ordinal() + 1) % 4]; // retrieves the direction index, adds 1 to obtain its right direction
    }

    /**
     * Return the direction obtained by turning 180°(the opposite direction) from the current direction.
     * @return direction opposite to the current direction
     */
    public Direction opposite() {
        return values()[(this.ordinal() + 2) % 4]; // retrieves the direction index, adds 2 to obtain its opposite direction
    }
}
