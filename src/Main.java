import java.sql.*;
import java.util.Scanner;

public class Main {

    private static String myUrl = "jdbc:postgresql://localhost:5432/highscore";
    private static Connection connection;
    private static boolean running=true;
    private static String firstName;
    private static String lastName;

    public static void main(String[] args) {
        connectToDatabase();

        while (running) {
            handleLogin();
            if (running) {
                displayUserLogin();
                playGame();
                handleMainMenuChoice();
            }
        }

        disconnectFromDatabase();
    }


    private static void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(myUrl);
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database: " + e.getMessage());
        }
    }

    private static void disconnectFromDatabase() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected from the database.");
            }
        } catch (SQLException e) {
            System.out.println("Failed to disconnect from the database: " + e.getMessage());
        }
    }


    private static void handleLogin() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("-----Login-----");
        System.out.println("1. Login");
        System.out.println("2. Quit");
        System.out.print("Enter your choice: ");

        while (!scanner.hasNextInt()) {
            System.out.println("Invalid choice. Please enter a valid option");
            System.out.print("Enter your choice: ");
            scanner.next();
        }
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                break;
            case 2:
                System.out.println("Quitting the program.");
                running = false;
                break;
            default:
                System.out.println("Invalid choice. Please enter a valid option");
                break;
        }
    }
    private static void displayUserLogin(){
        User user = new User();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your first name: ");
        firstName = scanner.nextLine();
        System.out.print("Enter your last name: ");
        lastName = scanner.nextLine();

        user.setFirstName(firstName);
        user.setLastName(lastName);

    }



    private static void displayMenu() {
        System.out.println("-----Main Menu-----");
        System.out.println("1. Play Game");
        System.out.println("2. View Poker Hands Table");
        System.out.println("3. View Leaderboard Table");
        System.out.println("4. Logout");
        System.out.print("Enter one of the given numbers: ");
    }





    private static void handleMainMenuChoice() {
        while (running) {
            displayMenu();

            Scanner scanner = new Scanner(System.in);

            while (!scanner.hasNextInt()) {
                System.out.println("Invalid choice. Please enter a valid option");
                System.out.print("Enter one of the given numbers: ");
                scanner.next();
            }
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    playGame();
                    break;
                case 2:
                    viewPokerHandsTable();
                    break;
                case 3:
                    viewLeaderboardTable();
                    break;
                case 4:
                    System.out.println("Logged out.");
                    return; // Return from the method to go back to the login screen
                default:
                    System.out.println("Invalid choice. Please enter a valid option");
                    break;
            }
        }
    }
    private static void playGame() {
        Dealer dealer = new Dealer();
        User user = new User();

        dealer.initialize();
        dealer.dealInitialHand();
        dealer.dealCommunityCards();

        user.receiveCard(dealer.getDealerHand()[0], 0);
        user.receiveCard(dealer.getDealerHand()[1], 1);

        user.displayHand();
        promptContinueOrFold();

        dealer.displayCommunityCards(3); // Display flop
        promptContinueOrFold();

        dealer.displayCommunityCards(4); // Display turn
        promptContinueOrFold();

        dealer.displayCommunityCards(5); // Display river

        HandRanking userHandRanking = dealer.evaluateHands(user);
        System.out.println("Your Hand: " + userHandRanking);
        updatePlayersTable(firstName, lastName, userHandRanking.toString());
        promptPlayerDetails(user, userHandRanking, lastName);
        updatePokerHands(userHandRanking);
        updateLeaderboard(lastName, userHandRanking);

        dealer.endGame();
    }

    private static void promptContinueOrFold() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("1. Continue");
        System.out.println("2. Fold");
        System.out.print("Enter your choice: ");

        while (!scanner.hasNextInt()) {
            System.out.println("Invalid choice. Please enter a valid option");
            System.out.print("Enter your choice: ");
            scanner.next();
        }
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                System.out.println("Continuing...");
                break;
            case 2:
                System.out.println("Folding...");
                break;
            default:
                System.out.println("Invalid choice. Continuing by default...");
                break;
        }
    }


    private static void promptPlayerDetails(User user, HandRanking userHandRanking, String lastName) {
        try {
            if (lastName == null) {
                System.out.println("Last name cannot be null. Unable to update leaderboard.");
                return;
            }

            String query = "SELECT * FROM leaderboard WHERE lastname = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, lastName);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Player found in leaderboard, update the hand column
                String updateQuery = "UPDATE leaderboard SET hand = ? WHERE lastname = ?";
                PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                updateStatement.setString(1, userHandRanking.toString());
                updateStatement.setString(2, lastName);
                updateStatement.executeUpdate();
            } else {
                // Player not found in leaderboard, insert a new row
                String insertQuery = "INSERT INTO leaderboard (lastname, hand, timesplayed) VALUES (?, ?, 1)";
                PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                insertStatement.setString(1, lastName);
                insertStatement.setString(2, userHandRanking.toString());
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Failed to update Leaderboard table: " + e.getMessage());
        }

        user.setHandRanking(userHandRanking);
    }


    private static void updatePokerHands(HandRanking userHandRanking) {
        try {
            String insertQuery = "INSERT INTO pokerhands (hand, rank, timesplayed) VALUES (?, ?, 1) " +
                    "ON CONFLICT (rank) DO UPDATE SET timesplayed = pokerhands.timesplayed + 1";
            PreparedStatement statement = connection.prepareStatement(insertQuery);
            statement.setString(1, userHandRanking.toString());
            statement.setInt(2, userHandRanking.getRank());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update Poker Hands table: " + e.getMessage());
        }
    }

    private static void updateLeaderboard(String lastName, HandRanking userHandRanking) {
        try {
            String insertQuery = "INSERT INTO leaderboard (lastname, hand, timesplayed) VALUES (?, ?, 1) " +
                    "ON CONFLICT (lastname) DO UPDATE SET timesplayed = leaderboard.timesplayed + 1";
            PreparedStatement statement = connection.prepareStatement(insertQuery);
            statement.setString(1, lastName);
            statement.setString(2, userHandRanking.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Failed to update Leaderboard table: " + e.getMessage());
        }
    }

    private static void viewPokerHandsTable() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM pokerhands");
            System.out.println("Poker Hands Table:");
            System.out.println("Rank\t\tHand\t\tTimes Played");
            while (resultSet.next()) {
                int rank = resultSet.getInt("rank");
                String hand = resultSet.getString("hand");
                int timesPlayed = resultSet.getInt("timesplayed");
                System.out.println(rank + "\t\t" + hand + "\t\t" + timesPlayed);
            }
            resultSet.close();
        } catch (SQLException e) {
            System.out.println("Failed to fetch data from Poker Hands table: " + e.getMessage());
        }
    }

    private static void viewLeaderboardTable() {
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT leaderboard.lastname, leaderboard.hand, leaderboard.timesplayed, theplayers.first_name " +
                    "FROM leaderboard " +
                    "JOIN theplayers ON leaderboard.lastname = theplayers.last_name";
            ResultSet resultSet = statement.executeQuery(query);

            System.out.println("Leaderboard Table (with Join):");
            System.out.println("Last Name\tFirst Name\tHand\t\tTimes Played");
            while (resultSet.next()) {
                String lastName = resultSet.getString("lastname");
                String firstName = resultSet.getString("first_name");
                String hand = resultSet.getString("hand");
                int timesPlayed = resultSet.getInt("timesplayed");
                System.out.println(lastName + "\t\t" + firstName + "\t\t" + hand + "\t\t" + timesPlayed);
            }
            resultSet.close();
        } catch (SQLException e) {
            System.out.println("Failed to fetch data from Leaderboard table: " + e.getMessage());
        }
    }

    private static void updatePlayersTable(String firstName, String lastName, String hand) {
        try {
            String selectQuery = "SELECT MAX(userid) AS max_userid FROM theplayers";
            Statement selectStatement = connection.createStatement();
            ResultSet resultSet = selectStatement.executeQuery(selectQuery);

            int nextUserId = 1;
            if (resultSet.next()) {
                Integer maxUserId = resultSet.getInt("max_userid");
                if (maxUserId != null) {
                    nextUserId = maxUserId + 1;
                }
            }

            String updateQuery = "UPDATE theplayers SET hand = ? WHERE first_name = ? AND last_name = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setString(1, hand);
            updateStatement.setString(2, firstName);
            updateStatement.setString(3, lastName);
            int rowsUpdated = updateStatement.executeUpdate();

            if (rowsUpdated == 0) {
                String insertQuery = "INSERT INTO theplayers (userid, first_name, last_name, hand) VALUES (?, ?, ?, ?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                insertStatement.setInt(1, nextUserId);
                insertStatement.setString(2, firstName);
                insertStatement.setString(3, lastName);
                insertStatement.setString(4, hand);
                insertStatement.executeUpdate();
            }

            resultSet.close();
        } catch (SQLException e) {
            System.out.println("Failed to update theplayers table: " + e.getMessage());
        }
    }



}