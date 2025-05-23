package com.example.nouveau;
import java.util.*;

/**
 * * The {@code Resolve} class is responsible for solving a maze.
 * <p>
 * It contains the necessary information to run through a maze of type {@link Maze},
 * including the dimensions, starting and ending points, and performance metrics.
 */
public class Resolve {
    private Case[][] Labyrinthe;
    private int width, height;
    private int nbCase =0;
    private long duration =0;
    private Case start;
    private Case end;

    /**
     * Constructor of the class {@code Resolve}.
     * <p>
     * This constructor initializes a maze resolution object using a given maze, without providing an entry or exit point.
     * It calls the main constructor with {@code null} values for entry and exit.
     * @param Labyrinthe the maze that need to be solved
     */
    public Resolve(Maze Labyrinthe) {
        this(Labyrinthe, null, null); // Call the main constructor with entry and exit null
    }

    /**
     * Main constructor of the {@code Resolve} class.
     * <p>
     * Initializes the maze grid from the given {@code Maze} object,
     * as well as the dimensions and entry/exit points.
     * If the entry or exit points are located on the edges of the maze,
     * this constructor forces the corresponding walls to be opened to allow
     * access or exit.
     * @param Labyrinthe the maze to be solved, in the form of a {@link Maze} object
     * @param entry cell representing the entry point of the maze (can be {@code null})
     * @param exit cell representing the exit point of the maze (can be {@code null})
     */
    public Resolve(Maze Labyrinthe, Case entry, Case exit) {
        this.Labyrinthe = Labyrinthe.getMaze();
        this.height = this.Labyrinthe.length;
        this.width = this.Labyrinthe[0].length;
        this.start = entry;
        this.end = exit;

        // Forcing walls open for entry/exit
        // If the entry is on the edge, we remove the wall in the corresponding direction
        if (start != null) {
            if (start.getX() == 0) start.setNorth(false);
            else if (start.getX() == height - 1) start.setSouth(false);
            else if (start.getY() == 0) start.setWest(false);
            else if (start.getY() == width - 1) start.setEast(false);
        }

        if (end != null) {
            if (end.getX() == 0) end.setNorth(false);
            else if (end.getX() == height - 1) end.setSouth(false);
            else if (end.getY() == 0) end.setWest(false);
            else if (end.getY() == width - 1) end.setEast(false);
        }
    }

