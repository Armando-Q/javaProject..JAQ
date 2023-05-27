public class User {
    private Card[] userHand;

    public User() {
        userHand = new Card[2];
    }

    public void receiveCard(Card card, int index) {
        userHand[index] = card;
    }

    public Card[] getUserHand() {
        return userHand;
    }


    public void displayHand() {
        System.out.println("Your Hand:");
        System.out.println("************");
        for (Card card : userHand) {
            System.out.println(card);
        }
    }
}