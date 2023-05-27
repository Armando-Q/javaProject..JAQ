// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Dealer dealer = new Dealer();
        User user = new User();

        dealer.initialize();
        dealer.dealInitialHand();
        dealer.dealCommunityCards();

        user.receiveCard(dealer.getDealerHand()[0],0);
        user.receiveCard(dealer.getDealerHand()[1],1);

        user.displayHand();
        promptContinueOrFold();

        dealer.displayCommunityCards(3); // Display flop
        promptContinueOrFold();

        dealer.displayCommunityCards(4); // Display turn
        promptContinueOrFold();

        dealer.displayCommunityCards(5); // Display river

        HandRanking userHandRanking = dealer.evaluateHands(user);
        System.out.println("Your Hand: " + userHandRanking);
        dealer.endGame();
    }

    private static void promptContinueOrFold() {
        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        while (choice != 1 && choice != 2){
            System.out.println("Continue (1) or Fold (2)");
            choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1){
                System.out.println("Continuing!!");
            }
            else if (choice == 2)
            {
                System.out.println("Folding..");
                System.exit((0));
            }
            else{
                System.out.println("Invalid input. Please enter a '1' or '2' to continue the game.");
            }
        }


    }
}