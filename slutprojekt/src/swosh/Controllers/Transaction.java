package swosh.Controllers;

import swosh.DatabaseConnection.DatabaseConnection;

import java.sql.*;
import java.util.Calendar;

public class Transaction extends DatabaseConnection {

    public static void sendMoney(int receiver, int amount, String user_id, int accountNumber) throws SQLException {

        Connection conn = GetConnection();
        assert conn != null;

        String getAmount = "SELECT amount, id FROM account WHERE user_id = ? AND accountNumber = ?;";
        PreparedStatement ps = conn.prepareStatement(getAmount);
        ps.setString(1, user_id);
        ps.setInt(2, accountNumber);

        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            int amountInAccount = rs.getInt("amount");
            int id = rs.getInt("id");

            String getReceiverAmount = "SELECT amount, user_id FROM account WHERE accountNumber = ?;";
            ps = conn.prepareStatement(getReceiverAmount);
            ps.setInt(1, receiver);

            rs = ps.executeQuery();
            while (rs.next()) {
                int newAmountAtReceiver = rs.getInt("amount");
                int receiver_id = rs.getInt("user_id");

                if (amountInAccount > 0 && amountInAccount >= amount) {
                    amountInAccount -= amount;
                    newAmountAtReceiver += amount;
                    String updateAccount = "UPDATE account SET amount = ? WHERE user_id = ? AND id = ?;";

                    ps = conn.prepareStatement(updateAccount);
                    ps.setInt(1, amountInAccount);
                    ps.setString(2, user_id);
                    ps.setInt(3, id);
                    ps.executeUpdate();

                    String updateReceiver = "UPDATE account SET amount = ? WHERE accountNumber = ?;";

                    ps = conn.prepareStatement(updateReceiver);
                    ps.setInt(1, newAmountAtReceiver);
                    ps.setInt(2, receiver);
                    ps.executeUpdate();

                    String sendAmount = "INSERT INTO transaction (receiver, amount, user_id, accountNumber, receiver_id) VALUES (?, ?, ?, ?, ?);";

                    ps = conn.prepareStatement(sendAmount);
                    ps.setInt(1, receiver);
                    ps.setInt(2, amount);
                    ps.setString(3, user_id);
                    ps.setInt(4, accountNumber);
                    ps.setInt(5, receiver_id);

                    ps.executeUpdate();
                    System.out.println("Transaction success");
                }else{
                    System.out.println("Transaction failed..");
                }
            }
        }
    }

    public static void getTransactions(String user_id, int days) throws SQLException {
        String query =  "SELECT * FROM transaction WHERE (user_id = ? OR receiver_id = ?) AND created >= NOW() - INTERVAL ? DAY AND created <= NOW() ORDER BY created ASC;";

        Connection connection = GetConnection();
        assert connection != null;
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, user_id);
        ps.setString(2, user_id);
        ps.setInt(3, days);
       // ps.setDate(4, endDay);
        ResultSet rs = ps.executeQuery();

        //Gå igenom resultatet med iterator för varje rad, om sant avancerar resultatet till nästa rad.
        while (rs.next()) {
            String created = rs.getString("created");
            String sender = rs.getString("accountNumber");
            String receiver = rs.getString("receiver");
            String amount = rs.getString("amount");
            System.out.println("Created: " + created + " " + "Sender: " + sender + " Receiver: " + receiver + " Amount: " + amount);
        }
        connection.close();
    }
}
