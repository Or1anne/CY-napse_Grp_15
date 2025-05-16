package com.example.nouveau;

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

    //getteur
    public int getX() { return x; }
    public int getY() { return y; }
    public int getID() { return id; }
    public boolean getNorth() { return North; }
    public boolean getSouth() { return South; }
    public boolean getWest() { return West; }
    public boolean getEast() { return East; }
    public boolean getVisited() { return !visited; }
    public int getCount() { return count; }
    public int getNumber(){ return number;}

    //setteur
    public void setNorth(boolean North) { this.North = North; }
    public void setSouth(boolean South) { this.South = South; }
    public void setWest(boolean West)   { this.West = West; }
    public void setEast(boolean East)   { this.East = East; }
    public void setVisited(boolean visited) { this.visited = visited; }
    public void setX(int x){ this.x=x;}
    public void setY(int y){ this.y=y;}
    public void setId(int id){this.id = id;}

    public void resetCount(int count){ this.count=count;}
    public void incrementCount() { this.count++; }

    public static void resetNumber() {
        number = 0;
    }
    public boolean hasWall(Direction direction) {
    switch (direction) {
        case NORTH: return North;
        case SOUTH: return South;
        case EAST:  return East;
        case WEST:  return West;
    }
    return false;
}

public void removeWall(Direction direction) {
    switch (direction) {
        case NORTH: North = false; break;
        case SOUTH: South = false; break;
        case EAST:  East  = false; break;
        case WEST:  West  = false; break;
    }
}

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
