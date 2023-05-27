import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Deck {
    private final List<Card> cards;
    private final Random random;

    public Deck() {
        cards = new ArrayList<>();
        random = new Random();
        initializeDeck();
    }

    private void initializeDeck() {
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"};

        for (String suit : suits) {
            for (String rank : ranks) {
                Card card = new Card(suit, rank);
                cards.add(card);
            }
        }
    }

    public void shuffle() {
        for (int i = cards.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            swapCards(i, j);
        }
    }

    private void swapCards(int index1, int index2) {
        Card temp = cards.get(index1);
        cards.set(index1, cards.get(index2));
        cards.set(index2, temp);
    }

    public Card dealCard() {
        if (cards.isEmpty()) {
            return null; // Deck is empty
        }
        return cards.remove(0);
    }
}