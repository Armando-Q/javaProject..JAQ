public class User {
    private String firstName;
    private String lastName;
    private Card[] userHand;
    private HandRanking handRanking;

    public User() {
        userHand = new Card[2];
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void receiveCard(Card card, int index) {
        userHand[index] = card;
    }

    public HandRanking getHandRanking(){
        return handRanking;
    }

    public void setHandRanking(HandRanking handRanking){
        this.handRanking = handRanking;
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