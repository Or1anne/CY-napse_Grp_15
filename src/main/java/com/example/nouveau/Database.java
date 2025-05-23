package com.example.nouveau;
import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Database {
    String dbName = "Sauvegarde.db";

    public Connection connectDatabase() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dbName);
    }

    public void createDatabase() {
        try (Connection conn = connectDatabase()) {
            if (conn != null) {
                System.out.println("Base SQLite créée ou déjà existante.");
            }
        } catch (SQLException e) {
            System.out.print("Erreur dans la création de la BDD : " + e.getMessage());
        }
    }

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
                        CellStmt.executeBatch();  // Exécute le batch toutes les 500 requêtes
                        CellStmt.clearBatch();    // Vide le batch après exécution (optionnel mais recommandé)
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


