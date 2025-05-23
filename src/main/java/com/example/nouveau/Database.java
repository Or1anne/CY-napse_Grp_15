package com.example.nouveau;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * The class {@code Database} manages the persistence of mazes in an SQLite database.
 * It allows the creation of the database and associated tables,the save of a maze, the loading of this later,
 * and the retrieval of the list of saved mazes.
 */
public class Database {
    String dbName = "Sauvegarde.db";

    /**
     * Establish a connection to the SQLite database.
     *
     * @return a {@link Connection} instance connected to the database.
     * @throws SQLException if a connection error occurs.
     */
    public Connection connectDatabase() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dbName);
    }

    /**
     * Creates the SQLite database if it does not exist.
     * If the database already exists, it does nothing.
     * Prints a message to the console indicating success or failure.
     */
    public void createDatabase() {
        try (Connection conn = connectDatabase()) {
            if (conn != null) {
                System.out.println("Base SQLite créée ou déjà existante.");
            }
        } catch (SQLException e) {
            System.out.print("Erreur dans la création de la BDD : " + e.getMessage());
        }
    }

    /**
     * Creates the tables {@code Maze} and {@code Cell} in the database if they do not exist.
     */
    public void createTable() {
        try (Connection conn = connectDatabase()) {
            Statement stmt = conn.createStatement();

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Maze (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT UNIQUE,
                    height INTEGER,
                    width INTEGER
                );
            """);

            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Cell (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    maze_id INTEGER,
                    cell_id INTEGER,
                    x INTEGER,
                    y INTEGER,
                    North INTEGER,
                    South INTEGER,
                    West INTEGER,
                    East INTEGER,
                    FOREIGN KEY (maze_id) REFERENCES Maze(id) ON DELETE CASCADE
                );
            """);

            stmt.close();
        } catch (SQLException e) {
            System.out.println("Erreur dans la création des tables : " + e.getMessage());
        }
    }

    /**
     * Save a maze in the database.
     *
     * @param labyrinth the maze to save.
     * @param Name the name associated with the maze.
     */
    public void SaveMaze(Maze labyrinth, String Name) {
        try (Connection conn = connectDatabase()) {
            conn.setAutoCommit(false);
            PreparedStatement MazeStmt = conn.prepareStatement(
                    "INSERT INTO Maze (name, height, width) VALUES (?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            MazeStmt.setString(1, Name);
            MazeStmt.setInt(2, labyrinth.getHeight());
            MazeStmt.setInt(3, labyrinth.getWidth());
            MazeStmt.executeUpdate();

            ResultSet rs = MazeStmt.getGeneratedKeys();
            int mazeId = rs.next() ? rs.getInt(1) : -1;
            MazeStmt.close();

            PreparedStatement CellStmt = conn.prepareStatement(
                    "INSERT INTO Cell (maze_id, cell_id, x, y, North, South, West, East) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            int batchSize = 500;
            int count = 0;

            for (int i = 0; i < labyrinth.getHeight(); i++) {
                for (int j = 0; j < labyrinth.getWidth(); j++) {
                    Case cell = labyrinth.getMaze()[i][j];
                    CellStmt.setInt(1, mazeId);
                    CellStmt.setInt(2, cell.getID());
                    CellStmt.setInt(3, j);
                    CellStmt.setInt(4, i);
                    CellStmt.setBoolean(5, cell.getNorth());
                    CellStmt.setBoolean(6, cell.getSouth());
                    CellStmt.setBoolean(7, cell.getWest());
                    CellStmt.setBoolean(8, cell.getEast());
                    CellStmt.addBatch();

                    count++;
                    if (count % batchSize == 0) {
                        CellStmt.executeBatch();  // Execute the batch every 500 requests
                        CellStmt.clearBatch();    // Clear the batch after execution (optionnal but recommended) 
                    }
                }
            }
            CellStmt.executeBatch();
            CellStmt.clearBatch();
            CellStmt.close();

            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la sauvegarde du labyrinthe : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Charge a maze from the database using its name.
     *
     * @param Name the name of the maze to load.
     * @return the corresponding maze, or {@code null} if it does not exist.
     */
    public Maze DataChargeMaze(String Name) {
        Maze labyrinth = null;
        try (Connection conn = connectDatabase()) {
            PreparedStatement stmtCollect = conn.prepareStatement("SELECT * FROM Maze WHERE name = ?");
            stmtCollect.setString(1, Name);
            ResultSet rs = stmtCollect.executeQuery();
            if (rs.next()) {
                int width = rs.getInt("width");
                int height = rs.getInt("height");
                int id = rs.getInt("id");
                labyrinth = new Maze(width, height);

                PreparedStatement stmtCell = conn.prepareStatement("SELECT * FROM Cell WHERE maze_id = ?");
                stmtCell.setInt(1, id);
                ResultSet rsCell = stmtCell.executeQuery();
                while (rsCell.next()) {
                    int x = rsCell.getInt("x");
                    int y = rsCell.getInt("y");
                    labyrinth.getMaze()[y][x].setId(rsCell.getInt("cell_id"));
                    labyrinth.getMaze()[y][x].setX(y);
                    labyrinth.getMaze()[y][x].setY(x);
                    labyrinth.getMaze()[y][x].setNorth(rsCell.getInt("North") == 1);
                    labyrinth.getMaze()[y][x].setSouth(rsCell.getInt("South") == 1);
                    labyrinth.getMaze()[y][x].setWest(rsCell.getInt("West") == 1);
                    labyrinth.getMaze()[y][x].setEast(rsCell.getInt("East") == 1);
                }
                stmtCell.close();
            } else {
                System.out.println("Aucun labyrinthe trouvé");
            }
            stmtCollect.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la BDD : " + e.getMessage());
            e.printStackTrace();
        }
        return labyrinth;
    }

    public void DeleteMaze(String Name) {
        try (Connection conn = connectDatabase()){
            PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM Maze WHERE name = ?");
            deleteStmt.setString(1, Name);
            deleteStmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Retrieves the list of names of all mazes saved in the database.
     *
     * @return a list containing the names of the mazes.
     */
    public ObservableList<String> getMazeList() {
        ObservableList<String> SavedMaze = FXCollections.observableArrayList();
        try (Connection conn = connectDatabase()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM Maze");
            while (rs.next()) {
                SavedMaze.add(rs.getString("name"));
            }
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération de la liste des labyrinthes : " + e.getMessage());
        }
        return SavedMaze;
    }
}


