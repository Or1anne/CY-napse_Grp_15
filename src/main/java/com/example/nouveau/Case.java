package com.example.nouveau;

/**
 * The class {@code Case} represents a cell in a maze.
 * Each cell has walls (north, east, south, west) that can be present or absent,
 * as well as information about its position, unique identifier, and visit status.
 */
public class Case {
    private int count = 0;
    private int id;
    private static int number;
    private boolean North;
    private boolean South;
    private boolean East;
    private boolean West;
    private boolean visited;
    private int x;
    private int y;


    /**
     * Constructor for a cell at the given position. All walls are initially present.
     * @param x Vertical coordinate of the cell.
     * @param y Horizontal coordinate of the cell.
     */
    public Case(int x, int y){
        id = number++;
        North = true;
        South = true;
        East = true;
        West = true;
        this.x = x;
        this.y = y;
        visited = false;
    }

    //Getters
    /** @return The vertical coordinate (line) of the cell*/
    public int getX() { return x; }

    /** @return The horizontal coordinate (column) of the cell. */
    public int getY() { return y; }

    /** @return The unique id of the cell. */
    public int getID() { return id; }

    /** @return True if the wall at the North is present. */
    public boolean getNorth() { return North; } 

    /** @return True if the wall at the South is present. */
    public boolean getSouth() { return South; }

    /** @return True if the wall at the West is present. */
    public boolean getWest() { return West; }

    /** @return True if the wall at the East if present. */
    public boolean getEast() { return East; }

    /** @return False if the cell has already been visited. */
    public boolean getVisited() { return !visited; }

    /** @return The number of time the cell has been visited in some algorithmes. */
    public int getCount() { return count; }

    /** @return The total number of created cells (static). */
    public int getNumber(){ return number; }


    //Setters
    public void setNorth(boolean North) { this.North = North; }
    public void setSouth(boolean South) { this.South = South; }
    public void setWest(boolean West)   { this.West = West; }
    public void setEast(boolean East)   { this.East = East; }

    /**
     * Define the visit status of the cell.
     * @param visited True if the cell should be considered as unvisited.
     */
    public void setVisited(boolean visited) { this.visited = visited; }

    public void setX(int x){ this.x=x;}
    public void setY(int y){ this.y=y;}
    public void setId(int id){this.id = id;}

    /**
     * Renitialise the visit counter of the cell.
     * @param count New value of the counter.
     */
    public void resetCount(int count){ this.count=count;}

    /** Increment the visit counter of the cell. */
    public void incrementCount() { this.count++; }

    /**Reset the global counter of created cells. */ 
    public static void resetNumber() {
        number = 0;
    }

    /**
     * Verify if a wall is present in the given direction.
     * @param direction The direction to check.
     * @return True if a wall is present, false otherwise.
     */
    public boolean hasWall(Direction direction) {
    switch (direction) {
        case NORTH: return North;
        case SOUTH: return South;
        case EAST:  return East;
        case WEST:  return West;
    }
    return false;
}

    /**
     * Delete the wall in the given direction.
     * @param direction Direction in which to remove the wall.
     */
public void removeWall(Direction direction) {
    switch (direction) {
        case NORTH: North = false; break;
        case SOUTH: South = false; break;
        case EAST:  East  = false; break;
        case WEST:  West  = false; break;
    }
}

    /**
     * Add a wall in the given direction.
     * @param direction Direction in which to add the wall.
     */
public void addWall(Direction direction) {
    switch (direction) {
        case NORTH: North = true; break;
        case SOUTH: South = true; break;
        case EAST:  East  = true; break;
        case WEST:  West  = true; break;
    }
}

    @Override
    public String toString() {
        return "Case{x=" + x + ", y=" + y + ", count=" + count + "}";
    }
}
