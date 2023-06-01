package swosh.Controllers;

import swosh.DatabaseConnection.DatabaseConnection;
import swosh.Password;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Account extends DatabaseConnection {
    public static void createAccount(String personalNumber, String password) {
        String checkPassword = "SELECT password FROM users WHERE personalNumber = ?;";
        try(Connection conn = GetConnection()) {
            assert conn != null;
            PreparedStatement preparedStatement = conn.prepareStatement(checkPassword);
            preparedStatement.setString(1, personalNumber);
            ResultSet rs = preparedStatement.executeQuery();

            while(rs.next()){
                String hashPassword = rs.getString("password");


                if(Password.Verify(password, hashPassword)) {
                    String query = "SELECT id FROM users WHERE password = ?;";
                    preparedStatement = conn.prepareStatement(query);
                    preparedStatement.setString(1, hashPassword);

                    rs = preparedStatement.executeQuery();

                    Scanner sc = new Scanner(System.in);

                    while (rs.next()) {
                        String user_id = String.valueOf(rs.getInt("id"));

                        System.out.println("How much would you like to deposit?");
                        String amount = sc.nextLine();
                        System.out.println("Set an account number: ");
                        int accountNumber = Integer.parseInt(sc.nextLine());
                        String createAccount = "INSERT INTO account(user_id, amount, accountNumber) VALUES (?, ?, ?);";
                        PreparedStatement insertStatement = conn.prepareStatement(createAccount);
                        insertStatement.setString(1, user_id);
                        insertStatement.setString(2, amount);
                        insertStatement.setInt(3, accountNumber);

                        int i  = insertStatement.executeUpdate();

                        System.out.println("Result: " + i);
                    }
                }else{
                    System.out.println("hej");
                }
            }
        } catch (SQLException sqlException){
            System.out.println("ERROR " + sqlException);
        }
    }
    public static void deleteAccount(String personalNumber, String password, int accountNumber) throws SQLException {
        String checkPassword = "SELECT password, id FROM users WHERE personalNumber = ?";
        Connection conn = GetConnection();
        assert conn != null;
        PreparedStatement statement1 = conn.prepareStatement(checkPassword);
        statement1.setString(1, personalNumber);

        ResultSet rs = statement1.executeQuery();

        while(rs.next()) {
            String hashPassword = rs.getString("password");
            String user_id = rs.getString("id");
            System.out.println(Password.Verify(password, hashPassword));
            if (Password.Verify(password, hashPassword)) {

                String query = "DELETE FROM account WHERE accountNumber = ? AND user_id = ?;";

                try {
                    Connection connection = GetConnection();
                    assert connection != null;
                    PreparedStatement statement = connection.prepareStatement(query);

                    statement.setInt(1,accountNumber );
                    statement.setString(2, user_id);
                    statement.executeUpdate();
                    connection.close();
                    return;
                } catch (SQLException e) {
                    System.out.println("ERROR" + e);
                    return;
                }
            } else {
                Password.Verify(password, hashPassword);
                return;
            }
        }
    }
    public static void getAccInfo(String user_id) throws SQLException {
        String query = "SELECT * FROM account WHERE user_id = ?";

        Connection connection = GetConnection();
        assert connection != null;
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, user_id);
        ResultSet rs = ps.executeQuery();

        //Gå igenom resultatet med iterator för varje rad, om sant avancerar resultatet till nästa rad.
        while (rs.next()) {
            String accNum = rs.getString("accountNumber");
            String amount = rs.getString("amount");
            System.out.println("Account Number: "+ accNum + " " + " Amount: " + amount);
        }
        connection.close();
    }
    public static void getAllAcc(String user_id) throws SQLException {
        String query = "SELECT * FROM account WHERE user_id <> ?;";
        Connection connection = GetConnection();
        assert connection != null;
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, user_id);
        ResultSet rs = ps.executeQuery();

        //Gå igenom resultatet med iterator för varje rad, om sant avancerar resultatet till nästa rad.
        while (rs.next()) {
            String accNum = rs.getString("accountNumber");
            System.out.println("Account Number: "+ accNum);
        }
        connection.close();

    }

}
