package swosh.View;

import swosh.DatabaseConnection.DatabaseConnection;
import swosh.Password;
import swosh.Controllers.Account;
import swosh.Controllers.Transaction;
import swosh.Controllers.Users;

import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

public class View {
    public static void loggIn() throws SQLException {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.println("1. Sign Up");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            switch (scan.nextLine()) {
                case "1" -> {
                    System.out.println("Enter your name");
                    String name = scan.nextLine();
                    System.out.println("Enter your personal number");
                    String personalNumber = scan.nextLine();
                    System.out.println("Enter your password");
                    String password = scan.nextLine();
                    System.out.println("Enter your address");
                    String address = scan.nextLine();
                    System.out.println("Enter your phone number");
                    int phoneNumber = Integer.parseInt(scan.nextLine());
                    Users.createUser(name, personalNumber, password, address, phoneNumber);
                }
                case "2" -> {
                    System.out.println("Enter you personal number");
                    String personalNumber = scan.nextLine();
                    System.out.println("Enter you password");
                    String password = scan.nextLine();
                    loggedInMenu(personalNumber, password);
                }
                case "3" -> {
                    return;
                }
            }
        }
    }

    public static void loggedInMenu(String personalNumber, String password) throws SQLException {

        String checkPassword = "SELECT password FROM users WHERE personalNumber = ?;";
        try (Connection conn = DatabaseConnection.GetConnection()) {
            assert conn != null;
            PreparedStatement statement1 = conn.prepareStatement(checkPassword);
            statement1.setString(1, personalNumber);
            ResultSet rs = statement1.executeQuery();

            while (rs.next()) {

                String hashPassword = rs.getNString("password");

                if (Password.Verify(password, hashPassword)) {
                    String checkUser = "SELECT id FROM users WHERE password = ?;";

                    statement1 = conn.prepareStatement(checkUser);
                    statement1.setString(1, hashPassword);

                    rs = statement1.executeQuery();
                    Scanner sc = new Scanner(System.in);

                    while(rs.next()) {
                        String user_id = String.valueOf(rs.getInt("id"));

                        while (true) {
                            System.out.println();
                            System.out.println("Choose:");
                            System.out.println("1.Create Account");
                            System.out.println("2.Make a transaction");
                            System.out.println("3.Show transactions");
                            System.out.println("4.Show accounts");
                            System.out.println("5.Show user information");
                            System.out.println("6.Update information");
                            System.out.println("7.Delete user account");
                            System.out.println("8.Delete bank account");
                            System.out.println("9.Exit");

                            switch (sc.nextLine()) {
                                case "1" -> {
                                    Account.createAccount(personalNumber, password);
                                    System.out.println("Press Enter");
                                    System.in.read();
                                }case "2" -> {
                                    System.out.println("Who would you like to send money to?");
                                    Account.getAllAcc(user_id);
                                    int receiver = Integer.parseInt(sc.nextLine());
                                    System.out.println("From what account?");
                                    Account.getAccInfo(user_id);
                                    int accountNumber = Integer.parseInt(sc.nextLine());
                                    System.out.println("How much would you like to send?");
                                    int amount = Integer.parseInt(sc.nextLine());
                                    Transaction.sendMoney(receiver, amount, user_id, accountNumber);
                                    System.out.println("Press Enter");
                                    System.in.read();
                                }case "3" -> {
                                    System.out.println("Select amount of days from today:");
                                    int startDay = Integer.parseInt(sc.nextLine());
                                    Transaction.getTransactions(user_id, startDay);
                                    System.out.println("Press Enter");
                                    System.in.read();
                                }case "4" -> {
                                    Account.getAccInfo(user_id);
                                    System.out.println("Press Enter");
                                    System.in.read();
                                }case"5"->{
                                    Users.getUserAccInfo(user_id);
                                    System.out.println("Press Enter");
                                    System.in.read();
                                }case"6" ->{
                                    System.out.println("Update information..");
                                    System.out.println("1.Address");
                                    System.out.println("2.Phone number");
                                    System.out.println("3.Password");
                                    switch(sc.nextLine()){
                                        case "1"-> {
                                            System.out.println("Enter new address");
                                            String choice = sc.nextLine();
                                            Users.updateUser("address", choice, user_id);
                                        }case  "2"->{
                                            System.out.println("Enter new phone number");
                                            String choice = sc.nextLine();
                                            Users.updateUser("phoneNumber", choice, user_id);
                                        }case"3"->{
                                            System.out.println("Enter new password");
                                            String choice = sc.nextLine();
                                            Users.updateUser("password", choice, user_id);
                                        }
                                    }
                                }
                                case "7" -> {
                                    System.out.println("Confirm your personal number");
                                    String confirmPersonalNum = sc.nextLine();
                                    System.out.println("Confirm your password");
                                    String  confirmPass = sc.nextLine();
                                    Users.deleteUser(confirmPersonalNum, confirmPass);
                                    System.out.println("Press Enter");
                                    System.in.read();
                                    return;
                                }case"8" ->{
                                    System.out.println("Confirm your personal number");
                                    String confirmPersonalNum = sc.nextLine();
                                    System.out.println("Confirm your password");
                                    String confirmPass = sc.nextLine();
                                    System.out.println("Confirm account number");
                                    Account.getAccInfo(user_id);
                                    int accountNumber = Integer.parseInt(sc.nextLine());
                                    Account.deleteAccount(confirmPersonalNum, confirmPass, accountNumber);
                                    System.out.println("Press Enter");
                                    System.in.read();
                                }case "9" -> {
                                    return;
                                }
                            }
                        }
                    }
                }else if(!Password.Verify(password, hashPassword)){
                    System.out.println("Wrong Password");
                    System.out.println("Press Enter");
                    System.in.read();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
