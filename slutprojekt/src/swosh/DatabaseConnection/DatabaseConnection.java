package swosh.DatabaseConnection;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    static MysqlDataSource dataSource;
    static String url = "localhost";
    static int port = 3308;
    static String database = "swosh";
    static String username = "root";
    static String password = "";

    public static void InitializeDatabase() {
        try {
            System.out.println("Configuring data source...");
            dataSource = new MysqlDataSource();
            dataSource.setUser(username);
            dataSource.setPassword(password);
            dataSource.setUrl("jdbc:mysql://" + url + ":" + port + "/" + database + "?serverTimezone=UTC");
            dataSource.setUseSSL(false);
            System.out.println("done!\n");
        } catch (SQLException e) {
            System.out.println("failed!\n");
            //PrintSQLException(e);
            System.exit(0);
        }
    }

    //Skapar en tillfällig koppling till databasen
    public static Connection GetConnection() {
        if(dataSource == null){
            InitializeDatabase();
        }

        try {
            Connection connection = dataSource.getConnection();
            return connection;
        }catch (SQLException e){
        }
        return null;
    }

    public static void  CreateTable() throws SQLException {
        Connection conn = GetConnection();

        assert conn != null;
        Statement state = conn.createStatement();

        String users = "CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(50), password VARCHAR(256),  personalNumber VARCHAR(12), UNIQUE (personalNumber), created TIMESTAMP default CURRENT_TIMESTAMP, address VARCHAR(100), phoneNumber INT, UNIQUE (phoneNumber));";
        String account = "CREATE TABLE IF NOT EXISTS account (id INT PRIMARY KEY AUTO_INCREMENT, accountNumber INT, UNIQUE (accountNumber), user_id INT, amount int, created TIMESTAMP default CURRENT_TIMESTAMP);";
        String transaction = "CREATE TABLE IF NOT EXISTS transaction (id INT PRIMARY KEY AUTO_INCREMENT, user_id INT, accountNumber INT, receiver_id INT, created TIMESTAMP default CURRENT_TIMESTAMP, receiver INT, amount INT);";

        int usersResult = state.executeUpdate(users);
        int accountResult = state.executeUpdate(account);
        int transactionResult = state.executeUpdate(transaction);

        System.out.println("USERS: " + usersResult + " ACCOUNT: " + accountResult + " TRANSACTION: " + transactionResult);

        conn.close();
    }
}

