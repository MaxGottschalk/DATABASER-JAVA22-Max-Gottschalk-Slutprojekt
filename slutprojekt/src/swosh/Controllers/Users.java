package swosh.Controllers;

import swosh.DatabaseConnection.DatabaseConnection;
import swosh.Password;

import java.sql.*;



public class Users {

    public static void createUser(String name, String personalNumber, String password, String address, int phoneNumber)  {


        //Skriv din query som du vill köra mot databasen.
        String query = "INSERT INTO users (name, personalNumber, password, address, phoneNumber) VALUES (?, ?, ?, ?, ?);";

        //Skapa ett statement object för att köra SQL-querys genom databaskopplingen
       try(Connection connection = DatabaseConnection.GetConnection()) {
           assert connection != null;
           try(PreparedStatement ps = connection.prepareStatement(query)) {
               ps.setString(1, name);
               ps.setString(2, personalNumber);
               ps.setString(3, Password.Encrypt(password));
               ps.setString(4, address);
               ps.setInt(5, phoneNumber);

               //Kör SQL-query och returnera resultatet (Antalet påverkade rader returneras)
               int result = ps.executeUpdate();
               System.out.println("Result: " + result);
           }
       } catch (SQLException sqlException){
           System.out.println("ERROR" + sqlException);
       }



        //Stäng databaskoppling och returnera den till databaspoolen
    }

    public static void deleteUser(String personalNumber, String password) throws SQLException {

        String checkPassword = "SELECT password FROM users WHERE personalNumber = ?";
        Connection conn = DatabaseConnection.GetConnection();
        assert conn != null;
        PreparedStatement statement1 = conn.prepareStatement(checkPassword);
        statement1.setString(1, personalNumber);

        ResultSet rs = statement1.executeQuery();

        while(rs.next()) {
            String hashPassword = rs.getString("password");

            System.out.println(Password.Verify(password, hashPassword));
            if (Password.Verify(password, hashPassword)) {

                String usersQuery = "DELETE FROM users WHERE personalNumber = ? AND password = ?";
                String accountQuery = "DELETE FROM account WHERE user_id IN (SELECT id FROM users WHERE personalNumber = ? AND password = ?)";

                try {
                    Connection connection = DatabaseConnection.GetConnection();
                    assert connection != null;
                    PreparedStatement statement = connection.prepareStatement(accountQuery);

                    statement.setString(1, personalNumber);
                    statement.setString(2, hashPassword);
                    statement.executeUpdate();

                    statement = connection.prepareStatement(usersQuery);
                    statement.setString(1, personalNumber);
                    statement.setString(2, hashPassword);
                    statement.executeUpdate();

                    connection.close();
                    System.out.println("User Deleted");
                } catch (SQLException e) {
                    System.out.println("ERROR" + e);
                }
            }else{
                System.out.println("ERROR Wrong Password");
            }
        }
    }

    public static void updateUser(String change, String choice, String user_id) throws SQLException {

        String query = "UPDATE users SET " + change + " = ? WHERE id = ?;";
        Connection conn = DatabaseConnection.GetConnection();
        assert conn != null;
        PreparedStatement statement1 = conn.prepareStatement(query);

        if(change.equals("password")){
            statement1.setString(1, Password.Encrypt(choice));
            statement1.setString(2, user_id);

            statement1.executeUpdate();
        }else {
            statement1.setString(1, choice);
            statement1.setString(2, user_id);

            statement1.executeUpdate();
        }

        conn.close();
    }

    public static void getUserAccInfo(String user_id) throws SQLException {
        String query = "SELECT * FROM users WHERE id = ?;";
        Connection connection = DatabaseConnection.GetConnection();
        assert connection != null;
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, user_id);
        ResultSet rs = ps.executeQuery();

        //Gå igenom resultatet med iterator för varje rad, om sant avancerar resultatet till nästa rad.
        while (rs.next()) {
            String name = rs.getString("name");
            String address = rs.getString("address");
            String personalNumber = rs.getString("personalNUmber");
            String phone = rs.getString("phoneNumber");
            System.out.println("Name: " + name + " Personal number: " + personalNumber + " Phone number: " + phone + " Address: " + address);
            Account.getAccInfo(user_id);
        }
        connection.close();
    }

}
