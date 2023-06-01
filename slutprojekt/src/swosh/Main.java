package swosh;

import swosh.DatabaseConnection.DatabaseConnection;
import swosh.View.View;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        DatabaseConnection.CreateTable();
        View.loggIn();
    }
}