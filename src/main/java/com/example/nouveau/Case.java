package com.example.nouveau;

public class Case {
    private final int x;
    private final int y;
    private final int id;
    private static int number;
    private boolean North = true, South = true, East = true, West = true;
    private boolean visited = false;
    private int count = 0;

    public Case(int x, int y) {
        this.x = x;
        this.y = y;
        this.id = number++;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getID() { return id; }
    public boolean getNorth() { return North; }
    public boolean getSouth() { return South; }
    public boolean getWest() { return West; }
    public boolean getEast() { return East; }
    public boolean getVisited() { return visited; }
    public int getCount() { return count; }

    public void setNorth(boolean North) { this.North = North; }
    public void setSouth(boolean South) { this.South = South; }
    public void setWest(boolean West)   { this.West = West; }
    public void setEast(boolean East)   { this.East = East; }
    public void setVisited(boolean visited) { this.visited = visited; }

    public void incrementCount() { this.count++; }

    @Override
    public String toString() {
        return "Case{x=" + x + ", y=" + y + ", count=" + count + "}";
    }

    public static void resetNumber(){
        number = 0;
    }
}