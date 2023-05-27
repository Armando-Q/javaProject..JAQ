import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class Dealer {
    private Deck deck;
    private Card[] dealerHand;
    private Card[] communityCards;
    private Random random;

    public Card[] getDealerHand(){
        return dealerHand;
    }
    public void initialize() {
        deck = new Deck();
        deck.shuffle();
        dealerHand = new Card[2];
        communityCards = new Card[5];
        random = new Random();
    }

    public void dealInitialHand() {
        dealerHand[0] = deck.dealCard();
        dealerHand[1] = deck.dealCard();
    }

    public void dealCommunityCards() {
        Card[] allCards = new Card[5];

        for (int i = 0; i < 5; i++) {
            allCards[i] = deck.dealCard();
        }

        System.arraycopy(allCards, 0, communityCards, 0, 5);
    }


    public void displayCommunityCards(int stage) {
        if(stage == 3){
            System.out.println("The Flop");
            System.out.println("****************");
        }
        else if (stage == 4) {
            System.out.println("The Turn");
            System.out.println("****************");
        }
        else{
            System.out.println("The River");
            System.out.println("****************");
        }

        for (int i = 0; i < stage; i++) {
            System.out.println(communityCards[i]);
        }
    }

    public HandRanking evaluateHands(User user) {
        Card[] userHand = user.getUserHand();
        Card[] allCards = new Card[7];
        System.arraycopy(userHand, 0, allCards, 0, 2);
        System.arraycopy(communityCards, 0, allCards, 2, 5);

        return evaluateHandRanking(allCards);

    }

    private HandRanking evaluateHandRanking(Card[] hand) {
        if (isRoyalFlush(hand)) {
            return HandRanking.ROYAL_FLUSH;
        } else if (isStraightFlush(hand)) {
            return HandRanking.STRAIGHT_FLUSH;
        } else if (isFourOfAKind(hand)) {
            return HandRanking.FOUR_OF_A_KIND;
        } else if (isFullHouse(hand)) {
            return HandRanking.FULL_HOUSE;
        } else if (isFlush(hand)) {
            return HandRanking.FLUSH;
        } else if (isStraight(hand)) {
            return HandRanking.STRAIGHT;
        } else if (isThreeOfAKind(hand)) {
            return HandRanking.THREE_OF_A_KIND;
        } else if (isTwoPair(hand)) {
            return HandRanking.TWO_PAIR;
        } else if (isOnePair(hand)) {
            return HandRanking.ONE_PAIR;
        } else {
            return HandRanking.HIGH_CARD;
        }
    }

    private boolean isRoyalFlush(Card[] hand) {
        return isStraightFlush(hand) && containsAce(hand) && containsKing(hand);
    }

    private boolean isStraightFlush(Card[] hand) {
        return isFlush(hand) && isStraight(hand);
    }

    private boolean isFourOfAKind(Card[] hand) {
        for (int i = 0; i < hand.length; i++) {
            int count = 1;
            for (int j = i + 1; j< hand.length; j++) {
                if (hand[i].getRank().equals(hand[j].getRank())) {
                    count++;
                }
            }
            if (count == 4) {
                return true;
            }
        }
        return false;
    }

    private boolean isFullHouse(Card[] hand) {
        return isThreeOfAKind(hand) && isOnePair(hand);
    }

    private boolean isFlush(Card[] hand) {
        String suit = hand[0].getSuit();
        for (int i = 1; i < hand.length; i++) {
            if (!hand[i].getSuit().equals(suit)) {
                return false;
            }
        }
        return true;
    }

    private boolean isStraight(Card[] hand) {
        sortHandByRank(hand);
        for (int i = 0; i < hand.length - 1; i++) {
            int currentRank = getRankValue(hand[i].getRank());
            int nextRank = getRankValue(hand[i + 1].getRank());
            if (currentRank + 1 != nextRank) {
                return false;
            }
        }
        return true;
    }

    private void sortHandByRank(Card[] hand){
        Arrays.sort(hand,(card1, card2)->{
            int rankValue1 = getRankValue(card1.getRank());
            int rankValue2 = getRankValue(card2.getRank());
            return Integer.compare(rankValue1, rankValue2);
        });
    }

    private boolean isThreeOfAKind(Card[] hand) {
        for (int i = 0; i < hand.length; i++) {
            int count = 1;
            for (int j = 0; j < hand.length; j++) {
                if (j != i && hand[i].getRank().equals(hand[j].getRank())) {
                    count++;
                }
            }
            if (count == 3) {
                return true;
            }
        }
        return false;
    }

    private boolean isTwoPair(Card[] hand) {
        int pairCount = 0;
        Set<String> ranks = new HashSet<>();

        for (Card card: hand){
            String rank = card.getRank();
            if(ranks.contains(rank)){
                pairCount++;
                ranks.remove(rank);
            }
            else{
                ranks.add(rank);
            }
        }
        return pairCount >=2;
    }

    private boolean isOnePair(Card[] hand) {
        for (int i = 0; i < hand.length; i++) {
            int count = 1;
            for (int j = 0; j < hand.length; j++) {
                if (j != i && hand[i].getRank().equals(hand[j].getRank())) {
                    count++;
                }
            }
            if (count == 2) {
                return true;
            }
        }
        return false;
    }

    // check if the hand contains an Ace
    private boolean containsAce(Card[] hand) {
        for (Card card : hand) {
            if (card.getRank().equals("Ace")) {
                return true;
            }
        }
        return false;
    }

    //  check if the hand contains a King
    private boolean containsKing(Card[] hand) {
        for (Card card : hand) {
            if (card.getRank().equals("King")) {
                return true;
            }
        }
        return false;
    }

   // method to get the numerical value of a rank
    private int getRankValue(String rank) {
        switch (rank) {
            case "Ace":
                return 14;
            case "King":
                return 13;
            case "Queen":
                return 12;
            case "Jack":
                return 11;
            default:
                return Integer.parseInt(rank);
        }
    }



    public void endGame() {
        System.out.println("Game over.");
    }
}