    /**
     * Reset all the cells of the maze.
     * <p>
     * This method resets the counters of each cell and marks all cells as unvisited
     * by setting their {@code visited} attribute to {@code false}.
     * <p>
     * This method is called at the beginning of each algorithm to ensure that
     * the maze is in a clean state before starting the resolution.
     * <p>
     */
    private void resetCounts() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Labyrinthe[i][j].resetCount(0);
                Labyrinthe[i][j].setVisited(false);
            }
        }
    }

    /**
     * Resolves the maze using the Trémaux algorithm.
     * <p>
     * This method applies a modified version of the Trémaux algorithm to explore the maze.
     * It resets the cells, recursively explores from the starting cell, and then filters
     * the paths that have been explored twice, which corresponds to dead ends or unnecessary detours.
     * <p>
     * The execution time is measured in nanoseconds and stored via {@code setDuration}.
     * 
     * @return A list of {@link Case} representing the solution path between the entrance and exit of the maze,
     *         or {@code null} if no path was found.
     */
    public List<Case> Tremaux(){
        long startTIme = System.nanoTime();
        resetCounts();
        List<Case> path = new ArrayList<>();
        boolean found = explore(start.getX(), start.getY(), path);
        if (!found) return null;
        path.removeIf(c ->(c.getCount()==2));
        long endTime = System.nanoTime();
        setDuration(endTime-startTIme);
        return path;
    }

    /**
     * Recursive method used in the Trémaux algorithm to explore the maze.
     * <p>
     * This method recursively visits the accessible cells from the given position.
     * It marks the visited cells, counts the passages, and attempts to build a path to the exit.
     * The possible directions are shuffled to avoid a systematic route and simulate a random behavior.
     * <p>
     * A cell is explored if it has already been visited but has not yet been explored twice.
     * @param x The vertical coordinate (row) of the current cell.
     * @param y The horizontal coordinate (column) of the current cell.
     * @param path The list of covered cells constituting the current path.
     * @return {@code true} if the exit has been reached from this position, otherwise {@code false}.
     */
    private boolean explore(int x, int y, List<Case> path) {
        Case current = Labyrinthe[x][y];
        current.incrementCount();
        path.add(current);
        addNbCase();


        if (Labyrinthe[x][y] == end){
            System.out.println("Nb Case visité:" + nbCase);
            return true;
        }

        List<int[]> directions = new ArrayList<>();

        // Add possible directions to the list
        if (!current.getNorth() && x > 0) directions.add(new int[]{x - 1, y});
        if (!current.getSouth() && x < height - 1) directions.add(new int[]{x + 1, y});
        if (!current.getWest() && y > 0) directions.add(new int[]{x, y - 1});
        if (!current.getEast() && y < width - 1) directions.add(new int[]{x, y + 1});

        Collections.shuffle(directions); // Shuffle the directions to avoid systematic exploration

        for (int[] dir : directions) { 
            // For each possible direction
            // Check if the next cell is already visited and has been explored less than twice
            // If so, we can explore it
            Case next = Labyrinthe[dir[0]][dir[1]];

            if (next.getVisited() && next.getCount() < 2) {
                current.setVisited(true);
                if (explore(dir[0], dir[1], path)) {
                    return true;
                }
            }
        }
        current.incrementCount();
        return false;
    }


    /**
     * Algorithme of resolution of the maze : "Hand on Wall"
     * This method consists of continuously following the wall on the left until reaching the exit.
     * It is a simple and intuitive algorithm, but it does not guarantee the shortest path.
     * The algorithm starts from the entry point and explores the maze 
     * The algorithm continues until it reaches the exit or until a maximum number of steps is reached to avoid infinite loops.
     * The execution time is measured in nanoseconds and stored via {@code setDuration}.
     * The method returns a list of cells representing the path taken to reach the exit.
     */
    public List<Case> HandOnWall() {
        resetCounts();
        long startTime = System.nanoTime();
        int x = start.getX();
        int y = start.getY();
        Case current = Labyrinthe[x][y];

        // Initialise the direction to the left because the entry is always on the left edge of the maze
        Direction dir = Direction.EAST;
        List<Case> path = new ArrayList<>();
        current.setVisited(true);
        path.add(current);
        int steps = 0;
        int maxSteps = width * height * 4;


        // Condition to get out of the loop if the maze does not have an solution
        while (Labyrinthe[x][y] != end) {
            if (steps++ > maxSteps) {
                System.out.println("Labyrinthe insoluble (boucle infinie détectée)");
                return null;
            }

            addNbCase();
            Direction left = dir.turnLeft();

            // Test if we can go left
            if (canMove(x, y, left)) {
                dir = left;
                int[] newPos = move(x, y, dir);
                System.out.println(Arrays.toString(newPos)); // Debug
                x = newPos[0];
                y = newPos[1];
            }
            // Else test in front
            else if (canMove(x, y, dir)) {
                int[] newPos = move(x, y, dir);
                System.out.println(Arrays.toString(newPos)); // Debug
                x = newPos[0];
                y = newPos[1];
            }
            // Else test right
            else if (canMove(x, y, dir.turnRight())) {
                dir = dir.turnRight();
                int[] newPos = move(x, y, dir);
                System.out.println(Arrays.toString(newPos)); // Debug
                x = newPos[0];
                y = newPos[1];
            }
            // Else go back
            else {
                dir = dir.opposite();
                int[] newPos = move(x, y, dir);
                System.out.println(Arrays.toString(newPos)); // Debug
                x = newPos[0];
                y = newPos[1];
            }

            // If the cell is not visited, mark it as visited and add it to the path
            if (Labyrinthe[x][y].getVisited()) {
                Labyrinthe[x][y].setVisited(true);
                path.add(Labyrinthe[x][y]);
            }
        }
        long endTime = System.nanoTime();
        setDuration(endTime - startTime);

        return path;
    }


    /**
     * Verify if a move is possible from a given cell in a specific direction.
     * @param x The vertical coordinate (row) of the current cell in the maze.
     * @param y The horizontal coordinate (column) of the current cell in the maze.
     * @param dir The direction in which we want to move (NORTH, EAST, SOUTH or WEST).
     * @return true if the move is possible, if there is no wall and the target cell is within the maze limits, false otherwise.
     */
    public boolean canMove(int x, int y, Direction dir){
        Case c = Labyrinthe[x][y];
        return switch (dir) {
            case NORTH -> !c.getNorth() && x > 0;
            case EAST  -> !c.getEast() && y < width - 1;
            case SOUTH -> !c.getSouth() && x < height - 1;
            case WEST  -> !c.getWest() && y > 0;
        };
    }


    /**
     * Calculates the new position after a move from a given position in a specified direction.
     * @param x The vertical coordinate (row) of the current cell in the maze.
     * @param y The horizontal coordinate (column) of the current cell in the maze.
     * @param dir The direction in which to move (NORTH, EAST, SOUTH or WEST).
     * @return An array of integers of size 2 containing the new position after the move: [newX, newY].
     */
    public int[] move(int x, int y, Direction dir){
        return switch (dir) {
            case NORTH -> new int[] {x - 1, y};
            case EAST  -> new int[] {x, y + 1};
            case SOUTH -> new int[] {x + 1, y};
            case WEST  -> new int[] {x, y - 1};
        };
    }


    /**
     * Resolves the maze using the breadth-first search (BFS) algorithm.
     * <p>
     * This algorithm guarantees to find the shortest path (in terms of number of cells)
     * between the entry {@code start} and the exit {@code end} in the unweighted maze.
     * <p>
     * The route is performed by exploring the accessible neighbors of each cell iteratively,
     * recording the parent of each visited cell to allow path reconstruction once the destination is reached.
     * @return A list of {@code Case} representing the solution path from the entry to the exit.
     *        If no path exists, returns an empty list.
     */
    public List<Case> BFS() {
        resetCounts();
        Queue<Case> queue = new LinkedList<>(); // Queue for BFS
        Map<Case, Case> parentMap = new HashMap<>(); // Map to keep track of the parent of each cell
        Set<Case> visited = new HashSet<>(); // Set to keep track of visited cells

        queue.add(start);
        visited.add(start);
        parentMap.put(start, null);

        while (!queue.isEmpty()) {
            Case current = queue.poll();

            // If we reach the end, we reconstruct the path
            // using the parent map
            if (current == end) {
                return reconstructPath(parentMap, end);
            }

            // We explore the accessible neighbors of the current cell
            // and add them to the queue if they haven't been visited yet
            // We also mark them as visited and set their parent
            // to the current cell
            for (Case neighbor : getAccessibleNeighbors(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        return Collections.emptyList(); // No path found
    }

    // We reconstruct the path from the parents
    /**
     * Reconstructs the path from the entry to the exit using the parent map.
     * <p>
     * This method traces the path from the end cell {@code end} back to the entry
     * by following the references contained in {@code parentMap}, then reverses the list
     * to obtain the correct order (from entry to exit).
     * @param parentMap A map containing, for each visited cell, the cell from which it came.
     * @param end The destination cell (end of the maze).
     * @return An ordered list of {@code Case} representing the solution path.
     */
    private List<Case> reconstructPath(Map<Case, Case> parentMap, Case end) {
        List<Case> path = new ArrayList<>();
        Case current = end;

        while (current != null) {
            path.add(current);
            current = parentMap.get(current);
        }

        Collections.reverse(path);
        return path;
    }

    // Neighbors accessible from a cell
    /**
     * Returns the list of accessible neighbors from a given cell.
     * <p>
     * This method examines the walls of the current cell to determine
     * the directions in which a move is possible (i.e., where there are no walls).
     * It returns all the neighboring cells that can be reached directly from the current cell.
     * @param current The current cell from which we want to know the accessible neighbors.
     * @return A list of {@code Case} representing the accessible neighboring cells.
     */

    private List<Case> getAccessibleNeighbors(Case current) {
        List<Case> neighbors = new ArrayList<>();
        int x = current.getX();
        int y = current.getY();

        // Neighborg of the top
        if (!current.getNorth() && x > 0) {
            neighbors.add(Labyrinthe[x-1][y]);
        }
        // Neighborg of the bottom
        if (!current.getSouth() && x < height - 1) {
            neighbors.add(Labyrinthe[x+1][y]);
        }
        // Neighborg of the left
        if (!current.getWest() && y > 0) {
            neighbors.add(Labyrinthe[x][y-1]);
        }
        // Neighborg of the right
        if (!current.getEast() && y < width - 1) {
            neighbors.add(Labyrinthe[x][y+1]);
        }

        return neighbors;
    }


    /**
     * Return the number of visited cells during the maze resolution.
     * 
     * @return The number of visited cells.
     */
    public int getNbCase() { return nbCase; }


    /**
     * Return the duration of the maze resolution in nanoseconds.
     * 
     * @return The duration in nanoseconds.
     */
    public long getDuration() { return duration; }


    /**
     * Increments the number of visited cells by 1.
     */
    public void addNbCase(){ this.nbCase++; }


    /**
     * Define the number of visited cells.
     * 
     * @param nbCase The new number of visited cells.
     */
    public void setNbCase(int nbCase) { this.nbCase = nbCase; }


    /**
     * Define the duration of the maze resolution.
     * 
     * @param duration The duration in nanoseconds.
     */
    public void setDuration(long duration) { this.duration = duration; }

}