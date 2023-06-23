public enum HandRanking {
    ROYAL_FLUSH(1),
    STRAIGHT_FLUSH(2),
    FOUR_OF_A_KIND(3),
    FULL_HOUSE(4),
    FLUSH(5),
    STRAIGHT(6),
    THREE_OF_A_KIND(7),
    TWO_PAIR(8),
    ONE_PAIR(9),
    HIGH_CARD(10);

    private final int rank;

    HandRanking(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

    public static HandRanking fromString(String currentHand) {
        for (HandRanking ranking : HandRanking.values()) {
            if (ranking.name().equalsIgnoreCase(currentHand)) {
                return ranking;
            }
        }
        return null; // Or throw an exception if the input doesn't match any hand ranking
    }
}